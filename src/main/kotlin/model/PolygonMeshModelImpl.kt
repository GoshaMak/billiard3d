package org.wocy.model

import org.wocy.primitive.Face
import org.wocy.primitive.Vec3f
import org.wocy.primitive.Vertex
import kotlin.math.cos
import kotlin.math.sin

class PolygonMeshModelImpl {
    private val vertices: MutableList<Vertex> = ArrayList()
    private val faces: MutableList<Face> = ArrayList()

    fun generateSphereVertices(radius: Double, segments: Int, rings: Int): List<Vertex> {
        val vertices = mutableListOf<Vertex>()

        for (i in 0..rings) {
            val v = i.toDouble() / rings
            val theta = v * Math.PI

            for (j in 0..segments) {
                val u = j.toDouble() / segments
                val phi = u * 2.0 * Math.PI

                val x = sin(theta) * cos(phi)
                val y = cos(theta)
                val z = sin(theta) * sin(phi)

                val normal = Vec3f(x, y, z)
                val position = Vec3f(x * radius, y * radius, z * radius)

                vertices.add(Vertex(position, normal))
            }
        }

        return vertices
    }

    fun generateSphereFaces(segments: Int, rings: Int): List<Face> {
        val faces = mutableListOf<Face>()

        for (i in 0 until rings) {
            for (j in 0 until segments) {
                val row1 = i * (segments + 1)
                val row2 = (i + 1) * (segments + 1)

                val a = row1 + j
                val b = row1 + j + 1
                val c = row2 + j
                val d = row2 + j + 1

                // Первый треугольник
                faces.add(Face(a, c, b))
                // Второй треугольник
                faces.add(Face(b, c, d))
            }
        }

        return faces
    }
}