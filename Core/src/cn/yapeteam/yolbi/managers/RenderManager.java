package cn.yapeteam.yolbi.managers;


import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.utils.IMinecraft;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Getter
@SuppressWarnings("unused")
public class RenderManager implements IMinecraft {
    public Map<String, Shape> shapesMap;

    public List<String> modifiedidbuffer;
    private Pane root;
    private Stage primaryStage; // reference to the primary stage

    private AnimationTimer animationTimer;
    private Scene scene; // reference to the scene
    private boolean isopen = true;
    private double lastX = -1;
    private double lastY = -1;

    private double lastwidth = -1;
    private double lastheight = -1;

    public static void unzip(InputStream zipFile, File desDir) throws Exception {
        boolean ignored = desDir.mkdir();
        ZipInputStream zipInputStream = new ZipInputStream(zipFile);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            String unzipFilePath = desDir.getAbsolutePath() + File.separator + zipEntry.getName();
            if (zipEntry.isDirectory())
                mkdir(new File(unzipFilePath));
            else {
                File file = new File(unzipFilePath);
                mkdir(file.getParentFile());
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(unzipFilePath)));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1)
                    bufferedOutputStream.write(bytes, 0, readLen);
                bufferedOutputStream.close();
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    public static void mkdir(File file) {
        if (null == file || file.exists())
            return;
        mkdir(file.getParentFile());
        boolean ignored = file.mkdir();
    }

    public boolean isdrawinggui = false;

    // Check the window position every second
    public void CheckWindowPosition() {

        Timer timer = new Timer();
        TimerTask checkPositionTask = new TimerTask() {
            @Override
            public void run() {
                if (isopen) {
                    if (Display.getX() != lastX || Display.getY() != lastY || Display.getHeight() != lastheight || Display.getWidth() != lastwidth) {
                        lastX = Display.getX();
                        lastY = Display.getY();
                        lastwidth = Display.getWidth();
                        lastheight = Display.getHeight();
                        updateWindowLocation();
                    }
                }
            }
        };

        // Schedule the task to run once every second (1000 milliseconds)
        timer.scheduleAtFixedRate(checkPositionTask, 0, 1000);
    }


    public void setupAnimationTimer() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Platform.runLater(() -> {
                    CheckCanvas(); // Ensure this is called to check and render shapes
                });
            }
        };
        animationTimer.start();
    }

    public void CheckCanvas() {
        // Iterates over the map using an iterator to avoid ConcurrentModificationException
        Iterator<Map.Entry<String, Shape>> iterator = shapesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Shape> entry = iterator.next();
            String id = entry.getKey();
            Shape shape = entry.getValue();
            shape.setMouseTransparent(true); // Ensure the shape is not interactive
            if (!modifiedidbuffer.contains(id)) {
                // Directly remove from the iterator and root to avoid concurrent modification issues
                Platform.runLater(() -> root.getChildren().remove(entry.getValue()));
                iterator.remove(); // Removes from shapesMap safely
            }
        }
        modifiedidbuffer.clear(); // Clear the buffer for the next check

        if (isdrawinggui) {
            if (!isopen) {
                isopen = true;
            }
        } else if (mc.theWorld != null && mc.thePlayer != null && mc.currentScreen == null) {
            // means ingame
            if (!isopen) {
                isopen = true;
            }
        } else {
            isopen = false;
        }
    }


    // Initialize the overlay window
    public void initwindow() {
        try {
            unzip(RenderManager.class.getResourceAsStream(System.getProperty("java.version").startsWith("1.8") ?
                    "/jfxrt-natives.zip" : "/javafx-natives.zip"), new File(System.getProperty("java.library.path")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new JFXPanel(); // Initialize JavaFX toolkit
        shapesMap = new HashMap<>();
        modifiedidbuffer = new ArrayList<>(); // Corrected initialization
        root = new Pane(); // reference to the root pane
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        double width = scaledResolution.getScaledWidth_double();
        double height = scaledResolution.getScaledHeight_double();
        createOverlayWindow(width, height);
        lastX = Display.getX();
        lastY = Display.getY();
        CheckWindowPosition();
        updateWindowLocation();
        setupAnimationTimer();
    }

    public void destroywindow() {
        animationTimer.stop();
        shapesMap.clear();
        modifiedidbuffer.clear();
        isopen = false;
        Platform.runLater(() -> {
            if (root != null) {
                primaryStage.close();
                root.getChildren().clear();
            }
        });
    }


    // Create the overlay window
    private void createOverlayWindow(double width, double height) {
        Platform.runLater(() -> {
            primaryStage = new Stage();
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            scene = new Scene(root, width * scaledResolution.getScaleFactor(), height * scaledResolution.getScaleFactor());
            scene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(scene);
            // Handle the close request by consuming the event, which prevents the window from being closed
            primaryStage.setOnCloseRequest(Event::consume);
            primaryStage.setTitle("YolBi Lite Overlay");
            Natives.SetWindowsTransparent(true, primaryStage.getTitle());
            primaryStage.show();
        });
    }


    public void updateWindowLocation() {
        Platform.runLater(() -> {
            if (isopen) {
                if (primaryStage != null) {
                    ScaledResolution scaledResolution = new ScaledResolution(mc);
                    final int factor = scaledResolution.getScaleFactor();

                    final int titleBarHeightEstimate = 30; // Adjust this value as necessary

                    // Adjust window size and position
                    primaryStage.setX(Display.getX() + 8);
                    primaryStage.setY(Display.getY() + titleBarHeightEstimate);
                    // Here, subtract the estimated title bar height from the height calculation
                    primaryStage.setWidth(scaledResolution.getScaledWidth_double() * factor);
                    primaryStage.setHeight((scaledResolution.getScaledHeight_double() * factor));

                    // Reinforce root size after clearing its children
                    root.setPrefSize(scaledResolution.getScaledWidth() * factor, scaledResolution.getScaledHeight() * factor);
                }
            }
        });
    }


    public void roundedRectangle(String id, final double x, final double y, final double width, final double height, final double arcWidth, final double arcHeight, final java.awt.Color color) {
        roundedRectangle(id, x, y, width, height, arcWidth, arcHeight, color, false, false);
    }


    public void roundedRectangle(String id, final double x, final double y, final double width, final double height, final double arcWidth, final double arcHeight, final java.awt.Color color, boolean blur, boolean bloom) {
        if (isopen) {
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Rectangle roundedRectangle = (Rectangle) shapesMap.get(id);
                GaussianBlur blurEffect = new GaussianBlur();
                Bloom bloomEffect = new Bloom();
                Color fillcolor = convertColor(color);
                blurEffect.setRadius(1000000);
                if (roundedRectangle == null) {
                    roundedRectangle = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                    roundedRectangle.setArcWidth(arcWidth * factor);
                    roundedRectangle.setArcHeight(arcHeight * factor);
                    roundedRectangle.setFill(fillcolor);
                    shapesMap.put(id, roundedRectangle);
                    roundedRectangle.setMouseTransparent(true);
                    if (blur) {
                        roundedRectangle.setEffect(blurEffect);
                    }
                    if (bloom) {
                        roundedRectangle.setEffect(bloomEffect);
                    }
                    root.getChildren().add(roundedRectangle);
                } else {
                    // Update properties for existing rectangle
                    roundedRectangle.setX(x * factor);
                    roundedRectangle.setY(y * factor);
                    roundedRectangle.setWidth(width * factor);
                    roundedRectangle.setHeight(height * factor);
                    roundedRectangle.setArcWidth(arcWidth * factor);
                    roundedRectangle.setArcHeight(arcHeight * factor);
                    roundedRectangle.setFill(fillcolor);
                    if (blur) {
                        roundedRectangle.setEffect(blurEffect);
                    }
                    if (bloom) {
                        roundedRectangle.setEffect(bloomEffect);
                    }
                }
                modifiedidbuffer.add(id);
            });
        }
    }


    public void horizontalGradient(String id, final double x, final double y, final double width, final double height, final java.awt.Color leftColor, final java.awt.Color rightColor) {
        if (isopen) {
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Rectangle rect = (Rectangle) shapesMap.get(id);

                if (rect == null) {
                    rect = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                    shapesMap.put(id, rect);
                    rect.setMouseTransparent(true);
                    root.getChildren().add(rect);
                }

                Color fxLeftColor = convertColor(leftColor);
                Color fxRightColor = convertColor(rightColor);
                LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null, new Stop(0, fxLeftColor), new Stop(1, fxRightColor));
                rect.setFill(gradient);

                rect.setX(x * factor);
                rect.setY(y * factor);
                rect.setWidth(width * factor);
                rect.setHeight(height * factor);

                modifiedidbuffer.add(id);
            });
        }
    }


    public void rectangle(String id, final double x, final double y, final double width, final double height, final java.awt.Color color) {
        if (isopen) {
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Rectangle rect = (Rectangle) shapesMap.get(id);

                if (rect == null) {
                    rect = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                    shapesMap.put(id, rect);
                    rect.setMouseTransparent(true);
                    root.getChildren().add(rect);
                } else {
                    rect.setX(x * factor);
                    rect.setY(y * factor);
                    rect.setWidth(width * factor);
                    rect.setHeight(height * factor);
                }
                rect.setFill(convertColor(color));
                modifiedidbuffer.add(id);
            });
        }

    }

    public void rectangle(String id, final double x, final double y, final double width, final double height, final java.awt.Color color, boolean bloom, boolean Blur) {
        if (isopen) {
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Rectangle rect = (Rectangle) shapesMap.get(id);
                Bloom bloomEffect = new Bloom();
                GaussianBlur blurEffect = new GaussianBlur();
                if (rect == null) {
                    rect = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                    shapesMap.put(id, rect);
                    if (bloom) {
                        rect.setEffect(bloomEffect);
                    }
                    if (Blur) {
                        rect.setEffect(blurEffect);
                    }
                    rect.setMouseTransparent(true);
                    root.getChildren().add(rect);
                } else {
                    rect.setX(x * factor);
                    rect.setY(y * factor);
                    rect.setWidth(width * factor);
                    rect.setHeight(height * factor);
                    if (bloom) {
                        rect.setEffect(bloomEffect);
                    }
                    if (Blur) {
                        rect.setEffect(blurEffect);
                    }
                }
                rect.setFill(convertColor(color));
                modifiedidbuffer.add(id);
            });
        }

    }

    public void verticalGradient(String id, final double x, final double y, final double width, final double height, final java.awt.Color topColor, final java.awt.Color bottomColor) {
        if (isopen) {
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Rectangle rect = (Rectangle) shapesMap.get(id);

                if (rect == null) {
                    rect = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                    shapesMap.put(id, rect);
                    rect.setMouseTransparent(true);
                    root.getChildren().add(rect);

                } else {
                    rect.setX(x * factor);
                    rect.setY(y * factor);
                    rect.setWidth(width * factor);
                    rect.setHeight(height * factor);
                }

                Color fxTopColor = convertColor(topColor);
                Color fxBottomColor = convertColor(bottomColor);
                LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, fxTopColor), new Stop(1, fxBottomColor));
                rect.setFill(gradient);

                modifiedidbuffer.add(id);
            });
        }

    }

    private Color convertColor(java.awt.Color color) {
        return new Color(
                color.getRed() / 255.0,
                color.getGreen() / 255.0,
                color.getBlue() / 255.0,
                color.getAlpha() / 255.0
        );
    }

    // Convert ARGB integer color to JavaFX Color
    private Color convertARGBtoColor(int argb) {
        double alpha = (argb >> 24 & 0xFF) / 255.0;
        double red = (argb >> 16 & 0xFF) / 255.0;
        double green = (argb >> 8 & 0xFF) / 255.0;
        double blue = (argb & 0xFF) / 255.0;
        return new Color(red, green, blue, alpha);
    }

    // Adapted drawBorderedRect for JavaFX
    public void drawBorderedRect(String id, double x, double y, double width, double height, double lineSize, int borderColor, int fillColor) {
        if (isopen) {
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();

                // Main rectangle
                Rectangle mainRect = (Rectangle) shapesMap.computeIfAbsent(id, k -> new Rectangle());
                mainRect.setX(x * factor);
                mainRect.setY(y * factor);
                mainRect.setWidth(width * factor);
                mainRect.setHeight(height * factor);
                mainRect.setFill(convertARGBtoColor(fillColor));
                if (!root.getChildren().contains(mainRect)) {
                    mainRect.setMouseTransparent(true);
                    root.getChildren().add(mainRect);
                }

                // Border rectangles
                String[] borderIds = {id + "_top", id + "_left", id + "_right", id + "_bottom"};
                double[][] borderDimensions = {
                        {x, y, width, lineSize}, // Top
                        {x, y, lineSize, height}, // Left
                        {(x + width - lineSize), y, lineSize, height}, // Right
                        {x, (y + height - lineSize), width, lineSize} // Bottom
                };

                for (int i = 0; i < borderIds.length; i++) {
                    Rectangle borderRect = (Rectangle) shapesMap.computeIfAbsent(borderIds[i], k -> new Rectangle());
                    borderRect.setX(borderDimensions[i][0] * factor);
                    borderRect.setY(borderDimensions[i][1] * factor);
                    borderRect.setWidth(borderDimensions[i][2] * factor);
                    borderRect.setHeight(borderDimensions[i][3] * factor);
                    borderRect.setFill(convertARGBtoColor(borderColor));
                    if (!root.getChildren().contains(borderRect)) {
                        borderRect.setMouseTransparent(true);
                        root.getChildren().add(borderRect);
                    }
                }

                modifiedidbuffer.add(id);
                modifiedidbuffer.addAll(Arrays.asList(borderIds));
            });
        }
    }

    public void drawTextWithBox(String id, double x, double y, String text, java.awt.Color backgroundcolor, java.awt.Color textColor, double boxWidth, double boxHeight, double boxarc, double fontSize) {
        drawTextWithBox(id, x, y, text, backgroundcolor, textColor, boxWidth, boxHeight, boxarc, fontSize, false, false);
    }

    public void drawTextWithBox(String id, double x, double y, String text, java.awt.Color backgroundcolor, java.awt.Color textColor, double boxWidth, double boxHeight, double boxarc, double fontSize, boolean blur, boolean bloom) {
        if (isopen) {
            Color newbackgroundcolor = convertColor(backgroundcolor);
            roundedRectangle(id + "_bgbox", x, y, boxWidth, boxHeight, boxarc, boxarc, backgroundcolor, blur, bloom);
            Platform.runLater(() -> {
                GaussianBlur blurEffect = new GaussianBlur();
                Bloom bloomEffect = new Bloom();
                Color newtextColor = convertColor(textColor);
                Text textNode = (Text) shapesMap.get(id + "_text");
                if (textNode == null) {
                    textNode = new Text(text);
                    textNode.setFont(javafx.scene.text.Font.font("Arial", fontSize));
                    textNode.setFill(newtextColor);
                    if (blur) {
                        textNode.setEffect(blurEffect);
                    }
                    if (bloom) {
                        textNode.setEffect(bloomEffect);
                    }
                    shapesMap.put(id + "_text", textNode);
                    root.getChildren().add(textNode);
                } else {
                    textNode.setText(text);
                    textNode.setFont(javafx.scene.text.Font.font("Arial", fontSize));
                    textNode.setFill(newtextColor);
                    if (blur) {
                        textNode.setEffect(blurEffect);
                    }
                    if (bloom) {
                        textNode.setEffect(bloomEffect);
                    }
                }

                // Calculate position of the text within the box
                double textX = x + (boxWidth - textNode.getBoundsInLocal().getWidth()) / 2;
                double textY = y + (boxHeight - textNode.getBoundsInLocal().getHeight()) / 2 + fontSize; // Add fontSize to align to the bottom

                // Position the text
                textNode.setX(textX);
                textNode.setY(textY);

            });
        }

    }

    public void drawText(String id, double x, double y, String text, java.awt.Color color, double fontSize) {
        if (isopen) {
            Platform.runLater(() -> {
                Text textNode = (Text) shapesMap.get(id);
                if (textNode == null) {
                    textNode = new Text(text);
                    textNode.setFont(javafx.scene.text.Font.font("Arial", fontSize));
                    textNode.setFill(convertColor(color));
                    shapesMap.put(id, textNode);
                    root.getChildren().add(textNode);
                } else {
                    textNode.setText(text);
                    textNode.setFont(javafx.scene.text.Font.font("Arial", fontSize));
                    textNode.setFill(convertColor(color));
                }

                textNode.setX(x);
                textNode.setY(y);
            });
        }
    }
}