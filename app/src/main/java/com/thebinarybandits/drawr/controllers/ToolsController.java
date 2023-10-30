package com.thebinarybandits.drawr.controllers;

import com.thebinarybandits.drawr.pixelcanvas.PixelCanvas;
import com.thebinarybandits.drawr.tools.Eraser;
import com.thebinarybandits.drawr.tools.EyeDropper;
import com.thebinarybandits.drawr.tools.PaintBucket;
import com.thebinarybandits.drawr.tools.Pen;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.paint.Color;

/**
 * Interacts with the view_tools xml file.
 */
public class ToolsController {

    private final PixelCanvas canvas = PixelCanvas.getInstance(); // the active canvas

    @FXML private ColorPicker colorPicker;
    @FXML private RadioButton penButton;

    /**
     * Resets the Tools panel to default values.
     */
    public void reset(){
        penButton.setSelected(true);
        canvas.setTool(new Pen());
        colorPicker.setValue(Color.BLACK);
        canvas.setColor(colorPicker.getValue());
    }

    /**
     * Runs when the user clicks the eraser button.
     * Sets the eraser tool as the active tool.
     */
    @FXML
    void eraserSelected() {
        canvas.setTool(new Eraser());
    }

    /**
     * Runs when the user clicks the paint bucket button.
     * Sets the paint bucket tool as the active tool.
     */
    @FXML
    void paintBucketSelected() {
        canvas.setTool(new PaintBucket());
    }

    /**
     * Runs when the user clicks the pen button.
     * Sets the pen tool as the active tool.
     */
    @FXML
    void penSelected() {
        canvas.setTool(new Pen());
    }

    /**
     * Runs when the user clicks the eye dropper button.
     * Sets the eye dropper tool as the active tool.
     */
    @FXML
    void eyeDropperSelected() {
        canvas.setTool(new EyeDropper(colorPicker, canvas));
    }

    /**
     * Runs when the user selects a color from the color picker.
     * Sets the color chosen as the active color.
     */
    @FXML
    void updateColor() {
        canvas.setColor(colorPicker.getValue());
    }

}
