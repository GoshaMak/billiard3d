package org.wocy

import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.PixelFormat
import javafx.scene.paint.Color
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.wocy.camera.RayTracingCamera
import org.wocy.light.DistantLight
import org.wocy.model.BaseModel
import org.wocy.model.BowlingBall
import org.wocy.model.Table
import org.wocy.primitive.Mat44f
import org.wocy.primitive.Vec2f
import org.wocy.primitive.Vec3f
import kotlin.math.cos
import kotlin.math.sin

class BilliardLoop(
    private val gc: GraphicsContext,
    private val width: Int,
    private val height: Int,
) : AnimationTimer() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val writer = gc.pixelWriter
    private val format = PixelFormat.getIntArgbPreInstance()

    // 1 unit = 1 centimeter
    val camera = RayTracingCamera(
        position = Vec3f(0f, 3f, 0f),
        forward = Vec3f(0f, 0f, -1f),
        up = Vec3f(0f, 1f, 0f),
        screenWidth = width.toDouble(),
        screenHeight = height.toDouble(),
    )
    val light = DistantLight(
        //        Mat44f.identity(),
        lightToWorld = Mat44f.rotationOX(-60f),
        //        Mat44f.translation(1.5f, 3.1f, -3f),
        color = Color.WHITE,
        intensity = 1f,
    )
    val cueLength: Float = 120f
    val models = mutableListOf<BaseModel>(
        BowlingBall(Vec3f(0f, 3.5f, -10f), 3.5f, Color.RED, Vec2f()),
        BowlingBall(Vec3f(1.5f, 3.5f, 10f), 3.5f, Color.YELLOW, Vec2f()),
        Table(Color.GREEN, (440f - 2f * cueLength - 2f * 10f), (350f - 2f * cueLength - 2f * 10f)),
    )
    private val renderer = Renderer(
        width.toInt(),
        height.toInt(),
        camera,
        light,
        models,
    )

    private var lastTime = 0L
    private val frameIntervalNs = 1_000_000_000L / 60  // 60 fps // nanoseconds
    private val frameIntervalS = frameIntervalNs / 1_000_000_000.0 // 60 fps // seconds

    override fun handle(now: Long) {
        if (lastTime == 0L) {
            lastTime = now
            camera.move(0f, 50f, 0f)
            camera.rotateOX(-90f)
            return
        }
        if (now - lastTime >= frameIntervalNs) {
            val fps = 1_000_000_000 / (now - lastTime)
            applyPhysics()
            drawFrame()
            customAnimation()
            logger.info { "FPS: $fps" }
            lastTime = now
        }
    }

    override fun stop() {
        super.stop()
        renderer.destroy()
    }

    private var deg = 0

    private fun customAnimation() {
        return
        light.rotateOZ(1.toFloat())

        deg = (deg + 1) % 360
        val x = 10f * cos(Math.toRadians(deg.toDouble())).toFloat()
        val z = 10f * sin(Math.toRadians(deg.toDouble())).toFloat() - 10f

        val dx = x - camera.position.x
        val dz = z - camera.position.z

        camera.rotateOY(1f)
        camera.move(dx, 0f, dz)
    }

    private fun applyPhysics() {
        for (model in models) {
            model.update(frameIntervalS)
        }
    }

    // желательно не делать вычисления в UI потоке
    private fun drawFrame() = runBlocking {
        launch {
            val pixels = renderer.render()
            writer.setPixels(0, 0, width, height, format, pixels, 0, width)
        }
    }
}