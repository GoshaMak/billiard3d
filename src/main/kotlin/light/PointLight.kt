package org.wocy.light

import javafx.scene.paint.Color
import org.wocy.primitive.Mat4f
import org.wocy.primitive.Vec3f
import kotlin.math.min

// not affected by rotation
class PointLight(
    lightToWorld: Mat4f,
    color: Color,
    intensity: Float,
) : Light(lightToWorld, color, intensity) {

    private var position = Vec3f() * lightToWorld

    override fun getDirectionAndIntensity(p: Vec3f): Pair<Vec3f, Float> {
        val dir = (p - position)
        val r2 = dir.squaredLength()
        dir.normalize()
        val intens = intensity / (4.0 * Math.PI * r2)
        return Pair(dir, intens.toFloat())
    }

    // WARNING was done before figuring out camera matrix fields
    override fun translate(dx: Float, dy: Float, dz: Float) {
        super.translate(dx, dy, dz)
        position.timesAssign(Mat4f.translation(dx, dy, dz))
    }

    override fun rotateOX(deg: Float) {}

    override fun rotateOY(deg: Float) {}

    override fun rotateOZ(deg: Float) {}
}

operator fun Color.times(factor: Float): Color {
    return Color(min(1.0, red * factor), min(1.0, green * factor), min(1.0, blue * factor), opacity)
}
