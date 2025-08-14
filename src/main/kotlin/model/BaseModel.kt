package org.wocy.model

import javafx.scene.paint.Color
import org.wocy.primitive.Vec2f

abstract class BaseModel(
    var color: Color, // The RGB color of the light (values range from [0,1]).
    val albedo: Float, // 0..1
    val mass: Float,
    velocity: Vec2f,
) : Intersectable, Collideable {
    val velocity = velocity

    abstract fun update(dt: Double)
}