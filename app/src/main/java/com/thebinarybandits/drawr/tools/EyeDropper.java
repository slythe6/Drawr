package com.thebinarybandits.drawr.tools;

import com.thebinarybandits.drawr.pixelcanvas.PixelImage;
import javafx.scene.paint.Color;
import javafx.scene.control.ColorPicker;
import com.thebinarybandits.drawr.pixelcanvas.PixelCanvas;

/**
 * EyeDropper tool. Implements the useTool method.
 */
public class EyeDropper implements Tool {

    private final ColorPicker colorPicker;
    private final PixelCanvas canvas;

    /**
     * EyeDropper Constructor.
     * 
     * @param colorPicker  the color picker from ToolsController
     * @param canvas  the canvas from ToolsController
     */
    public EyeDropper(ColorPicker colorPicker, PixelCanvas canvas) {
        this.colorPicker = colorPicker;
        this.canvas = canvas;
    }

    /**
     * Inherited from Tool. Sets the colorPicker and canvas activeColor to the color of a square.
     * 
     * @param panel  the panel that the color will be grabbed from
     * @param x  the x coordinate of the square that's color will be grabbed
     * @param y  the y coordinate of the square that's color will be grabbed
     * @param color  not needed. Inherited from tool
     * @param CANVAS_SIZE  size of the grid on the panel. Default is 16x16
     */
    public void useTool(PixelImage panel, int x, int y, Color color, int CANVAS_SIZE) {
        colorPicker.setValue(panel.getPixelData(x, y));
        canvas.setColor(colorPicker.getValue());
    }

}
