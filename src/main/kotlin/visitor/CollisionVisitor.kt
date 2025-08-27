package org.wocy.visitor

import org.wocy.model.BaseModel
import org.wocy.model.BaseModelComposite
import org.wocy.model.BowlingBall
import org.wocy.model.Table
import org.wocy.primitive.Mat2f
import org.wocy.primitive.Vec2f
import kotlin.math.abs

class CollisionVisitor : BaseModelVisitor() {

    override fun visit(o: BowlingBall) {}

    override fun visit(o: Table) {}

    override fun visit(o: BaseModelComposite) {
        for (i in 0 until o.size - 1) {
            val m = o[i]
            for (j in i + 1 until o.size) {
                when (m) {
                    is BowlingBall -> m.collide(o[j])
                    is Table       -> m.collide(o[j])
                    else           -> {}
                }
            }
        }
    }

    private fun BowlingBall.collide(o: BaseModel) {
        when (o) {
            is BowlingBall -> {
                if (!isColliding(o)) {
                    return
                }
                val (oxBallsRelated, oyBallsRelated) = constructNewAxis(o)
                val u = Mat2f(oxBallsRelated.x, oyBallsRelated.x, oxBallsRelated.y, oyBallsRelated.y)
                val ut = Mat2f(oxBallsRelated.x, oxBallsRelated.y, oyBallsRelated.x, oyBallsRelated.y)
                val v1BallsRelated = velocity * u
                val v2BallsRelated = o.velocity * u
                val (v1newBallsRelated, v2newBallsRelated) = computeNewVelocities(
                    v1BallsRelated,
                    v2BallsRelated,
                    mass,
                    o.mass
                )
                val v1new = v1newBallsRelated * ut
                val v2new = v2newBallsRelated * ut

                velocity(v1new)
                rangle = velocity.rangleWithOX()
                o.velocity(v2new)
                o.rangle = o.velocity.rangleWithOX()
            }
            is Table       -> o.collide(this)
            else           -> {}
        }
    }

    private fun BowlingBall.isColliding(o: BaseModel): Boolean {
        return when (o) {
            is BowlingBall -> {
                val dist2 = (center.x - o.center.x) * (center.x - o.center.x) +
                        (center.z - o.center.z) * (center.z - o.center.z)
                if (dist2 > (radius + o.radius) * (radius + o.radius)) {
                    return false
                }
                val vrel = velocity - o.velocity
                if (velocity.x > 0f || velocity.y > 0f) {
                    return (vrel.x > 0f) || (vrel.y > 0f)
                }
                return (vrel.x < 0f) || (vrel.y < 0f)
            }
            else           -> false
        }
    }

    private fun BowlingBall.constructNewAxis(o: BowlingBall): Pair<Vec2f, Vec2f> {
        val c2c1 = Vec2f(o.center.z - center.z, o.center.x - center.x)
        val oxnew = Vec2f.normalized(c2c1)
        /*
                val oynew = Vec2f()
                if (abs(oxnew.x) <= Vec2f.EPSILON) {
                    oynew.y = 0f
                    // oxnew.y == 1f
                    if (oxnew.y > 0f) {
                        oynew.x = -1f
                    } else {
                        oynew.x = 1f
                    }
                } else {
                    oynew.y = 1f
                    oynew.x = -oxnew.y / oxnew.x
                    if (!oxnew.isRightHanded(oynew)) {
                        oynew.y = -1f
                        oynew.x = -oynew.x
                    }
                }
                oynew.normalize()
                return Pair(oxnew, oynew)
        */
        var nx: Float
        var ny = 1f
        if (abs(oxnew.x) < Vec2f.EPSILON) {
            nx = 1f; ny = 0f
        } else if (abs(oxnew.y) < Vec2f.EPSILON) {
            nx = 0f
        } else {
            nx = -(oxnew.y * ny) / oxnew.x
        }
        val oynew = Vec2f(nx, ny).apply { normalize() }
        if (!oxnew.isRightHanded(oynew)) {
            oynew.x *= -1f
            oynew.y *= -1f
        }
        return Pair(oxnew, oynew)
    }

    private fun computeNewVelocities(v1: Vec2f, v2: Vec2f, m1: Float, m2: Float): Pair<Vec2f, Vec2f> {
        val v1xafter = (m1 * v1.x - m2 * v1.x + 2f * m2 * v2.x) / (m1 + m2)
        val v2xafter = (m2 * v2.x - m1 * v2.x + 2f * m1 * v1.x) / (m1 + m2)
        return Pair(Vec2f(v1xafter, v1.y), Vec2f(v2xafter, v2.y))
    }

    private fun Table.collide(o: BaseModel) {
        if (o !is BowlingBall) {
            return
        }
        if (o.center.x - o.radius <= -width / 2f) {
            if (o.velocity.y < 0f) {
                o.velocity.y *= -1f
                o.rangle = o.velocity.rangleWithOX()
            }
        } else if (o.center.x + o.radius >= width / 2f) {
            if (o.velocity.y > 0f) {
                o.velocity.y *= -1f
                o.rangle = o.velocity.rangleWithOX()
            }
        }
        if (o.center.z - o.radius <= -length / 2f) {
            if (o.velocity.x < 0f) {
                o.velocity.x *= -1f
                o.rangle = o.velocity.rangleWithOX()
            }
        } else if (o.center.z + o.radius >= length / 2f) {
            if (o.velocity.x > 0f) {
                o.velocity.x *= -1f
                o.rangle = o.velocity.rangleWithOX()
            }
        }
    }
}
