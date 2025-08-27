package org.wocy

import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.wocy.model.BowlingBall
import org.wocy.ui.CanvasPanel
import org.wocy.ui.ControlPanel

class BilliardApp : Application() {

    private val logger = KotlinLogging.logger {}
    lateinit var canvasPanel: CanvasPanel
    lateinit var controlPanel: ControlPanel
    lateinit var loop: BilliardLoop
    private var lastX = 0.0
    private var lastY = 0.0
    private var isDragging = false
    private val sensitivity = 0.2

    override fun init() {
        logger.info { "init" }
        // screen: Rectangle2D [minX=0.0, minY=0.0, maxX=1536.0, maxY=864.0, width=1536.0, height=864.0]

        val canvasWidth = 800.0
        val canvasHeight = 600.0
        canvasPanel = CanvasPanel(canvasWidth, canvasHeight).apply {
            isFocusTraversable = true
        }
        controlPanel = ControlPanel(10.0)

        configureListeners()
    }

    private fun configureListeners() {
        controlPanel.strikeBallButton.setOnAction {
            val ind = controlPanel.strikeBallSpinner.value - 1
            val force = controlPanel.strikeBallForceSpinner.value.toFloat()
            val angle = controlPanel.strikeBallAngleSpinner.value
            (loop.composite[ind] as BowlingBall).cueHit(force, Math.toRadians(angle))
        }
        controlPanel.moveLeftButton.setOnAction {
            loop.camera.moveLeft()
        }
        controlPanel.moveRightButton.setOnAction {
            loop.camera.moveRight()
        }
        controlPanel.moveBackwardButton.setOnAction {
            loop.camera.moveBackward()
        }
        controlPanel.moveForwardButton.setOnAction {
            loop.camera.moveForward()
        }
        controlPanel.moveDownwardButton.setOnAction {
            loop.camera.moveDownward()
        }
        controlPanel.moveUpwardButton.setOnAction {
            loop.camera.moveUpward()
        }

        controlPanel.rotateOXButton.setOnAction {
            val deg = controlPanel.rotateOXSpinner.value.toFloat()
            loop.camera.rotateOX(deg)
        }
        controlPanel.rotateOYButton.setOnAction {
            val deg = controlPanel.rotateOYSpinner.value.toFloat()
            loop.camera.rotateOY(deg)
        }
        controlPanel.rotateOZButton.setOnAction {
            val deg = controlPanel.rotateOZSpinner.value.toFloat()
            loop.camera.rotateOZ(deg)
        }

        canvasPanel.setOnKeyPressed { event ->
            when (event.code) {
                KeyCode.W, KeyCode.UP       -> loop.camera.moveForward()
                KeyCode.S, KeyCode.DOWN     -> loop.camera.moveBackward()
                KeyCode.A, KeyCode.LEFT     -> loop.camera.moveLeft()
                KeyCode.D, KeyCode.RIGHT    -> loop.camera.moveRight()
                KeyCode.SPACE               -> loop.camera.moveUpward()
                KeyCode.CAPS, KeyCode.SHIFT -> loop.camera.moveDownward()
                else                        -> {}
            }
        }

        canvasPanel.setOnMousePressed { event ->
            if (event.isPrimaryButtonDown) {
                lastX = event.x; lastY = event.y
                isDragging = true
                canvasPanel.requestFocus()
            }
        }

        canvasPanel.setOnMouseReleased { event ->
            isDragging = false
        }

        canvasPanel.setOnMouseDragged { event ->
            if (!isDragging) {
                return@setOnMouseDragged
            }
            val dx = event.x - lastX
            val dy = event.y - lastY
            lastX = event.x; lastY = event.y
            if (event.isShiftDown) {
                loop.camera.rotateAroundRight((-dy * sensitivity).toFloat())
            } else {
                loop.camera.rotateAroundUp((dx * sensitivity).toFloat())
            }
        }

        controlPanel.setOnMousePressed { event ->
            canvasPanel.requestFocus()
        }
    }

    override fun start(stage: Stage) {
        logger.info { "starting billiard" }

        stage.title = "3D Billiard"
        stage.scene = Scene(HBox(controlPanel, canvasPanel))
        stage.show()

        loop =
            BilliardLoop(canvasPanel.graphicsContext2D, canvasPanel.width.toInt(), canvasPanel.height.toInt())
                    .apply { start() }
    }
}
