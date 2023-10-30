package com.thebinarybandits.drawr.pixelcanvas;

import com.thebinarybandits.drawr.tools.Pen;
import com.thebinarybandits.drawr.tools.Tool;
import com.thebinarybandits.drawr.utils.Pair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

// singleton pattern
public class PixelCanvas {

    private static volatile PixelCanvas instance;

    private int size;
    private int viewSize;
    private Tool activeTool;
    private Color activeColor;
    private final ArrayList<PixelImage> layers;
    private final PixelView canvasView;
    private final ObservableList<PixelView> layerViews;
    private int index;
    private Stack<Pair<ArrayList<String[][]>, Integer>> undo;
    private Stack<Pair<ArrayList<String[][]>, Integer>> redo;
    private static final boolean DEBUG = false;

    public enum Direction {UP, DOWN}

    private PixelCanvas() {
        size = 16;
        viewSize = 640;
        activeTool = new Pen();
        activeColor = Color.BLACK;
        layers = new ArrayList<>();
        canvasView = new PixelView(viewSize);
        layerViews = FXCollections.observableArrayList();
        index = -1;
        undo = new Stack<>();
        redo = new Stack<>();
    }

    public static PixelCanvas getInstance() {
        PixelCanvas result = instance;
        if (result == null) {
            synchronized (PixelCanvas.class) {
                result = instance;
                if (result == null) {
                    instance = result = new PixelCanvas();
                }
            }
        }

        return result;
    }

    public void initialize() {
        ++index;
        layers.add(new PixelImage(size));
        layerViews.add(new PixelView(240));
    }

