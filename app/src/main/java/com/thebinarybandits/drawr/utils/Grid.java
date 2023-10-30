package com.thebinarybandits.drawr.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Grid extends Canvas {

    private int size;
    private int scale;
    private final GraphicsContext graphics;

    public Grid(int size, int scale) {
        super(size, size);
        this.size = size;
        this.scale = scale;
        graphics = this.getGraphicsContext2D();
        graphics.setLineWidth(0.2);
        overlayGrid();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void overlayGrid() {
        for (int row = 0; row <= size; row += scale) {
            graphics.strokeLine(0, row, size, row);
        }
        for (int column = 0; column <= size; column += scale) {
            graphics.strokeLine(column, 0, column, size);
        }
    }

}
