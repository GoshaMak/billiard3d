package org.wocy.primitive

// think about storing it as transposed to optimize cache loads during multiplication
class Mat3f {

    companion object {

        fun identity(): Mat3f {
            val mtr = FloatArray(9) { i: Int ->
                when (i) {
                    0, 4, 8 -> 1.0f
                    else    -> 0.0f
                }
            }

            return Mat3f(mtr)
        }

        fun transposed(m: Mat3f): Mat3f {
            return Mat3f(m).apply { transpose() }
        }
    }

    val size = 3
    private val mtr: FloatArray

    private constructor(mtr: FloatArray) {
        this.mtr = mtr
    }

    constructor() {
        mtr = FloatArray(9) { 0.0f }
    }

    constructor(v1: Vec3f, v2: Vec3f, v3: Vec3f) {
        mtr = FloatArray(9) { i ->
            when (i) {
                0    -> v1.x
                1    -> v2.x
                2    -> v3.x
                3    -> v1.y
                4    -> v2.y
                5    -> v3.y
                6    -> v1.z
                7    -> v2.z
                8    -> v3.z
                else -> 0f
            }
        }
    }

    constructor(m: Mat3f) {
        this.mtr = FloatArray(9) { i -> m.mtr[i] }
    }

    operator fun get(i: Int, j: Int): Float = mtr[i * size + j]

    operator fun set(i: Int, j: Int, value: Float) {
        mtr[i * size + j] = value
    }

    fun transpose() {
        fun swap(i: Int, j: Int) {
            val tmp = get(i, j)
            set(i, j, get(j, i))
            set(j, i, tmp)
        }

        for (i in 0 until size) {
            for (j in i + 1 until size) {
                swap(i, j)
            }
        }
    }
}
