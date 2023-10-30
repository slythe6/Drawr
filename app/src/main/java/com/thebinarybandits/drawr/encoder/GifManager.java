package com.thebinarybandits.drawr.encoder;

import com.thebinarybandits.drawr.pixelcanvas.PixelImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.ArrayList;

public class GifManager {

    private final ArrayList<PixelImage> images;
    private final ArrayList<BufferedImage> bufferedImages;
    private int repeat;
    private int delay;
    private int size;

    public GifManager(ArrayList<PixelImage> images) {
        this.images = images;
        bufferedImages = new ArrayList<>();
        repeat = 0;
        delay = 500;
        size = 240;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void start(OutputStream stream) {
        convertImages();

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();

        encoder.start(stream);
        encoder.setDelay(delay);
        encoder.setRepeat(repeat);

        for (BufferedImage image : bufferedImages) {
            encoder.addFrame(image);
        }

        encoder.finish();
    }

    public void start(String path) {
        convertImages();

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();

        encoder.start(path);
        encoder.setDelay(delay);
        encoder.setRepeat(repeat);

        for (BufferedImage image : bufferedImages) {
            encoder.addFrame(image);
        }

        encoder.finish();
    }

    private void convertImages() {
        for (PixelImage image : images) {
            WritableImage scaledImage = scaleImage(image);

            BufferedImage converted = SwingFXUtils.fromFXImage(scaledImage, null);
            bufferedImages.add(converted);
        }
    }

    private WritableImage scaleImage(PixelImage image) {
        Canvas imageView = new Canvas(size, size);
        GraphicsContext viewGraphics = imageView.getGraphicsContext2D();
        viewGraphics.setImageSmoothing(false);
        viewGraphics.drawImage(image, 0, 0, size, size);

        return imageView.snapshot(null, null);
    }

}
