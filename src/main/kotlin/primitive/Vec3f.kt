package org.wocy.primitive

import kotlin.math.sqrt

class Vec3f(
    var x: Float,
    var y: Float,
    var z: Float,
) {
    companion object {
        private val EPSILON = 1e-8
        fun normalized(vec: Vec3f): Vec3f {
            val len = vec.length()
            return Vec3f(vec.x / len, vec.y / len, vec.z / len)
        }

        fun squaredLength(start: Vec3f, end: Vec3f): Float {
            val dx = end.x - start.x
            val dy = end.y - start.y
            val dz = end.z - start.z
            return dx * dx + dy * dy + dz * dz
        }

        fun length(start: Vec3f, end: Vec3f): Float = sqrt(squaredLength(start, end))
    }

    constructor() : this(0.0f, 0.0f, 0.0f)

    constructor(x: Float) : this(x, x, x)

    constructor(x: Int, y: Int, z: Int) : this(x.toFloat(), y.toFloat(), z.toFloat())

    constructor(x: Double, y: Double, z: Double) : this(x.toFloat(), y.toFloat(), z.toFloat())

    operator fun plus(other: Vec3f): Vec3f = Vec3f(x + other.x, y + other.y, z + other.z)

    operator fun plusAssign(other: Vec3f) {
        x += other.x
        y += other.y
        z += other.z
    }

    operator fun minus(other: Vec3f): Vec3f = Vec3f(x - other.x, y - other.y, z - other.z)

    operator fun minusAssign(other: Vec3f) {
        x -= other.x
        y -= other.y
        z -= other.z
    }

    operator fun times(other: Float): Vec3f = Vec3f(x * other, y * other, z * other)

    operator fun timesAssign(other: Float) {
        x *= other
        y *= other
        z *= other
    }

    operator fun times(other: Vec3f): Vec3f = Vec3f(x * other.x, y * other.y, z * other.z)

    operator fun timesAssign(other: Vec3f) {
        x *= other.x
        y *= other.y
        z *= other.z
    }

    operator fun times(other: Mat44f): Vec3f = Vec3f(
        x * other[0, 0] + y * other[1, 0] + z * other[2, 0] + other[3, 0],
        x * other[0, 1] + y * other[1, 1] + z * other[2, 1] + other[3, 1],
        x * other[0, 2] + y * other[1, 2] + z * other[2, 2] + other[3, 2],
    )

    operator fun timesAssign(other: Mat44f) {
        val px = x;
        val py = y;
        val pz = z
        x = px * other[0, 0] + py * other[1, 0] + pz * other[2, 0] + other[3, 0]
        y = px * other[0, 1] + py * other[1, 1] + pz * other[2, 1] + other[3, 1]
        z = px * other[0, 2] + py * other[1, 2] + pz * other[2, 2] + other[3, 2]
    }

    operator fun unaryMinus(): Vec3f = Vec3f(-x, -y, -z)

    fun normalize() {
        val len = length()
        x /= len
        y /= len
        z /= len
    }

    fun dot(vec: Vec3f): Float = x * vec.x + y * vec.y + z * vec.z

    fun cross(o: Vec3f): Vec3f = Vec3f(
        y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x
    )

    fun squaredLength(): Float = dot(this)

    fun length(): Float = sqrt(squaredLength())

    /*
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Vec3) return false
            return (abs(x - other.x) < EPSILON)
                    && (abs(y - other.y) < EPSILON)
                    && (abs(z - other.z) < EPSILON)
        }
    */

    override fun toString(): String {
        return "Vec3f($x, $y, $z)"
    }
}