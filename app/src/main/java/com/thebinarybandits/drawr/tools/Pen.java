package com.thebinarybandits.drawr.tools;

import com.thebinarybandits.drawr.pixelcanvas.PixelImage;
import javafx.scene.paint.Color;

/**
 * Pen tool. Implements the useTool method.
 */
public class Pen implements Tool {

    /**
     * Inherited from Tool. Draws on a panel.
     *
     * @param panel       the panel to be drawn on
     * @param x           the x coordinate of the square that will be drawn on
     * @param y           the y coordinate of the square that will be drawn on
     * @param color       the color that will be used to draw on the panel
     * @param CANVAS_SIZE size of the grid on the panel. Default is 16x16
     */
    public void useTool(PixelImage panel, int x, int y, Color color, int CANVAS_SIZE) {
        panel.draw(x, y, color);
    }

}
