package org.wocy.model

import javafx.scene.paint.Color
import org.wocy.camera.Ray
import org.wocy.primitive.Vec2f
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
import org.wocy.visitor.BaseModelVisitor

class BaseModelComposite(color: Color, albedo: Float, mass: Float, velocity: Vec2f) :
    BaseModel(
        color, albedo, mass,
        velocity,
    ) {

    val cueLength: Float = 120f
    private val models: MutableList<BaseModel> = mutableListOf(
        BowlingBall(Vec3f(0f, 3.5f, -10f), 3.5f, Color.RED, Vec2f()),
        BowlingBall(Vec3f(1.5f, 3.5f, 10f), 3.5f, Color.YELLOW, Vec2f()),
        Table(
            Color.GREEN,
            (440f - 2f * cueLength - 2f * 10f),
            (350f - 2f * cueLength - 2f * 10f),
            0.618f * 3.5f
        ),
    )

    operator fun get(index: Int): BaseModel = models[index]

    val size: Int get() = models.size

    override fun accept(visitor: BaseModelVisitor) = visitor.visit(this)

    override fun intersect(ray: Ray): Vertex? = null

    override fun iterator(): Iterator<BaseModel> = models.iterator()
}
