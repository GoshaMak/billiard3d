package org.wocy.camera

import org.wocy.primitive.Vec3f

class RayTracingCamera(
    position: Vec3f,
    forward: Vec3f,
    up: Vec3f,
    right: Vec3f = forward.cross(up).apply { normalize() },
    screenWidth: Double,
    screenHeight: Double,
) : Camera(position, forward, up, right, screenWidth, screenHeight) {

    val horFov = 200
    val vertFov = 140

    // width > height
    val imgAspectRatio = screenWidth / screenHeight

    override fun generateRay(px: Int, py: Int, screenWidth: Int, screenHeight: Int): Ray {
        /*
                val ndcx = (px + 0.5f) / screenWidth
                val ndcy = (py + 0.5f) / screenHeight

                val screenx = 2.0 * ndcx - 1.0
                val screeny = 1.0 - 2.0 * ndcy

                // width > height
                val imgAspectRatio = screenWidth.toDouble() / screenHeight.toDouble()

                var camerax = screenx * imgAspectRatio
                var cameray = screeny

                //        camerax *= tan(Math.toRadians(vertFov / 2.0))
                //        cameray *= tan(Math.toRadians(horFov / 2.0))

                val o = Vec3f() * cameraToWorld
                val p = Vec3f(camerax, cameray, -1.0) * cameraToWorld
                val dir = (p - o).apply { normalize() }

                return Ray(o, dir)
        */
        val o = Vec3f(cameraToWorld[3, 0], cameraToWorld[3, 1], cameraToWorld[3, 2])
        val dir = (Vec3f(
            (2.0 * ((px + 0.5f) / screenWidth) - 1.0) * imgAspectRatio, /*\*tan(Math.toRadians(vertFov / 2.0))*/
            (1.0 - 2.0 * ((py + 0.5f) / screenHeight)), /*\*tan(Math.toRadians(horFov / 2.0))*/
            -1.0,
        ) * cameraToWorld - o).apply { normalize() }

        return Ray(o, dir)
    }
}
