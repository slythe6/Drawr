package com.thebinarybandits.drawr.tools;

import com.thebinarybandits.drawr.pixelcanvas.PixelImage;
import javafx.scene.paint.Color;

/**
 * Eraser tool. Implements the useTool method.
 */
public class Eraser implements Tool {

    /**
     * Inherited from Tool. Erases a square on a panel.
     *
     * @param panel       the panel that will be erased on
     * @param x           the x coordinate of the square that will be erased on
     * @param y           the y coordinate of the square that will be drawn on
     * @param color       not needed. Inherited from tool
     * @param CANVAS_SIZE size of the grid on the panel. Default is 16x16
     */
    public void useTool(PixelImage panel, int x, int y, Color color, int CANVAS_SIZE) {
        panel.erase(x, y);
    }

}
