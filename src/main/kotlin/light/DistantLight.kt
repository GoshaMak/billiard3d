package org.wocy.light

import javafx.scene.paint.Color
import org.wocy.primitive.Mat44f
import org.wocy.primitive.Vec3f

// not affected by translation
class DistantLight(
    lightToWorld: Mat44f,
    color: Color, // The RGB color of the light (values range from [0,1]).
    intensity: Float, // probably 0..1
) : Light(lightToWorld, color, intensity) {
    private var direction = (Vec3f(0f, 0f, -1f) * lightToWorld).apply { normalize() }
        set(value) {
            field = value
            field.normalize()
        }

    override fun getDirectionAndIntensity(p: Vec3f): Pair<Vec3f, Float> = Pair(direction, intensity)

    override fun translate(dx: Float, dy: Float, dz: Float) {}

    override fun rotateOX(deg: Float) {
        super.rotateOX(deg)
        direction.timesAssign(Mat44f.rotationOX(deg))
    }

    override fun rotateOY(deg: Float) {
        super.rotateOY(deg)
        direction.timesAssign(Mat44f.rotationOY(deg))
    }

    override fun rotateOZ(deg: Float) {
        super.rotateOZ(deg)
        direction.timesAssign(Mat44f.rotationOZ(deg))
    }
}