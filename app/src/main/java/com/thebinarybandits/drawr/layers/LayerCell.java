package com.thebinarybandits.drawr.layers;

import com.thebinarybandits.drawr.utils.Grid;
import com.thebinarybandits.drawr.pixelcanvas.PixelView;
import javafx.scene.layout.Pane;

public class LayerCell extends Pane {

    private final Grid grid;

    public LayerCell(int viewSize, int scale) {
        this.setPrefWidth(viewSize);
        this.setPrefHeight(viewSize);

        grid = new Grid(viewSize, scale);

        this.getChildren().add(grid);
    }

    public void setLayerView(PixelView layerView) {
        this.getChildren().add(layerView);
        grid.toFront();
    }

}
