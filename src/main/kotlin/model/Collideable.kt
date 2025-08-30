package org.wocy.model

interface Collideable {
    fun collide(o: BaseModel, dt: Double)

    fun isColliding(o: BaseModel): Boolean = false
}
