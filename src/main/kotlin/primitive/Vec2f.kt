package org.wocy.primitive

import kotlin.math.acos
import kotlin.math.sqrt

class Vec2f(
    var x: Float,
    var y: Float,
) {

    companion object {

        val EPSILON = 1e-8

        fun normalized(vec: Vec2f): Vec2f {
            val len = vec.length()
            return Vec2f(vec.x / len, vec.y / len)
        }

        fun squaredLength(start: Vec2f, end: Vec2f): Float {
            val dx = end.x - start.x
            val dy = end.y - start.y
            return dx * dx + dy * dy
        }

        fun length(start: Vec2f, end: Vec2f): Float = sqrt(squaredLength(start, end))
    }

    constructor() : this(0f, 0f)

    constructor(x: Float) : this(x, x)

    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())

    operator fun plus(other: Vec2f): Vec2f = Vec2f(x + other.x, y + other.y)

    operator fun plusAssign(other: Vec2f) {
        x += other.x
        y += other.y
    }

    operator fun minus(other: Vec2f): Vec2f = Vec2f(x - other.x, y - other.y)

    operator fun minusAssign(other: Vec2f) {
        x -= other.x
        y -= other.y
    }

    operator fun times(other: Float): Vec2f = Vec2f(x * other, y * other)

    operator fun timesAssign(other: Float) {
        x *= other
        y *= other
    }

    operator fun times(other: Vec2f): Vec2f = Vec2f(x * other.x, y * other.y)

    operator fun timesAssign(other: Vec2f) {
        x *= other.x
        y *= other.y
    }

    operator fun times(other: Mat2f): Vec2f {
        return Vec2f(x * other[0, 0] + y * other[1, 0], x * other[0, 1] + y * other[1, 1])
    }

    operator fun unaryMinus(): Vec2f = Vec2f(-x, -y)

    operator fun invoke(o: Vec2f) {
        x = o.x
        y = o.y
    }

    fun normalize() {
        val len = length()
        x /= len
        y /= len
    }

    fun dot(vec: Vec2f): Float = x * vec.x + y * vec.y

    fun squaredLength(): Float = dot(this)

    fun length(): Float = sqrt(squaredLength())

    override fun toString(): String {
        return "Vec2f($x, $y)"
    }

    fun isRightHanded(o: Vec2f): Boolean = (x * o.y - y * o.x) >= 0f

    fun rangleWithOX(): Double {
        var rangle = acos(x / length()).toDouble()
        if (y < 0f) {
            rangle *= -1f
        }
        return rangle
    }
}
