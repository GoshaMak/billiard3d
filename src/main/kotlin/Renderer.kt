package org.wocy

import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.scene.paint.Color
import kotlinx.coroutines.*
import org.wocy.camera.Camera
import org.wocy.camera.Ray
import org.wocy.light.Light
import org.wocy.model.BaseModel
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
import kotlin.math.max
import kotlin.math.min

class Renderer(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val camera: Camera,
    private val light: Light,
    private val models: MutableList<BaseModel>,
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    private val pixels = IntArray(screenWidth * screenHeight)

    private val bias = 0.001f
    private val bgColor = Color.WHITE // floor color

    fun destroy() {
        scope.cancel()
    }

    suspend fun render(): IntArray {
        (0 until screenHeight).map { py ->
            scope.launch {
                for (px in 0 until screenWidth) {
                    setPixel(px, py, castRay(camera.generateRay(px, py, screenWidth, screenHeight)))
                }
            }
        }.joinAll()
        return pixels
    }

    private fun castRay(primaryRay: Ray): Color {
        val (hitPoint, modelInd) = trace(primaryRay)
        if (hitPoint == null) {
            return bgColor
        }

        //        return simpleShading(hitPoint, models[modelInd])
        return simpleDotShading(hitPoint, models[modelInd])
    }

    // nearest intersection
    private fun trace(ray: Ray): Pair<Vertex?, Int> {
        var minDistance = Float.MAX_VALUE
        var hitPoint: Vertex? = null
        var modelInd = -1
        for (i in 0 until models.size) {
            val intersectionPoint: Vertex? = models[i].intersect(ray)
            if (intersectionPoint != null) {
                val dist = Vec3f.squaredLength(ray.origin, intersectionPoint.position)
                if (dist < minDistance) {
                    minDistance = dist
                    hitPoint = intersectionPoint
                    modelInd = i
                }
            }
        }
        return Pair(hitPoint, modelInd)
    }

    private fun simpleShading(hitPoint: Vertex, model: BaseModel): Color {
        return if (isBlockedFromLight(hitPoint)) {
            model.color * 0.0f
        } else {
            model.color * light.intensity
        }
    }

    private fun isBlockedFromLight(hitPoint: Vertex): Boolean {
        val shadowRay = Ray(
            hitPoint.position + hitPoint.normal * bias, // добавка, чтобы в себя не попадать
            -light.getDirectionAndIntensity(hitPoint.position).first,
        )
        var isShadowed = false
        for (i in 0 until models.size) {
            if (models[i].intersect(shadowRay) != null) { // && intersection.position != shadowRay.origin) {
                isShadowed = true
                break
            }
        }
        return isShadowed
    }

    private fun simpleDotShading(hitPoint: Vertex, model: BaseModel): Color {
        var (l, intensity) = light.getDirectionAndIntensity(hitPoint.position)
        l = -l
        val shadowRay = Ray(hitPoint.position + hitPoint.normal * bias, l)
        val isVisible = (trace(shadowRay).first == null)
        val diffuse = light.color * (isVisible
                * /*(model.albedo / Math.PI).toFloat() * */intensity
                * max(0.0f, hitPoint.normal.dot(l)))
        val c = model.color * diffuse
        return c
    }

    private fun setPixel(x: Int, y: Int, color: Color) {
        pixels[y * screenWidth + x] = argbFromColor(color)
    }

    private fun argbFromColor(color: Color): Int {
        /*
                val a = (color.opacity * 255.0).toInt() and 0xFF
                val r = (color.red * 255.0).toInt() and 0xFF
                val g = (color.green * 255.0).toInt() and 0xFF
                val b = (color.blue * 255.0).toInt() and 0xFF
                return (a shl 24) or (r shl 16) or (g shl 8) or b
        */
        return (((color.opacity * 255.0).toInt() and 0xFF) shl 24) or (((color.red * 255.0).toInt() and 0xFF) shl 16) or
                (((color.green * 255.0).toInt() and 0xFF) shl 8) or ((color.blue * 255.0).toInt() and 0xFF)
    }
}

operator fun Color.times(factor: Float): Color {
    return Color(min(1.0, red * factor), min(1.0, green * factor), min(1.0, blue * factor), opacity)
}

operator fun Color.times(o: Color): Color {
    return Color(red * o.red, green * o.green, blue * o.blue, opacity)
}

operator fun Boolean.times(o: Float): Float = if (this) o else 0f