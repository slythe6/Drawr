package com.thebinarybandits.drawr.controllers;

import com.thebinarybandits.drawr.encoder.GifManager;
import com.thebinarybandits.drawr.pixelcanvas.PixelCanvas;
import com.thebinarybandits.drawr.pixelcanvas.PixelImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AppController {

    private PixelCanvas canvas;
    private Stage stage;

    @FXML
    private Parent tools;
    @FXML
    private ToolsController toolsController;

    @FXML
    public void initialize() {
        canvas = PixelCanvas.getInstance();
        canvas.initialize();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Runs when the user clicks the 'new' menu button.
     * Creates a new project.
     */
    @FXML
    void newProject() {
        // popup window to alert user that creating a new project will cause unsaved changes to be lost
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("New Project");
        alert.setHeaderText("Are you sure you want to create a new project?");
        alert.setContentText("All unsaved changes will be lost.");
        java.util.Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // user clicked OK, reset the PixelCanvas
            canvas.reset();
            canvas.initialize();
            toolsController.reset();
        }
    }

    /**
     * Runs when the user clicks the 'open' menu button.
     * Opens a project from a .drawr file.
     */
    @FXML
    void openProject() throws IOException, ClassNotFoundException {
        FileChooser fileSystem = new FileChooser();
        fileSystem.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawr Files (*.drawr)", "*.drawr"));

        File file = fileSystem.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        // Check if selected file is a '.drawr' file
        if (!file.getName().endsWith(".drawr")) {
            // Display an error message if selected file is not a '.drawr' file
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please choose a '.drawr' file.");
            alert.showAndWait();
            return;
        }

        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        ArrayList<String[][]> layersArrayList = (ArrayList<String[][]>) in.readObject();
        in.close();

        // if we allow users to change canvas size
        // then we would have to initialize the canvas based on the file's canvas size
        canvas.reset();
        canvas.initialize();
        canvas.initLayersData(layersArrayList);
        toolsController.reset();
    }

    @FXML
    void saveAsGIF() {
        FileChooser fileSystem = new FileChooser();
        fileSystem.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF Files (*.gif)", "*.gif"));
        fileSystem.setInitialFileName("animation.gif");

        File file = fileSystem.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        GifManager gifmanager = new GifManager(canvas.getLayers());
        gifmanager.start(file.getAbsolutePath());
    }

    @FXML
    void saveAsPNG() throws IOException {
        FileChooser fileSystem = new FileChooser();
        fileSystem.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png"));
        fileSystem.setInitialFileName("image.png");

        File file = fileSystem.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        Canvas flattenImage = new Canvas(canvas.getSize(), canvas.getSize());
        GraphicsContext imageGraphics = flattenImage.getGraphicsContext2D();
        ArrayList<PixelImage> images = canvas.getLayers();

        for (PixelImage image : images) {
            imageGraphics.drawImage(image, 0, 0);
        }

        WritableImage png = flattenImage.snapshot(null, null);

        BufferedImage test = SwingFXUtils.fromFXImage(png, null);
        ImageIO.write(test, "png", file);
    }

    @FXML
    void saveAsSpriteSheet() throws IOException {
        FileChooser fileSystem = new FileChooser();
        fileSystem.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png"));
        fileSystem.setInitialFileName("sprite-sheet.png");

        File file = fileSystem.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        int length = canvas.getSize() * canvas.getLayers().size();
        int height = canvas.getSize();
        Canvas combineImage = new Canvas(length, height);
        GraphicsContext imageGraphics = combineImage.getGraphicsContext2D();
        ArrayList<PixelImage> images = canvas.getLayers();

        int counter = 0;
        for (PixelImage image : images) {
            imageGraphics.drawImage(image, counter * canvas.getSize(), 0);
            ++counter;
        }

        WritableImage spriteSheet = combineImage.snapshot(null, null);

        BufferedImage test = SwingFXUtils.fromFXImage(spriteSheet, null);
        ImageIO.write(test, "png", file);
    }

    /**
     * Runs when the user clicks the 'save' menu button.
     * Saves the project as a .drawr file.
     */
    @FXML
    void saveProject() throws IOException {
        FileChooser fileSystem = new FileChooser();
        fileSystem.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawr Files (*.drawr)", "*.drawr"));
        fileSystem.setInitialFileName("project.drawr");

        File file = fileSystem.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        ArrayList<String[][]> layersArrayList = canvas.getLayersData();

        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(layersArrayList);
        out.close();
        fileOut.close();
    }

    /**
     * Runs when the user clicks the 'undo' menu button.
     * Undo the project.
     */
    @FXML
    void undo() {
        canvas.undo();
    }

    /**
     * Runs when the user clicks the 'redo' menu button.
     * Redo the project.
     */
    @FXML
    void redo() {
        canvas.redo();
    }

    /**
     * Runs when the user clicks the 'clear' menu button.
     * Erases the entire active image
     */
    @FXML
    void clear() {
        canvas.clear();
    }

}
