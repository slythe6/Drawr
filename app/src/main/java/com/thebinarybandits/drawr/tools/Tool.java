package com.thebinarybandits.drawr.tools;

import com.thebinarybandits.drawr.pixelcanvas.PixelImage;
import javafx.scene.paint.Color;

/**
 * Tool interface. To be implemented by all tools.
 * Allows the activeTool data member in PixelCanvas to benefit from polymorphism.
 */
public interface Tool {
    void useTool(PixelImage panel, int x, int y, Color color, int CANVAS_SIZE);
}
