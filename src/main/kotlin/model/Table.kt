package org.wocy.model

import javafx.scene.paint.Color
import org.wocy.camera.Ray
import org.wocy.primitive.Vec2f
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
import kotlin.math.abs

// 4 x 3 meters
class Table(
    color: Color,
    val length: Float,
    val width: Float,
) : BaseModel(color, 0.18f, Float.MAX_VALUE, Vec2f()) {
    val p0 = Vec3f(-width / 2f, 0f, length / 2f)
    val p1 = Vec3f(width / 2f, 0f, length / 2f)
    val p2 = Vec3f(width / 2f, 0f, -length / 2f)
    val p3 = Vec3f(-width / 2f, 0f, -length / 2f)

    override fun intersect(ray: Ray): Vertex? {
        val pos = rayIntersectsTriangle(ray, p0, p1, p2)
                ?: rayIntersectsTriangle(ray, p2, p3, p0)
        if (pos == null) {
            return null
        }
        val n = (p1 - p0).cross(p2 - p1).apply { normalize() }
        return Vertex(pos, n)
    }

    private fun rayIntersectsTriangle(ray: Ray, v0: Vec3f, v1: Vec3f, v2: Vec3f): Vec3f? {
        val epsilon = 1e-8
        // Compute the plane's normal
        val v0v1 = v1 - v0
        val v0v2 = v2 - v0
        // No need to normalize
        val N = v0v1.cross(v0v2) // N

        // Step 1: Finding P

        // Check if the ray and plane are parallel
        val NdotRayDirection = N.dot(ray.direction)
        if (abs(NdotRayDirection) < epsilon)  // Almost 0
            return null // They are parallel, so they don't intersect!

        // Compute d parameter using equation 2
        val d = -N.dot(v0)

        // Compute t (equation 3)
        val t = -(N.dot(ray.origin) + d) / NdotRayDirection

        // Check if the triangle is behind the ray
        if (t < 0) return null // The triangle is behind

        // Compute the intersection point using equation 1
        val P = ray.origin + ray.direction * t

        // Step 2: Inside-Outside Test
        var Ne: Vec3f? // Vector perpendicular to triangle's plane

        // Test sidedness of P w.r.t. edge v0v1
        val v0p = P - v0
        Ne = v0v1.cross(v0p)
        if (N.dot(Ne) < 0.0) return null // P is on the right side

        // Test sidedness of P w.r.t. edge v2v1
        val v2v1 = v2 - v1
        val v1p = P - v1
        Ne = v2v1.cross(v1p)
        if (N.dot(Ne) < 0) return null // P is on the right side

        // Test sidedness of P w.r.t. edge v2v0
        val v2v0 = v0 - v2
        val v2p = P - v2
        Ne = v2v0.cross(v2p)
        if (N.dot(Ne) < 0) return null // P is on the right side

        return P // The ray hits the triangle
    }

    override fun update(dt: Double) {}
}