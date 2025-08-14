package org.wocy.light

import javafx.scene.paint.Color
import org.wocy.primitive.Mat44f
import org.wocy.primitive.Vec3f

abstract class Light(
    var lightToWorld: Mat44f, // надо для патриот буфера
    var color: Color,
    var intensity: Float,
) {
    abstract fun getDirectionAndIntensity(p: Vec3f): Pair<Vec3f, Float>

    open fun translate(dx: Float, dy: Float, dz: Float) {
        lightToWorld = Mat44f.translation(dx, dy, dz) * lightToWorld
    }

    open fun rotateOX(deg: Float) {
        lightToWorld = Mat44f.rotationOX(deg) * lightToWorld
    }

    open fun rotateOY(deg: Float) {
        lightToWorld = Mat44f.rotationOY(deg) * lightToWorld
    }

    open fun rotateOZ(deg: Float) {
        lightToWorld = Mat44f.rotationOZ(deg) * lightToWorld
    }
}