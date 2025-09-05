package org.wocy.model

import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.scene.paint.Color
import org.wocy.camera.Ray
import org.wocy.primitive.Vec3d
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// 4 x 3 meters
class Table(
    color: Color,
    val length: Float,
    val width: Float,
    val borderHeight: Float,
) : BaseModel(color, 0.18f, Float.MAX_VALUE, Vec3d()) {

    val logger = KotlinLogging.logger {}
    private val vertices = listOf<Vec3f>(
        // поверхность, по которой катаются шары
        Vec3f(-width / 2f, 0f, length / 2f),
        Vec3f(width / 2f, 0f, length / 2f),
        Vec3f(width / 2f, 0f, -length / 2f),
        Vec3f(-width / 2f, 0f, -length / 2f),

        // левый бортик
        Vec3f(-width / 2f, borderHeight, length / 2f),
        Vec3f(-width / 2f, 0f, length / 2f),
        Vec3f(-width / 2f, 0f, -length / 2f),
        Vec3f(-width / 2f, borderHeight, -length / 2f),

        // правый бортик
        Vec3f(width / 2f, borderHeight, -length / 2f),
        Vec3f(width / 2f, 0f, -length / 2f),
        Vec3f(width / 2f, 0f, length / 2f),
        Vec3f(width / 2f, borderHeight, length / 2f),

        // ближний бортик
        Vec3f(width / 2f, borderHeight, length / 2f),
        Vec3f(width / 2f, 0f, length / 2f),
        Vec3f(-width / 2f, 0f, length / 2f),
        Vec3f(-width / 2f, borderHeight, length / 2f),

        // дальний бортик
        Vec3f(-width / 2f, borderHeight, -length / 2f),
        Vec3f(-width / 2f, 0f, -length / 2f),
        Vec3f(width / 2f, 0f, -length / 2f),
        Vec3f(width / 2f, borderHeight, -length / 2f),
    )
    private val center = Vec3f(0f, borderHeight / 2f, 0f)
    private val radius = max(width / 2f, max(length / 2f, borderHeight / 2f))
    private val radius2 = radius * radius
    private val hitFactor = -0.95f

    // TODO apply dt
    override fun collide(o: BaseModel, dt: Double) {
        if (o !is BowlingBall) {
            return
        }
        if (o.center.x - o.radius <= -width / 2f) {
            if (o.velocity.x < 0f) {
                o.rollBack(dt)
                val timeLeft = resolveHorizontalPenetration(
                    o,
                    o.center.x,
                    -width / 2f + o.radius,
                    o.velocity.x,
                    dt,
                )
                o.velocity.x *= hitFactor
                o.rangleOZOX = o.velocity.rangleOZOX()
            }
        } else if (o.center.x + o.radius >= width / 2f) {
            if (o.velocity.x > 0f) {
                o.rollBack(dt)
                val timeLeft = resolveHorizontalPenetration(
                    o,
                    o.center.x,
                    width / 2f - o.radius,
                    o.velocity.x,
                    dt,
                )
                o.velocity.x *= hitFactor
                o.rangleOZOX = o.velocity.rangleOZOX()
            }
        }

        if (o.center.z - o.radius <= -length / 2f) {
            if (o.velocity.z < 0f) {
                o.rollBack(dt)
                val timeLeft = resolveHorizontalPenetration(
                    o,
                    o.center.z,
                    -length / 2f + o.radius,
                    o.velocity.z,
                    dt,
                )
                o.velocity.z *= hitFactor
                o.rangleOZOX = o.velocity.rangleOZOX()
            }
        } else if (o.center.z + o.radius >= length / 2f) {
            if (o.velocity.z > 0f) {
                o.rollBack(dt)
                val timeLeft = resolveHorizontalPenetration(
                    o,
                    o.center.z,
                    length / 2f - o.radius,
                    o.velocity.z,
                    dt,
                )
                o.velocity.z *= hitFactor
                o.rangleOZOX = o.velocity.rangleOZOX()
            }
        }

        if (o.center.y < o.radius) {
            if (o.velocity.y < -Vec3f.EPSILON) {
                // falling down
                o.rollBack(dt)
                /*
                                if (abs(o.velocity.y) < Vec3d.EPSILON) {
                                    o.updatePosition(dt)
                                    o.velocity.y = 0.0
                                    o.acceleration.y = 0.0
                                    o.center.y = o.radius
                                } else {
                */
                val timeLeft = resolveVerticalPenetration(o, dt)
                o.velocity.y *= hitFactor
                //                }
            }
        }
    }

    private fun resolveHorizontalPenetration(
        b: BowlingBall,
        startPos: Float,
        endPos: Float,
        v: Double,
        dt: Double,
    ): Double {
        val dtnew = (endPos - startPos) / v
        if (/*dtnew > dt || */dtnew <= 0.0) {
            return 0.0
        }
        b.center += b.velocity * dtnew
        return dt - dtnew
    }

    private fun resolveVerticalPenetration(o: BowlingBall, dt: Double): Double {
        assert(o.center.y > o.radius) { "somehow ball is higher than its radius" }

        val d = o.velocity.y * o.velocity.y - 2 * o.acceleration.y * (o.radius - o.center.y)
        if (d < 0.0) {
            return 0.0
        }
        val dtnew = min((o.velocity.y - d) / o.acceleration.y, (o.velocity.y + d) / o.acceleration.y)
        if (dtnew <= 0.0) {
            return 0.0
        }
        o.updatePosition(dtnew)
        return dt - dtnew
    }

    override fun intersect(ray: Ray): Vertex? {
        /*
                if (!isIntersectsSphere(ray)) {
                    return null
                }
        */
        var pos: Vec3f? = null
        var ind = -1
        var minDistance = Float.MAX_VALUE
        var dist: Float
        for (i in 0 until vertices.size step 4) {
            val tmp1 = rayIntersectsTriangle(ray, vertices[i], vertices[i + 1], vertices[i + 2])
            if (tmp1 != null) {
                dist = Vec3f.squaredLength(ray.origin, tmp1)
                if (dist < minDistance) {
                    minDistance = dist
                    pos = tmp1
                    ind = i
                }
            }
            val tmp2 = rayIntersectsTriangle(ray, vertices[i], vertices[i + 2], vertices[i + 3])
            if (tmp2 != null) {
                dist = Vec3f.squaredLength(ray.origin, tmp2)
                if (dist < minDistance) {
                    minDistance = dist
                    pos = tmp2
                    ind = i
                }
            }
        }
        if (pos == null) {
            return null
        }
        val n =
            (vertices[ind + 1] - vertices[ind]).cross(vertices[ind + 3] - vertices[ind]).apply { normalize() }
        return Vertex(pos, n)
    }

    private fun isIntersectsSphere(ray: Ray): Boolean {
        val oc = ray.origin - center
        if (oc.squaredLength() <= radius2) { // ray inside sphere
            return true
        }
        val a = ray.direction.dot(ray.direction)
        val b = 2.0 * oc.dot(ray.direction)
        val c = oc.dot(oc) - radius2
        val d = b * b - 4 * a * c

        // пересечения нет
        if (d < 0.0) {
            return false
        }
        return true
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

    override fun updatePosition(dt: Double) {}
}
