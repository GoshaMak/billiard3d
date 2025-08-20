package org.wocy.camera

import org.wocy.primitive.Mat4f
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
    protected var cameraToWorld: Mat4f = Mat4f(right, up, forward, position)

    abstract fun generateRay(px: Int, py: Int, screenWidth: Int, screenHeight: Int): Ray

    fun move(dx: Float, dy: Float, dz: Float) {
        cameraToWorld = Mat4f.translation(dx, dy, dz) * cameraToWorld
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
        cameraToWorld = Mat4f.rotationOX(deg) * cameraToWorld
    }

    fun rotateOY(deg: Float) {
        cameraToWorld = Mat4f.rotationOY(deg) * cameraToWorld
    }

    fun rotateOZ(deg: Float) {
        cameraToWorld = Mat4f.rotationOZ(deg) * cameraToWorld
    }

    fun rotateAroundUp(deg: Float) {
        up = Vec3f(cameraToWorld[1, 0], cameraToWorld[1, 1], cameraToWorld[1, 2])
        forward = Vec3f(-cameraToWorld[2, 0], -cameraToWorld[2, 1], -cameraToWorld[2, 2]).apply { normalize() }
        forward = rotateAroundAxis(forward, up, Math.toRadians(deg.toDouble()))
        forward.normalize()
        val right = forward.cross(up).apply { normalize() }
        cameraToWorld(right, up, forward)
    }

    fun rotateAroundRight(deg: Float) {
        forward = Vec3f(-cameraToWorld[2, 0], -cameraToWorld[2, 1], -cameraToWorld[2, 2]).apply { normalize() }
        up = Vec3f(cameraToWorld[1, 0], cameraToWorld[1, 1], cameraToWorld[1, 2])
        val right = forward.cross(up).apply { normalize() }
        forward = rotateAroundAxis(forward, right, Math.toRadians(deg.toDouble()))
        up = rotateAroundAxis(up, right, Math.toRadians(deg.toDouble()))

        forward.normalize()
        up.normalize()
        right.normalize()
        cameraToWorld(right, up, forward)
    }

    private fun rotateAroundAxis(v: Vec3f, axisRaw: Vec3f, angleRad: Double): Vec3f {
        val a = Vec3f.normalized(axisRaw)
        val c = kotlin.math.cos(angleRad).toFloat()
        val s = kotlin.math.sin(angleRad).toFloat()
        // Rodrigues: v' = v c + (a×v) s + a (a·v)(1−c)
        return v * c + a.cross(v) * s + a * (a.dot(v) * (1f - c))
    }

    override fun toString(): String {
        return "Camera(position: $position, forward: $forward, up: $up)"
    }
}