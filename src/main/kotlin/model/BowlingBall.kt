package org.wocy.model

import javafx.scene.paint.Color
import org.wocy.camera.Ray
import org.wocy.primitive.Vec2f
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
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
    val radius2 = radius * radius
    var angle = 0.0
        set(value) {
            field = value
            ax = acceleration * cos(Math.toRadians(angle))
            ay = acceleration * sin(Math.toRadians(angle))
        }

    val nu = 0.02f
    val g = 9.81f
    val cueHitTimeS = 310e-6f // s
    val acceleration = nu * g
    var ax = acceleration * cos(Math.toRadians(angle))
    var ay = acceleration * sin(Math.toRadians(angle))

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

    fun cueHit(force: Float, angle: Double) {
        this.angle = angle
        velocity.x = force * cos(Math.toRadians(angle)).toFloat() * cueHitTimeS / mass
        velocity.y = force * sin(Math.toRadians(angle)).toFloat() * cueHitTimeS / mass
    }

    fun stop() {
        velocity.x = 0f
        velocity.y = 0f
    }

    // dt - s
    override fun update(dt: Double) {
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

        move(dlx * 100f, 0f, dly * 100f)
        velocity.x = vx.toFloat(); velocity.y = vy.toFloat()
    }

    fun move(dx: Float, dy: Float, dz: Float) {
        center.x += dx
        center.y += dy
        center.z += dz
    }
}