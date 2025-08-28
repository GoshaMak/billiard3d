package org.wocy.model

import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.scene.paint.Color
import org.wocy.camera.Ray
import org.wocy.primitive.Mat2f
import org.wocy.primitive.Vec2f
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class BowlingBall(
    val center: Vec3f,
    val radius: Float, // TODO think abt Int
    color: Color,
    velocity: Vec2f,
    mass: Float = 0.3f, // кг
) : BaseModel(color, 0.14f, mass, velocity) {
    //    val albedo = 0.14 // 0.14 to 0.22 // albedo = rho
    val logger = KotlinLogging.logger {}
    val radius2 = radius * radius
    var rangle = 0.0
        set(value) {
            field = value
            ax = acceleration * cos(rangle)
            ay = acceleration * sin(rangle)
        }

    val nu = 0.02f
    val g = 9.81f
    val cueHitTimeS = 310e-6f // s
    val acceleration = nu * g
    var ax = acceleration * cos(rangle)
    var ay = acceleration * sin(rangle)

    constructor(center: Vec3f, radius: Int, color: Color, mass: Float, velocity: Vec2f) : this(
        center,
        radius.toFloat(),
        color,
        velocity,
        mass,
    )

    operator fun Double.times(vec: Vec3f): Vec3f = Vec3f(vec.x * this, vec.y * this, vec.z * this)

    operator fun Float.timesAssign(vec: Vec3f) {
        vec.x *= this
        vec.y *= this
        vec.z *= this
    }

    override fun intersect(ray: Ray): Vertex? {
        val oc = ray.origin - center
        if (oc.squaredLength() <= radius2) { // ray inside sphere
            return null
        }
        val a = ray.direction.dot(ray.direction)
        val b = 2.0 * oc.dot(ray.direction)
        val c = oc.dot(oc) - radius2
        val d = b * b - 4 * a * c

        // пересечения нет
        if (d < 0.0) {
            return null
        }

        val sqrtDisc = sqrt(d)
        val t = (-b - sqrtDisc) / (2.0 * a)
        //        val t2 = (-b + sqrtDisc) / (2.0 * a)

        /*
                val t = when {
                    t1 >= 0.0 -> t1
                    t2 >= 0.0 -> t2
                    else      -> return null // пересечение на прямой, а не на линии
                }
        */

        if (t < 0.0) {
            return null
        }
        val hitPoint = ray.origin + ray.direction * t.toFloat()
        val norm = (hitPoint - center).apply { normalize() }
        return Vertex(hitPoint, norm)
    }

    fun cueHit(force: Float, rangle: Double) {
        // angle - угол между OZ и OX
        this.rangle = rangle
        velocity.x = force * cos(rangle).toFloat() * cueHitTimeS / mass // x==z
        velocity.y = force * sin(rangle).toFloat() * cueHitTimeS / mass // y==x
    }

    fun stop() {
        velocity.x = 0f
        velocity.y = 0f
    }

    // dt - s
    override fun updatePosition(dt: Double) {
        if (velocity.x == 0f && velocity.y == 0f) return

        var t = dt
        var vx = velocity.x - ax * dt
        var vy = velocity.y - ay * dt

        if (vx * velocity.x < 0f || vy * velocity.y < 0f) { // по всем осям останавливается одновременно
            t = velocity.x / ax
            vx = 0.0; vy = 0.0
        }

        val dlx = (velocity.x * t - ax * t * t / 2f).toFloat()
        val dly = (velocity.y * t - ay * t * t / 2f).toFloat()

        // dlx и dly поменяны местами так, что воображаемая ось OX (в которой двигается шарик)
        // расположена вдоль реальной оси OZ, а воображаемая ось OY (в которой двигается шарик)
        // расположена вдоль реальной оси OX, чтобы СК была правосторонней, для удобства расчётов
        move(dly * 100f, 0f, dlx * 100f)
        velocity.x = vx.toFloat(); velocity.y = vy.toFloat()
    }

    fun move(dx: Float, dy: Float, dz: Float) {
        center.x += dx
        center.y += dy
        center.z += dz
    }

    override fun collide(o: BaseModel) {
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
            is Table       -> {
                o.collide(this)
            }
            else           -> {}
        }
    }

    override fun isColliding(o: BaseModel): Boolean {
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

    private fun constructNewAxis(o: BowlingBall): Pair<Vec2f, Vec2f> {
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
}