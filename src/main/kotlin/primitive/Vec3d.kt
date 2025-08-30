package org.wocy.primitive

import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sign
import kotlin.math.sqrt

class Vec3d(
    var x: Double,
    var y: Double,
    var z: Double,
) {

    companion object {

        const val EPSILON = 1e-12

        fun normalized(vec: Vec3d): Vec3d {
            val len = vec.length()
            if (len > EPSILON) {
                return Vec3d(vec.x / len, vec.y / len, vec.z / len)
            }
            return Vec3d(0.0, 0.0, 0.0)
        }

        fun squaredLength(start: Vec3d, end: Vec3d): Double {
            val dx = end.x - start.x
            val dy = end.y - start.y
            val dz = end.z - start.z
            return dx * dx + dy * dy + dz * dz
        }

        fun length(start: Vec3d, end: Vec3d): Double = sqrt(squaredLength(start, end))
    }

    constructor() : this(0.0, 0.0, 0.0)

    constructor(x: Double) : this(x, x, x)

    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())

    operator fun plus(other: Vec3d): Vec3d = Vec3d(x + other.x, y + other.y, z + other.z)

    operator fun plusAssign(other: Vec3d) {
        x += other.x
        y += other.y
        z += other.z
    }

    operator fun minus(other: Vec3d): Vec3d = Vec3d(x - other.x, y - other.y, z - other.z)

    operator fun minusAssign(other: Vec3d) {
        x -= other.x
        y -= other.y
        z -= other.z
    }

    operator fun times(other: Float): Vec3d = Vec3d(x * other, y * other, z * other)

    operator fun timesAssign(other: Float) {
        x *= other
        y *= other
        z *= other
    }

    operator fun times(o: Double) = Vec3d(x * o, y * o, z * o)

    operator fun timesAssign(other: Double) {
        x *= other
        y *= other
        z *= other
    }

    operator fun times(other: Vec3d): Vec3d = Vec3d(x * other.x, y * other.y, z * other.z)

    operator fun timesAssign(other: Vec3d) {
        x *= other.x
        y *= other.y
        z *= other.z
    }

    operator fun times(o: Mat3f): Vec3d = Vec3d(
        x * o[0, 0] + y * o[1, 0] + z * o[2, 0],
        x * o[0, 1] + y * o[1, 1] + z * o[2, 1],
        x * o[0, 2] + y * o[1, 2] + z * o[2, 2],
    )

    operator fun times(other: Mat4f): Vec3d = Vec3d(
        x * other[0, 0] + y * other[1, 0] + z * other[2, 0] + other[3, 0],
        x * other[0, 1] + y * other[1, 1] + z * other[2, 1] + other[3, 1],
        x * other[0, 2] + y * other[1, 2] + z * other[2, 2] + other[3, 2],
    )

    operator fun timesAssign(other: Mat4f) {
        val px = x;
        val py = y;
        val pz = z
        x = px * other[0, 0] + py * other[1, 0] + pz * other[2, 0] + other[3, 0]
        y = px * other[0, 1] + py * other[1, 1] + pz * other[2, 1] + other[3, 1]
        z = px * other[0, 2] + py * other[1, 2] + pz * other[2, 2] + other[3, 2]
    }

    operator fun unaryMinus(): Vec3d = Vec3d(-x, -y, -z)

    operator fun invoke(o: Vec3d) {
        x = o.x
        y = o.y
        z = o.z
    }

    fun normalize() {
        val len = length()
        if (len > EPSILON) {
            x /= len
            y /= len
            z /= len
        } else {
            x = 0.0
            y = 0.0
            z = 0.0
        }
    }

    fun dot(vec: Vec3d): Double = x * vec.x + y * vec.y + z * vec.z

    fun dot(vec: Vec3f): Double = x * vec.x + y * vec.y + z * vec.z

    fun cross(o: Vec3d): Vec3d = Vec3d(
        y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x
    )

    fun squaredLength(): Double = dot(this)

    fun length(): Double = sqrt(squaredLength())

    fun isRightHanded(o: Vec3d): Boolean = (x * o.y - y * o.x) >= 0.0

    fun rangleOZOX(): Double {
        if (abs(z) < EPSILON) {
            return Math.PI / 2.0 * sign(x)
        }
        var rangle = atan(x / z)
        if (z < 0.0) {
            rangle += Math.PI
        }
        return rangle
    }

    override fun toString(): String {
        return "Vec3d($x, $y, $z)"
    }
}
