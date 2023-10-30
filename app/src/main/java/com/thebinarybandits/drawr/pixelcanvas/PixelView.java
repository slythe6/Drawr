package com.thebinarybandits.drawr.pixelcanvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

public class PixelView extends Canvas {

    private final GraphicsContext viewGraphics;

    public PixelView(int size) {
        super(size, size);
        viewGraphics = this.getGraphicsContext2D();
        viewGraphics.setImageSmoothing(false);
    }

    public void update(WritableImage image) {
        clear();
        viewGraphics.drawImage(image, 0, 0, this.getWidth(), this.getHeight());
    }

    public void clear() {
        viewGraphics.clearRect(0, 0, this.getWidth(), this.getHeight());
    }

    public GraphicsContext getGraphics() {
        return viewGraphics;
    }

}
