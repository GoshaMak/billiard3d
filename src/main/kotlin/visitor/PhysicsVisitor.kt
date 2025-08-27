package org.wocy.visitor

import org.wocy.model.BaseModelComposite
import org.wocy.model.BowlingBall
import org.wocy.model.Table

class PhysicsVisitor(
    val dt: Double,
) : BaseModelVisitor() {

    override fun visit(o: BowlingBall) {
        if (o.velocity.x == 0f && o.velocity.y == 0f) {
            return
        }

        var vx = o.velocity.x - o.ax * dt
        var vy = o.velocity.y - o.ay * dt

        val t =
            if (vx * o.velocity.x < 0f || vy * o.velocity.y < 0f) { // по всем осям останавливается одновременно
                vx = 0.0; vy = 0.0
                o.velocity.x / o.ax
            } else {
                dt
            }

        val dlx = (o.velocity.x * t - o.ax * t * t / 2f).toFloat()
        val dly = (o.velocity.y * t - o.ay * t * t / 2f).toFloat()

        // dlx и dly поменяны местами так, что воображаемая ось OX (в которой двигается шарик)
        // расположена вдоль реальной оси OZ, а воображаемая ось OY (в которой двигается шарик)
        // расположена вдоль реальной оси OX, чтобы СК была правосторонней, для удобства расчётов
        o.move(dly * 100f, 0f, dlx * 100f)
        o.velocity.x = vx.toFloat(); o.velocity.y = vy.toFloat()
    }

    override fun visit(o: Table) {}

    override fun visit(o: BaseModelComposite) {
        for (comp in o) {
            comp.accept(this)
        }
    }
}
