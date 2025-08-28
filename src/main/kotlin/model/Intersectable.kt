package org.wocy.model

import org.wocy.camera.Ray
import org.wocy.primitive.Vertex

interface Intersectable {
    fun intersect(ray: Ray): Vertex?
}