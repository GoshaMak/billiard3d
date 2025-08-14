package org.wocy.camera

import org.wocy.primitive.Mat44f
import org.wocy.primitive.Vec3f

abstract class Camera(
    var position: Vec3f,
    var forward: Vec3f,
    var up: Vec3f,
    right: Vec3f,
    val screenWidth: Double,
    val screenHeight: Double,
    /*
        abstract var leftPlane: Float
        abstract var rightPlane: Float
        abstract var bottomPlane: Float
        abstract var topPlane: Float
        abstract var nearPlane: Float
        abstract var farPlane: Float

        abstract var viewMatrix: Mat4 // в мировое пространство камеры
        abstract var projectionMatrix: Mat4 // из камеры в отсечённый объём

        // угол обзора камеры в World Space
        abstract var width: Int
        abstract var height: Int
    */
) {
    protected var cameraToWorld: Mat44f = Mat44f(right, up, forward, position)
    /*
        protected var cameraToWorld: Mat44f = Mat44f(
            Vec3f(1f, 0f, 0f),
            Vec3f(0f, 1f, 0f),
            Vec3f(0f, 0f, -1f),
            Vec3f(0f, 3f, 0f),
        )
    */

    abstract fun generateRay(px: Int, py: Int, screenWidth: Int, screenHeight: Int): Ray

    fun move(dx: Float, dy: Float, dz: Float) {
        cameraToWorld = Mat44f.translation(dx, dy, dz) * cameraToWorld
    }

    fun moveLeft() {
        move(-1f, 0f, 0f)
    }

    fun moveRight() {
        move(1f, 0f, 0f)
    }

    fun moveBackward() {
        move(0f, 0f, 1f)
    }

    fun moveForward() {
        move(0f, 0f, -1f)
    }

    fun moveDownward() {
        move(0f, -1f, 0f)
    }

    fun moveUpward() {
        move(0f, 1f, 0f)
    }

    fun rotateOX(deg: Float) {
        cameraToWorld = Mat44f.rotationOX(deg) * cameraToWorld
    }

    fun rotateOY(deg: Float) {
        cameraToWorld = Mat44f.rotationOY(deg) * cameraToWorld
    }

    fun rotateOZ(deg: Float) {
        cameraToWorld = Mat44f.rotationOZ(deg) * cameraToWorld
    }

    override fun toString(): String {
        return "Camera(position: $position, forward: $forward, up: $up)"
    }
}