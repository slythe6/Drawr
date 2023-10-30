package com.thebinarybandits.drawr.controllers;

import com.thebinarybandits.drawr.encoder.GifManager;
import com.thebinarybandits.drawr.layers.LayerCell;
import com.thebinarybandits.drawr.pixelcanvas.PixelCanvas;
import com.thebinarybandits.drawr.pixelcanvas.PixelView;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class LayersController {

    private PixelCanvas canvas;
    private SimpleIntegerProperty index;

    @FXML private ImageView animationView;
    @FXML private TextField delayField;
    @FXML private VBox layersContainer;

    @FXML
    public void initialize() {
        canvas = PixelCanvas.getInstance();

        index = new SimpleIntegerProperty(-1);

        // listen to change layer
        index.addListener((observable, oldValue, newValue) -> canvas.changeLayer(newValue.intValue()));

        // listen for layer selection from user
        layersContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            LayerCell parent = (LayerCell) event.getPickResult().getIntersectedNode().getParent();

            highlightSelection(parent);
            index.set(layersContainer.getChildren().indexOf(parent));
        });

        ObservableList<PixelView> views = canvas.getLayerViews();

        // listen for created or deleted layerCells
        views.addListener((ListChangeListener<PixelView>) change -> {
            change.next();

            if (change.wasReplaced()) {
                // swapping layers
                return;
            }

            if (change.wasAdded()) {
                // creating a new layerCell
                Binding<PixelView> layerView = Bindings.createObjectBinding(() -> canvas.getLayerView());

                LayerCell cell = new LayerCell(240, 240 / canvas.getSize());
                cell.setLayerView(layerView.getValue());

                // insert at the end of the list
                if (index.intValue() == views.size() - 1) {
                    index.set(index.intValue() + 1);
                    layersContainer.getChildren().add(cell);

                    highlightSelection(cell);

                    // insert at the next index
                } else {
                    index.set(index.intValue() + 1);
                    layersContainer.getChildren().add(index.intValue(), cell);

                    highlightSelection(cell);
                }
            }

            boolean newProject = (change.wasRemoved() && change.getRemovedSize() > 1) || (change.wasRemoved() && layersContainer.getChildren().size() - 1 == 0);

            if (newProject) {
                index.set(-1);
                layersContainer.getChildren().clear();

            } else if (change.wasRemoved()) {
                // prevent underflow if deleting from the middle or beginning
                if (index.intValue() - 1 >= 0) {
                    index.set(index.intValue() - 1);
                }

                layersContainer.getChildren().remove(change.getRemoved().get(0).getParent());
                highlightSelection(layersContainer.getChildren().get(index.intValue()));
            }
        });

        // filter user input to only accept int
        delayField.setTextFormatter(new TextFormatter<Integer>(change -> {
            String text = change.getControlNewText();

            if (text.matches("\\d*")) {
                return change;
            }

            return null;
        }));
    }

    void highlightSelection(Node cell) {
        for (Node child : layersContainer.getChildren()) {
            child.setStyle("-fx-border-color: #f4f4f4");
        }

        cell.setStyle("-fx-border-color: black");
    }

    @FXML
    void createLayer() {
        canvas.createLayer();
    }

    @FXML
    void deleteLayer() {
        canvas.deleteLayer();
    }

    @FXML
    void moveUp() {
        canvas.swapLayer(PixelCanvas.Direction.UP);

        if (index.intValue() - 1 >= 0) {
            Node currentCell = layersContainer.getChildren().get(index.intValue());
            layersContainer.getChildren().remove(currentCell);
            layersContainer.getChildren().add(index.intValue() - 1, currentCell);

            index.set(index.intValue() - 1);
        }
    }

    @FXML
    void moveDown() {
        canvas.swapLayer(PixelCanvas.Direction.DOWN);

        if (index.intValue() + 1 <= layersContainer.getChildren().size() - 1) {
            Node nextCell = layersContainer.getChildren().get(index.intValue() + 1);
            layersContainer.getChildren().remove(nextCell);
            layersContainer.getChildren().add(index.intValue(), nextCell);

            index.set(index.intValue() + 1);
        }
    }

    @FXML
    void previewAnimation() throws IOException {
        if ((delayField.getText().equals(""))) {
            return;
        }

        FileOutputStream file = new FileOutputStream(Objects.requireNonNull(getClass().getResource("/drawable/preview.gif")).getPath());

        GifManager gifManager = new GifManager(canvas.getLayers());
        gifManager.setRepeat(3);
        gifManager.setDelay(Integer.parseInt(delayField.getText()));
        gifManager.setSize(240);
        gifManager.start(file);

        file.close();

        animationView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/drawable/preview.gif"))));
    }

}
