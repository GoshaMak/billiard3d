package org.wocy.model

import javafx.scene.paint.Color
import org.wocy.primitive.Vec2f
import org.wocy.visitor.BaseModelVisitor

abstract class BaseModel(
    var color: Color, // The RGB color of the light (values range from [0,1]).
    val albedo: Float, // 0..1
    val mass: Float,
    val velocity: Vec2f,
) : Iterable<BaseModel>, Intersectable {

    abstract fun accept(visitor: BaseModelVisitor)

    override fun iterator(): Iterator<BaseModel> {
        return object : Iterator<BaseModel> {
            override fun next(): BaseModel = this@BaseModel

            override fun hasNext(): Boolean = false
        }
    }
}
