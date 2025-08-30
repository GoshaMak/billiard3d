package org.wocy.model

import javafx.scene.paint.Color
import org.wocy.primitive.Vec3d
import org.wocy.primitive.Vec3f

abstract class BaseModel(
    var color: Color, // The RGB color of the light (values range from [0,1]).
    val albedo: Float, // 0..1
    val mass: Float,
    velocity: Vec3d,
) : Intersectable, Collideable {

    val velocity = velocity

    abstract fun updatePosition(dt: Double)
}
