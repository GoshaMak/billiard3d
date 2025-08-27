package org.wocy.primitive

// think about storing it as transposed to optimize cache loads during multiplication
class Mat2f {

    companion object {

        fun identity(): Mat2f {
            val mtr = FloatArray(4) { i: Int ->
                when (i) {
                    0, 2 -> 1.0f
                    else -> 0.0f
                }
            }

            return Mat2f(mtr)
        }
    }

    val size = 2
    private val mtr: FloatArray

    private constructor(mtr: FloatArray) {
        this.mtr = mtr
    }

    constructor() {
        mtr = FloatArray(4) { 0.0f }
    }

    constructor(v1: Float, v2: Float, v3: Float, v4: Float) {
        mtr = FloatArray(4) { i ->
            when (i) {
                0    -> v1
                1    -> v2
                2    -> v3
                else -> v4
            }
        }
    }

    operator fun get(i: Int, j: Int): Float = mtr[i * size + j]

    operator fun set(i: Int, j: Int, value: Float) {
        mtr[i * size + j] = value
    }
}
