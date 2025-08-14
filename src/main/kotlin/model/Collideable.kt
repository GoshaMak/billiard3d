package org.wocy.model

interface Collideable {
    fun collide(obj: BaseModel) {
        if (!isColliding(obj)) {
            return
        }
    }

    private fun isColliding(obj: BaseModel): Boolean {
        return false
    }
}