package org.wocy.light

import javafx.scene.paint.Color
import org.wocy.primitive.Mat4f
import org.wocy.primitive.Vec3f

abstract class Light(
    var lightToWorld: Mat4f, // надо для патриот буфера
    var color: Color,
    var intensity: Float,
) {

    abstract fun getDirectionAndIntensity(p: Vec3f): Pair<Vec3f, Float>

    open fun translate(dx: Float, dy: Float, dz: Float) {
        lightToWorld = Mat4f.translation(dx, dy, dz) * lightToWorld
    }

    open fun rotateOX(deg: Float) {
        lightToWorld = Mat4f.rotationOX(deg) * lightToWorld
    }

    open fun rotateOY(deg: Float) {
        lightToWorld = Mat4f.rotationOY(deg) * lightToWorld
    }

    open fun rotateOZ(deg: Float) {
        lightToWorld = Mat4f.rotationOZ(deg) * lightToWorld
    }
}
