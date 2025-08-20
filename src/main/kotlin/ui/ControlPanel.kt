package org.wocy.ui

import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class ControlPanel : VBox {
    val addBallButton = Button("AddBall")
    val removeBallButton = Button("RemoveBall")
    val chooseBallButton = Button("Choose Ball")
    val strikeBallButton = Button("Strike Ball")
    val strikeBallSpinner = Spinner<Int>().apply {
        valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1)
        isEditable = true
    }
    val strikeBallAngleSpinner = Spinner<Double>().apply {
        valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(-1000.0, 1000.0, 0.0, 10.0)
        isEditable = true
    }
    val strikeBallForceSpinner = Spinner<Int>().apply {
        valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20000, 0, 100)
        isEditable = true
    }
    val moveLeftButton = Button("←")
    val moveRightButton = Button("→")
    val moveBackwardButton = Button("↓")
    val moveForwardButton = Button("↑")
    val moveDownwardButton = Button("↓")
    val moveUpwardButton = Button("↑")
    val rotateOXSpinner = Spinner<Double>().apply {
        valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(-1000.0, 1000.0, 0.0, 15.0)
        isEditable = true
    }
    val rotateOYSpinner = Spinner<Double>().apply {
        valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(-1000.0, 1000.0, 0.0, 15.0)
        isEditable = true
    }
    val rotateOZSpinner = Spinner<Double>().apply {
        valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(-1000.0, 1000.0, 0.0, 15.0)
        isEditable = true
    }
    val rotateOXButton = Button("Rotate OX")
    val rotateOYButton = Button("Rotate OY")
    val rotateOZButton = Button("Rotate OZ")

    constructor(spacing: Double) : super(spacing) {
        children.addAll(
            addBallButton,
            removeBallButton,
            chooseBallButton,
            HBox(5.0, strikeBallButton, strikeBallSpinner),
            HBox(5.0, strikeBallAngleSpinner, strikeBallForceSpinner),
            Separator(),
            Label("Camera controls"),
            HBox(
                5.0,
                moveLeftButton,
                moveRightButton,
                moveBackwardButton,
                moveForwardButton,
                moveDownwardButton,
                moveUpwardButton
            ),
            HBox(5.0, rotateOXSpinner, rotateOYSpinner, rotateOZSpinner),
            HBox(5.0, rotateOXButton, rotateOYButton, rotateOZButton),
        )
        minWidth = 200.0
        maxWidth = 200.0
        prefWidth = 200.0
        style = "-fx-padding: 10; -fx-background-color: #dddddd;"
    }
}