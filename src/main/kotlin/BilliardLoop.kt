package org.wocy

import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.PixelFormat
import javafx.scene.paint.Color
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.wocy.camera.RayTracingCamera
import org.wocy.light.DistantLight
import org.wocy.model.BaseModel
import org.wocy.model.BowlingBall
import org.wocy.model.Table
import org.wocy.primitive.Mat4f
import org.wocy.primitive.Vec3d
import org.wocy.primitive.Vec3f
import kotlin.math.cos
import kotlin.math.sin

class BilliardLoop(
    private val gc: GraphicsContext,
    private val width: Int,
    private val height: Int,
) : AnimationTimer() {

    private val logger = KotlinLogging.logger {}
    private val writer = gc.pixelWriter
    private val format = PixelFormat.getIntArgbPreInstance()

    // 1 unit = 1 centimeter
    val camera = RayTracingCamera(
        position = Vec3f(0f, 0f, 0f),
        forward = Vec3f(0f, 0f, -1f),
        up = Vec3f(0f, 1f, 0f),
        screenWidth = width.toDouble(),
        screenHeight = height.toDouble(),
    )
    val light = DistantLight(
        lightToWorld = Mat4f.rotationOX(-60f),
        color = Color.WHITE,
        intensity = 1f,
    )
    val cueLength: Float = 120f
    val models = mutableListOf<BaseModel>(
        BowlingBall(Vec3f(0f, 3.5f, -10f), 3.5f, Color.RED, Vec3d()),
        BowlingBall(Vec3f(0f, 1.5f, 10f), 1.5f, Color.YELLOW, Vec3d()),
        Table(
            Color.GREEN,
            (440f - 2f * cueLength - 2f * 10f),
            (350f - 2f * cueLength - 2f * 10f),
            0.618f * 3.5f
        ),
    )
    private val renderer = Renderer(
        width,
        height,
        camera,
        light,
        models,
    )

    private var lastTime = 0L
    private val frameIntervalNs = 1_000_000_000L / 60  // 60 fps // nanoseconds
    private val frameIntervalS = frameIntervalNs / 1_000_000_000.0 // 60 fps // seconds

    override fun handle(now: Long) {
        if (lastTime == 0L) {
            lastTime = now
            camera.apply {
                //                move(0f, 100f, 0f)
                //                rotateOX(-90f)
                move(-20f, 8f, 10f)
                rotateOY(-90f)
            }
            return
        }
        if (now - lastTime >= frameIntervalNs) {
            applyPhysics()
            drawFrame()
            customAnimation()
            //            val fps = 1_000_000_000 / (now - lastTime)
            //            logger.info { "FPS: $fps" }
            lastTime = now
        }
    }

    override fun stop() {
        super.stop()
        renderer.destroy()
    }

    private var deg = 0

    private fun customAnimation() {
        return
        light.rotateOZ(1.toFloat())

        deg = (deg + 1) % 360
        val x = 10f * cos(Math.toRadians(deg.toDouble())).toFloat()
        val z = 10f * sin(Math.toRadians(deg.toDouble())).toFloat() - 10f

        val dx = x - camera.position.x
        val dz = z - camera.position.z

        camera.rotateOY(1f)
        camera.move(dx, 0f, dz)
    }

    private fun applyPhysics() {
        updatePosition(frameIntervalS)
        collide(frameIntervalS)
    }

    private fun collide(dt: Double) {
        for (i in 0 until models.size - 1) {
            for (j in i + 1 until models.size) {
                models[i].collide(models[j], dt)
            }
        }
    }

    private fun updatePosition(dt: Double) = models.forEach { m ->
        m.updatePosition(dt)
    }

    // желательно не делать вычисления в UI потоке
    private fun drawFrame() = runBlocking {
        launch {
            val pixels = renderer.render()
            writer.setPixels(0, 0, width, height, format, pixels, 0, width)
        }
    }
}
