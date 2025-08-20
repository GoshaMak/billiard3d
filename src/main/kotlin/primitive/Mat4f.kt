package org.wocy.primitive

import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

// think about storing it as transposed to optimize cache loads during multiplication
class Mat4f {
    companion object {
        fun identity(): Mat4f {
            val mtr = FloatArray(16) { i: Int ->
                when (i) {
                    0, 5, 10, 15 -> 1.0f
                    else         -> 0.0f
                }
            }

            return Mat4f(mtr)
        }

        fun translation(x: Float, y: Float, z: Float): Mat4f {
            val mtr = FloatArray(16) { i: Int ->
                when (i) {
                    0, 5, 10, 15 -> 1.0f
                    12           -> x
                    13           -> y
                    14           -> z
                    else         -> 0.0f
                }
            }

            return Mat4f(mtr)
        }

        fun rotationOX(deg: Float): Mat4f {
            val sin_ = sin(toRadians(deg.toDouble())).toFloat()
            val cos_ = cos(toRadians(deg.toDouble())).toFloat()
            val mtr = FloatArray(16) { i: Int ->
                when (i) {
                    0, 15 -> 1.0f
                    5     -> cos_
                    6     -> sin_
                    9     -> -sin_
                    10    -> cos_
                    else  -> 0.0f
                }
            }

            return Mat4f(mtr)
        }

        fun rotationOY(deg: Float): Mat4f {
            val sin_ = sin(toRadians(deg.toDouble())).toFloat()
            val cos_ = cos(toRadians(deg.toDouble())).toFloat()
            val mtr = FloatArray(16) { i: Int ->
                when (i) {
                    5, 15 -> 1.0f
                    0     -> cos_
                    2     -> -sin_
                    8     -> sin_
                    10    -> cos_
                    else  -> 0.0f
                }
            }

            return Mat4f(mtr)
        }

        fun rotationOZ(deg: Float): Mat4f {
            val sin_ = sin(toRadians(deg.toDouble())).toFloat()
            val cos_ = cos(toRadians(deg.toDouble())).toFloat()
            val mtr = FloatArray(16) { i: Int ->
                when (i) {
                    10, 15 -> 1.0f
                    0      -> cos_
                    1      -> sin_
                    4      -> -sin_
                    5      -> cos_
                    else   -> 0.0f
                }
            }

            return Mat4f(mtr)
        }

        fun scale(sx: Float, sy: Float, sz: Float): Mat4f {
            val mtr = FloatArray(16) { i: Int ->
                when (i) {
                    0    -> sx
                    5    -> sy
                    10   -> sz
                    15   -> 1.0f
                    else -> 0.0f
                }
            }

            return Mat4f(mtr)
        }
    }

    val size = 4
    private val mtr: FloatArray

    private constructor(mtr: FloatArray) {
        this.mtr = mtr
    }

    constructor() {
        mtr = FloatArray(16) { 0.0f }
    }

    constructor(right: Vec3f, up: Vec3f, forward: Vec3f, position: Vec3f) {
        mtr = FloatArray(16) { i: Int ->
            when (i) {
                0    -> right.x
                1    -> right.y
                2    -> right.z
                4    -> up.x
                5    -> up.y
                6    -> up.z
                8    -> -forward.x
                9    -> -forward.y
                10   -> -forward.z
                12   -> position.x
                13   -> position.y
                14   -> position.z
                15   -> 1.0f
                else -> 0.0f
            }
        }
    }

    operator fun get(i: Int, j: Int): Float = mtr[i * size + j]

    operator fun set(i: Int, j: Int, value: Float) {
        mtr[i * size + j] = value
    }

    operator fun times(o: Mat4f): Mat4f {
        val res = Mat4f()
        for (i in 0 until size) {
            for (j in 0 until size) {
                res[i, j] = 0.0f
                for (k in 0 until size) {
                    res[i, j] += this[i, k] * o[k, j]
                }
            }
        }
        return res
    }

    operator fun invoke(right: Vec3f, up: Vec3f, forward: Vec3f) {
        mtr[0] = right.x
        mtr[1] = right.y
        mtr[2] = right.z
        mtr[4] = up.x
        mtr[5] = up.y
        mtr[6] = up.z
        mtr[8] = -forward.x
        mtr[9] = -forward.y
        mtr[10] = -forward.z
        //        mtr[12] = position.x
        //        mtr[13] = position.y
        //        mtr[14] = position.z

        mtr[3] = 0f
        mtr[7] = 0f
        mtr[11] = 0f
        mtr[15] = 1.0f
    }

    operator fun invoke(right: Vec3f, up: Vec3f, forward: Vec3f, position: Vec3f) {
        mtr[0] = right.x
        mtr[1] = right.y
        mtr[2] = right.z
        mtr[4] = up.x
        mtr[5] = up.y
        mtr[6] = up.z
        mtr[8] = -forward.x
        mtr[9] = -forward.y
        mtr[10] = -forward.z
        mtr[12] = position.x
        mtr[13] = position.y
        mtr[14] = position.z

        mtr[3] = 0f
        mtr[7] = 0f
        mtr[11] = 0f
        mtr[15] = 1.0f
    }
}