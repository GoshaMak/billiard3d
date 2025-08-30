package org.wocy.model

import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.scene.paint.Color
import org.wocy.camera.Ray
import org.wocy.primitive.Mat3f
import org.wocy.primitive.Vec3d
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
import kotlin.math.*

class BowlingBall(
    val center: Vec3f,
    val radius: Float, // TODO think abt Int
    color: Color,
    velocity: Vec3d,
    mass: Float = 0.3f, // кг
) : BaseModel(color, 0.14f, mass, velocity) {

    //    val albedo = 0.14 // 0.14 to 0.22 // albedo = rho
    val logger = KotlinLogging.logger {}
    val radius2 = radius * radius
    var rangleOZOX = 0.0
        set(value) {
            field = value
            //            az = acoef * cos(value)
            //            ax = acoef * sin(value)
            acceleration.z = nuG * cos(value) * (abs(velocity.z) > Vec3d.EPSILON).toInt()
            acceleration.x = nuG * sin(value) * (abs(velocity.x) > Vec3d.EPSILON).toInt()
        }

    val nu = 0.02f // table friction
    val g = 9.81f  // gravity acceleration
    val cueHitTimeS = 310e-6f // s
    val nuG = nu * g

    val acceleration = Vec3d(0.0, g.toDouble(), 0.0) // TODO change to double

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
        if (t < 0.0) {
            return null
        }

        val hitPoint = ray.origin + ray.direction * t.toFloat()
        val norm = (hitPoint - center).apply { normalize() }
        return Vertex(hitPoint, norm)
    }

    fun cueHit(force: Float, rangle: Double) {
        this.rangleOZOX = rangle
        velocity.z = force * cos(rangle) * cueHitTimeS / mass // x
        velocity.x = force * sin(rangle) * cueHitTimeS / mass // y
        velocity.y = 0.0                                          // z
    }

    fun stop() {
        velocity.x = 0.0
        velocity.y = 0.0
        velocity.z = 0.0
    }

    // dt - s
    override fun updatePosition(dt: Double) {
        if (abs(velocity.z) <= Vec3d.EPSILON && abs(velocity.x) <= Vec3d.EPSILON) {
            stop()
            return
        }

        var t = dt
        var vz = velocity.z - acceleration.z * dt
        var vx = velocity.x - acceleration.x * dt
        var vy = velocity.y - acceleration.y * dt
        if (vz * velocity.z < 0.0 || vx * velocity.x < 0.0) { // по всем осям останавливается одновременно
            t = velocity.z / acceleration.z
            vz = 0.0; vx = 0.0; vy = 0.0
        }

        val dlz = (velocity.z * t - acceleration.z * t * t / 2.0).toFloat()
        val dlx = (velocity.x * t - acceleration.x * t * t / 2.0).toFloat()
        val dly = (velocity.y * t - acceleration.y * t * t / 2.0).toFloat()

        move(dlx * 100f, dly * 100f, dlz * 100f)
        velocity.z = vz; velocity.x = vx; velocity.y = vy
    }

    fun move(dx: Float, dy: Float, dz: Float) {
        center.x += dx
        center.y += dy
        center.z += dz
    }

    override fun collide(o: BaseModel, dt: Double) {
        when (o) {
            is BowlingBall -> {
                if (!isColliding(o, dt)) {
                    return
                }
                logger.info { "collision detected" }
                val (oxBallsRelated, oyBallsRelated, ozBallsRelated) = constructNewAxis(o)
                val u = Mat3f(oxBallsRelated, oyBallsRelated, ozBallsRelated)
                val ut = Mat3f.transposed(u)
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

//                v1new.y = 0.0; v2new.y = 0.0 // FIXME

                velocity(v1new)
                rangleOZOX = velocity.rangleOZOX()
                o.velocity(v2new)
                o.rangleOZOX = o.velocity.rangleOZOX()
            }
            is Table       -> o.collide(this, dt)
            else           -> {}
        }
    }

    fun isColliding(o: BaseModel, dt: Double): Boolean {
        return when (o) {
            is BowlingBall -> {
                val dist2 = Vec3f.squaredLength(o.center, center)
                if (dist2 > (radius + o.radius) * (radius + o.radius)) {
                    return false
                }
                val timeLeft = resolvePenetration(this, o, dt)
                // FIXME
/*
                if (timeLeft > Vec3d.EPSILON) {
                    updatePosition(timeLeft)
                    o.updatePosition(timeLeft)
                }
*/
                return true
            }
            else           -> false
        }
    }

    private fun resolvePenetration(b1: BowlingBall, b2: BowlingBall, dt: Double): Double {
        // FIXME dtnew doesn't count acceleration
        b1.rollBack(dt)
        b2.rollBack(dt)
        val c = b1.center - b2.center
        val u = b1.velocity - b2.velocity
        val r = b1.radius + b2.radius
        val cu = u.dot(c)
        val u2 = u.dot(u)
        var d = cu * cu - u2 * (c.dot(c) - r * r)
        if (d <= Vec3d.EPSILON || u2 <= Vec3d.EPSILON) {
            return 0.0
        }
        d = sqrt(d)
        //  val dtnew = if (u2 < 1.0) (-cu + d) / u2 else (-cu + d) / u2
        val dtnew = min((-cu - d) / u2, (-cu + d) / u2)
        if (/*dtnew > dt || */dtnew <= 0.0) {
            return 0.0
        }
        //        b1.updatePosition(dtnew.toDouble())
        //        b2.updatePosition(dtnew.toDouble())
        b1.center += b1.velocity * dtnew
        b2.center += b2.velocity * dtnew
        return dt - dtnew
    }

    // TODO optimize
    fun rollBack(dt: Double) {
        val u = velocity + acceleration * dt
        val c = center - (u * dt - acceleration * dt * dt * 0.5) * 100.0
        velocity(u)
        center(c)
    }

    private fun constructNewAxis(o: BowlingBall): Triple<Vec3f, Vec3f, Vec3f> {
        val c1c2 = o.center - center
        val oxnew = Vec3f.normalized(c1c2)
        val oynew = constructOY(oxnew)
        val oznew = oxnew.cross(oynew)
        return Triple(oxnew, oynew, oznew)
    }

    private fun constructOY(ox: Vec3f): Vec3f {
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
        var nz: Float
        var nx = 1f
        if (abs(ox.z) < Vec3f.EPSILON) {
            nz = 1f; nx = 0f
        } else if (abs(ox.x) < Vec3f.EPSILON) {
            nz = 0f
        } else {
            nz = -(ox.x * nx) / ox.z
        }
        val oy = Vec3f(nx, 0f, nz).apply { normalize() }
        if (!ox.isRightHanded(oy)) {
            oy.z *= -1f
            oy.x *= -1f
        }
        return oy
    }

    private fun computeNewVelocities(v1: Vec3d, v2: Vec3d, m1: Float, m2: Float): Pair<Vec3d, Vec3d> {
        val v1xafter = (m1 * v1.x - m2 * v1.x + 2f * m2 * v2.x) / (m1 + m2)
        val v2xafter = (m2 * v2.x - m1 * v2.x + 2f * m1 * v1.x) / (m1 + m2)
        return Pair(Vec3d(v1xafter, v1.y, v1.z), Vec3d(v2xafter, v2.y, v2.z))
    }
}

fun Boolean.toInt() = if (this) 1 else 0
