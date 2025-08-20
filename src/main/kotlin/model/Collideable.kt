package org.wocy.model

interface Collideable {
    fun collide(o: BaseModel)

    fun isColliding(o: BaseModel): Boolean = false
}