    public void reset() {
        size = 16;
        viewSize = 640;
        activeTool = new Pen();
        activeColor = Color.BLACK;
        layers.clear();
        canvasView.clear();
        layerViews.clear();
        index = -1;
        undo = new Stack<>();
        redo = new Stack<>();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setViewSize(int viewSize) {
        this.viewSize = viewSize;
    }

    public void setTool(Tool tool) {
        activeTool = tool;
    }

    public void setColor(Color color) {
        activeColor = color;
    }

    public int getSize() {
        return size;
    }

    public int getViewSize() {
        return viewSize;
    }

    public int getScale() {
        return viewSize / size;
    }

    public PixelImage getImage() {
        if (DEBUG)
            System.out.println("moving to index: " + this.index);
        return layers.get(index);
    }

    public PixelView getCanvasView() {
        return canvasView;
    }

    public PixelView getLayerView() {
        return layerViews.get(index);
    }

    public ArrayList<PixelImage> getLayers() {
        return layers;
    }

    public ObservableList<PixelView> getLayerViews() {
        return layerViews;
    }

    public boolean draw(int x, int y) {
        if (activeTool != null && layers.size() > 0) {
            pushUndo();
            Color old_color = layers.get(index).getPixelData(x, y);

            activeTool.useTool(layers.get(index), x, y, activeColor, size);
            canvasView.update(getImage());

            getLayerView().update(getImage());

            if (old_color.equals(layers.get(index).getPixelData(x, y))) {
                if (DEBUG)
                    System.out.println("No changes, dropping undo push");
                undo.pop();
                return false;
            }

            return true;
        }

        return false;
    }

    public void clear() {
        pushUndo();
        layers.get(index).clear();
        canvasView.clear();
        layerViews.get(index).clear();
    }

    public void createLayer() {
        pushUndo();

        if (index == layers.size() - 1) {
            ++index;
            layers.add(new PixelImage(size));
            layerViews.add(new PixelView(240));

        } else {
            ++index;
            layers.add(index, new PixelImage(size));
            layerViews.add(index, new PixelView(240));
        }

        canvasView.update(getImage());
    }

    /**
     * Creates a layer.
     * Does not push it onto the undo stack.
     */
    private void createLayerInternal() {
        if (index == layers.size() - 1) {
            ++index;
            layers.add(new PixelImage(size));
            layerViews.add(new PixelView(240));

        } else {
            ++index;
            layers.add(index, new PixelImage(size));
            layerViews.add(index, new PixelView(240));
        }

        canvasView.update(getImage());
    }

    public void deleteLayer() {
        if (layers.size() - 1 == 0) return;

        pushUndo();

        layers.remove(getImage());
        layerViews.remove(getLayerView());

        canvasView.update(getImage());
    }

    public void changeLayer(int index) {
        this.index = index;

        // only update the view if the canvas is initialized
        if (index >= 0) {
            canvasView.update(getImage());
        }
    }

    public void swapLayer(Direction location) {
        if (location == Direction.UP && index - 1 >= 0) {
            pushUndo();

            PixelImage currentImage = layers.get(index);
            layers.remove(currentImage);
            layers.add(index - 1, currentImage);

            Collections.swap(layerViews, index, index - 1);

        } else if (location == Direction.DOWN && index + 1 <= layerViews.size() - 1) {
            pushUndo();

            PixelImage nextImage = layers.get(index + 1);
            layers.remove(nextImage);
            layers.add(index, nextImage);

            Collections.swap(layerViews, index, index + 1);
        }
    }

    /**
     * Gets the data of all images and squares in the project
     *
     * @return a mapped array, each arraylist element represents an image, each 2d array of strings represents the color of all squares
     */
    public ArrayList<String[][]> getLayersData() {
        ArrayList<String[][]> layersArrayList = new ArrayList<>();

        for (PixelImage layer : layers) {
            layersArrayList.add(layer.getImageData());
        }

        return layersArrayList;
    }

    /**
     * Initializes the layers on the project from a mapped ArrayList of Strings.
     *
     * @param layersArrayList a mapped array, each arraylist element represents an image, each 2d array of strings represents the color of all squares
     */
    public void initLayersData(ArrayList<String[][]> layersArrayList) {
        getImage().setImageData(layersArrayList.get(0));
        getCanvasView().update(getImage());
        getLayerView().update(getImage());

        for (int i = 1; i < layersArrayList.size(); i++) {
            createLayerInternal();
            getImage().setImageData(layersArrayList.get(i));
            getCanvasView().update(getImage());
            getLayerView().update(getImage());
        }
    }

    /**
     * Used only with Undo().
     * Sets the project to project in layersArraylist
     *
     * @param layersArrayList a mapped array, each arraylist element represents an image, each 2d array of strings represents the color of all squares
     * @param index           index of the active image
     */
    private void setLayersData(ArrayList<String[][]> layersArrayList, int index) {
        layers.clear();
        layerViews.clear();

        for (int i = 0; i < layersArrayList.size(); i++) {
            createLayerInternal();
            layers.get(i).setImageData(layersArrayList.get(i));
            layerViews.get(i).update(layers.get(i));
        }

        changeLayer(index);
    }

    /**
     * Pushes layersData onto the undo stack.
     * Clears the redo stack.
     */
    private void pushUndo() {
        if (DEBUG)
            System.out.println("***Function: pushUndo***");
        undo.push(new Pair<>(getLayersData(), index));
        if (DEBUG)
            System.out.println("undo stack size: " + undo.size());
        redo.clear();
        if (DEBUG)
            System.out.println("redo stack size: " + redo.size());
    }

    /**
     * Discards the top of the undo stack.
     * Used for PaneController to work with mouseDrag
     */
    public void discardUndo() {
        undo.pop();
    }

    /**
     * Pop the undo stack and make that the active project.
     * Also pushes the current project on the redo stack.
     */
    public void undo() {
        if (!undo.empty()) {
            if (DEBUG)
                System.out.println("***Function: undo***");
            redo.push(new Pair<>(getLayersData(), index));
            if (DEBUG)
                System.out.println("redo stack size: " + redo.size());
            Pair<ArrayList<String[][]>, Integer> temp = undo.pop();
            setLayersData(temp.x, temp.y);
            if (DEBUG)
                System.out.println("undo stack size: " + undo.size());
        }
    }

    /**
     * Pop the redo stack and make that the active project.
     */
    public void redo() {
        if (!redo.empty()) {
            if (DEBUG)
                System.out.println("***Function: redo***");
            undo.push(new Pair<>(getLayersData(), index));
            if (DEBUG)
                System.out.println("undo stack size: " + undo.size());
            Pair<ArrayList<String[][]>, Integer> temp = redo.pop();
            setLayersData(temp.x, temp.y);
            if (DEBUG)
                System.out.println("redo stack size: " + redo.size());
        }
    }

}
