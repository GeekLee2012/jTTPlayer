package xyz.rive.jttplayer.util;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.*;
import xyz.rive.jttplayer.MainApplication;
import xyz.rive.jttplayer.common.Bound;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.common.Rect;
import xyz.rive.jttplayer.common.Size;
import xyz.rive.jttplayer.control.DndAction;
import xyz.rive.jttplayer.control.RegionResizeAction;
import xyz.rive.jttplayer.control.StageDnmAction;
import xyz.rive.jttplayer.control.StageResizeAction;
import xyz.rive.jttplayer.menu.PopMenu;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

@SuppressWarnings("unchecked")
public final class FxUtils {
    public final static Class<?> MAIN_CLAZZ = MainApplication.class;
    public final static String MAIN_PKG_SLASH = StringUtils.getPackageSlash(MAIN_CLAZZ);
    //private final static Logger LOGGER = Logger.getLogger(MAIN_CLAZZ.getName());
    private final static SecureRandom RANDOM = new SecureRandom();

    private FxUtils() {

    }

    public static void onProperty(BooleanProperty property, ChangeListener<? super Boolean> listener) {
        property.addListener(listener);
    }

    public static void onProperty(Property<Number> property, ChangeListener<? super Number> listener) {
        property.addListener(listener);
    }

    public static void onProperty(StringProperty property, ChangeListener<? super String> listener) {
        property.addListener(listener);
    }

    public static URL getResource(String name) {
        try {
            return MAIN_CLAZZ.getResource(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        try {
            return MAIN_CLAZZ.getResourceAsStream(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FXMLLoader createLoader(String resource) {
        return new FXMLLoader(getResource(resource));
    }

    public static <T> T loadResource(String name) {
        FXMLLoader loader = createLoader(name);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static StageDnmAction setupDnmAction(Stage stage, Node... triggers) {
        return new StageDnmAction(stage, triggers);
    }

    public static DndAction setupDndAction(Consumer<DndAction.DndContext> action, Node... triggers) {
        return new DndAction(action, triggers);
    }

    public static StageResizeAction setResizable(Stage stage) {
        return new StageResizeAction(stage);
    }

    public static RegionResizeAction setResizable(Region region, Region trigger) {
        return new RegionResizeAction(region, trigger);
    }

    public static Image getImage(String name) {
        URL url = getResource("images/" + name);
        if(url != null) {
            return new Image(url.toExternalForm());
        }
        return null;
    }

    public static URL getImageUrl(String name) {
        return getResource("images/" + name);
    }

    public static Image createImage(String url) {
        url = toExternalForm(url);
        return isEmpty(url) ? null : new Image(url);
    }

    public static Stage createStage(String fxml, Window owner,
                                    double width, double height) {
        return createStage(fxml, owner, null, width, height, null);
    }

    public static Stage createModalityStage(String fxml, Window owner,
                                            double width, double height, Modality modality) {
        return setupStageDefault(new Stage(), fxml, owner, null, width, height, null, modality);
    }

    public static Stage createModalityStage(String fxml, Window owner, String title,
                                            double width, double height, Modality modality) {
        return setupStageDefault(new Stage(), fxml, owner, title, width, height, null, modality);
    }

    public static Stage createStage(String fxml, Window owner, String title,
                                    double width, double height) {
        return createStage(fxml, owner, title, width, height, (String) null);
    }

    public static Stage createStage(String fxml, Window owner, String title,
                                    double width, double height, String icon) {
        return setupStageDefault(new Stage(), fxml, owner, title, width, height, icon);
    }

    public static Stage setupStageDefault(
            Stage stage, String fxml, Window owner, String title,
            double width, double height, String icon) {
        return setupStageDefault(stage, fxml, owner, title, width, height, icon, null);
    }

    public static Stage setupStageDefault(
            Stage stage, String fxml, Window owner, String title,
            double width, double height, String icon, Modality modality)  {
        setupStageCommon(stage, fxml, width, height);
        return setupStageCommon(stage, owner, title, icon, modality);
    }

    public static Stage setupStageCommon(
            Stage stage, String fxml, double width, double height) {
        return setupStageCommon(stage, stage.getScene(), fxml, null, width, height, null);
    }

    public static Stage setupStageCommon(
            Stage stage, Scene scene, String fxml, double width, double height) {
        return setupStageCommon(stage, scene, fxml, null, width, height, null);
    }

    public static Stage setupStageCommon(
            Stage stage, String fxml, String title, double width, double height) {
        return setupStageCommon(stage, stage.getScene(), fxml, title, width, height, null);
    }

    public static Stage setupStageCommon(
            Stage stage, String fxml, double width, double height,
            BiConsumer<MouseEvent, Position[]> onMoved) {
        return setupStageCommon(stage, stage.getScene(), fxml, null, width, height, null);
    }

    public static Stage setupStageCommon(
            Stage stage, Scene scene, String fxml, String title, double width, double height,
            BiConsumer<MouseEvent, Position[]> onMoved) {
        if(scene == null) {
            scene = new Scene(loadResource(fxml));
        }
        stage.setScene(scene);
        scene.setFill(null);

        Node rootNode = stage.getScene().getRoot();
        stage.getScene().setFill(null);
        Node triggerNode = rootNode.lookup("#top");
        Node exNode1 = null;
        Node exNode2 = null;
        Node exNode3 = null;
        Node exNode4 = null;
        Node exNode5 = null;
        Set<Node> extraTriggerNodes = rootNode.lookupAll(".dnm_trigger");
        if(triggerNode == null) {
            triggerNode = rootNode;
        }
        if(extraTriggerNodes != null) {
            Iterator<Node> iter = extraTriggerNodes.iterator();
            while (iter.hasNext()) {
                if(exNode1 == null) {
                    exNode1 = iter.next();
                } else if(exNode2 == null) {
                    exNode2 = iter.next();
                } else if(exNode3 == null) {
                    exNode3 = iter.next();
                } else if(exNode4 == null) {
                    exNode4 = iter.next();
                } else if(exNode5 == null) {
                    exNode5 = iter.next();
                } else {
                    break;
                }
            }
        }
        stage.setUserData(setupDnmAction(stage, triggerNode,
                exNode1, exNode2, exNode3,
                exNode4, exNode5)
                .onMoving(onMoved));

        stage.setTitle(title != null ? title : "jTTPlayer");

        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMinWidth(width);
        stage.setMinHeight(height);

        stage.setScene(scene);
        return stage;
    }

    public static Stage setupStageCommon(
            Stage stage, Window owner, String title, String icon, Modality modality)  {
        if (modality != null) {
            stage.initModality(modality);
        }
        try {
            stage.initStyle(StageStyle.TRANSPARENT);
            if(owner != null) {
                stage.initOwner(owner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(icon != null) {
            stage.getIcons().add(getImage(icon));
        }
        stage.setTitle(title != null ? title : "jTTPlayer");
        return stage;
    }


    public static void setBelowStage(Stage target, Stage refrence) {
        setBelowStage(target, refrence, 0, 0);
    }

    public static void setBelowStageCenterAlign(Stage target, Stage refrence) {
        setBelowStage(target,
                refrence,
                (refrence.getWidth() - target.getWidth()) / 2,
                0);
    }

    public static void setBelowStageCenterAlign(Stage target, Stage refrence, double offsetY) {
        setBelowStage(target,
                refrence,
                (refrence.getWidth() - target.getWidth()) / 2,
                offsetY);
    }

    public static void setBelowStage(Stage target, Stage refrence, double offsetX, double offsetY) {
        if(target == null || refrence == null) return;
        double rx = refrence.getX();
        double ry = refrence.getY();
        //double rwidth = refrence.getWidth();
        double rheight = refrence.getHeight();
        target.setX(rx + offsetX);
        target.setY(ry + rheight + offsetY);
    }

    public static void setBackgroundImage(Region region, String imageName) {
        setBackgroundImage(region, imageName, 0, 0);
    }

    public static void setBackgroundImage(Region region, String imageName, double hp, double vp) {
        Image image = getImage(imageName);
        BackgroundPosition bgPosition = new BackgroundPosition(
                Side.LEFT, hp, false,
                Side.TOP, vp, false);
        BackgroundImage bgImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                bgPosition,
                BackgroundSize.DEFAULT);
        Background bg = new Background(bgImage);
        region.setBackground(bg);
    }


    public static void runFx(Runnable task) {
        Platform.runLater(task);
    }

    public static Process runExec(String[] cmdArray) {
        try {
            return Runtime.getRuntime().exec(cmdArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getIconStyle(String name) {
        return getIconStyle(name, null, -1, -1);
    }

    public static String getIconStyle(String name, int position) {
        return getIconStyle(name, String.format("-%1$s 0", position * 16), -1, -1);
    }

    public static String getIconStyle(String name, String position) {
        return getIconStyle(name, position, -1, -1);
    }

    public static String getIconStyle(String name, int width, int height) {
        return getIconStyle(name, null, width, height);
    }

    public static String getIconStyle(String name, String position, int width, int height) {
        if(isEmpty(position)) {
            position = "0 0";
        }
        String style = String.format("-fx-background-image: url(\"%s/images/%s\");"
                        + "-fx-background-position: %s;",
                MAIN_PKG_SLASH,
                name,
                position);
        if(width > 0 && height > 0) {
            style += String.format("-fx-background-size: %s %s;", width, height);
        }
        return style;
    }

    public static String getCssUrl(String name) {
        return String.format("%s/css/%s.css", MAIN_PKG_SLASH, name);
    }

    public static String getSkinUrl(String name) {
        return String.format("%s/skin/%s", MAIN_PKG_SLASH, name);
    }

    public static String getResourceUrl(String name) {
        return String.format("%s/%s", MAIN_PKG_SLASH, name);
    }

    public static double easeInOutQuad(double currentTime, double startValue, double changeValue, double duration) {
        currentTime /= duration / 2D;
        if (currentTime < 1) {
            return changeValue / 2D * currentTime * currentTime + startValue;
        }
        currentTime--;
        return -changeValue / 2 * (currentTime * (currentTime - 2) - 1) + startValue;
    }

    public static String getOSName() {
        return System.getProperty("os.name");
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }

    public static String getUserHome() {
        return transformPath(System.getProperty("user.home"));
    }

    public static boolean isOS(String name) {
        return containsIgnoreCase(getOSName(), name);
    }

    public static boolean isWindows() {
        return isOS("win");
    }

    public static boolean isMacOS() {
        return isOS("mac");
    }

    public static boolean isLinux() {
        return isOS("linux");
    }

    public static boolean isMouseHover(PopMenu menu, double screenX, double screenY) {
        double x = menu.getX();
        double y = menu.getY();
        double width = menu.getWidth();
        double height = menu.getHeight();
        double maxX = x + width;
        double maxY = y + height;
        return (screenX >= x && screenX <= maxX)
                && (screenY >= y && screenY <= maxY);
    }

    public static void showInFolder(String url) {
        url = trim(url);
        if(isEmpty(url) || url.startsWith("http")) {
            return ;
        }

        String[] cmdArray = null;
        if(isMacOS()) {
            cmdArray = new String[] { "open", "-R", transformPath(url) };
        } else if(isWindows()) {
            cmdArray = new String[] {
                    "explorer",
                    "/select,",
                    "\"" + detransformPath(url) + "\""
            };
        } else if(isLinux()) {
            cmdArray = new String[] { "xdg-open", transformPath(url) };
        }
        // Linux is now supported
        if(cmdArray != null) {
            runExec(cmdArray);
        }
    }

    public static Tooltip setupTip(Node node, String text) {
        return setupTip(node, text, "#f0f0f0", "#777777", 12);
    }

    public static Tooltip setupTip(Node node, String text, String bgColor, String textColor, int fontSize) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setMaxWidth(314);
        //#dce9ed
        tooltip.setStyle(String.format("-fx-background-color: %s;"
                + "-fx-text-fill: %s;"
                + "-fx-wrap-text: true;"
                + "-fx-font-size: %s;",
                bgColor, textColor, fontSize));
        Tooltip.install(node, tooltip);
        return tooltip;
    }

    public static boolean intersects(Node target, Node refer) {
        if(target == null || refer == null) {
            return false;
        }
        return target.localToScene(target.getBoundsInLocal())
                .intersects(refer.localToScene(refer.getBoundsInLocal()));
    }

    public static boolean isAttached(Stage target, Stage refer) {
        if(target == null || refer == null) {
            return false;
        }
        double minX = target.getX();
        double minY = target.getY();
        double maxX = target.getX() + target.getWidth();
        double maxY = target.getY() + target.getHeight();

        double rMinX = refer.getX();
        double rMinY = refer.getY();
        double rMaxX = refer.getX() + refer.getWidth();
        double rMaxY = refer.getY() + refer.getHeight();

        //垂直方向
        if(isNear(rMaxY, minY) || isNear(rMinY, maxY)) {
            return true;
        }
        //水平方向
        if(isNear(rMaxX, minX) || isNear(rMinX, maxX)) {
            return true;
        }
        return false;
    }

    private static boolean isNear(double d1, double d2) {
        return Math.abs(d1 - d2) <= 1;
    }

    public static boolean isAttached(Bound target, Stage refer) {
        if(target == null || refer == null) {
            return false;
        }
        double minX = target.getX();
        double minY = target.getY();
        double maxX = target.getX() + target.getWidth();
        double maxY = target.getY() + target.getHeight();

        double rMinX = refer.getX();
        double rMinY = refer.getY();
        double rMaxX = refer.getX() + refer.getWidth();
        double rMaxY = refer.getY() + refer.getHeight();
        //垂直方向
        if(isNear(rMaxY, minY) || isNear(rMinY, maxY)) {
            return true;
        }
        //水平方向
        if(isNear(rMaxX, minX) || isNear(rMinX, maxX)) {
            return true;
        }
        return false;
    }

    public static boolean isAssignableFrom(Object data, Class<?> clazz) {
        if (data == null || clazz == null) {
            return false;
        }
        return data.getClass() == clazz
                || clazz.isAssignableFrom(data.getClass());
    }

    public static <T> T getUserData(Stage stage, Class<T> clazz) {
        if(stage != null) {
            Object data = stage.getUserData();
            if(isAssignableFrom(data, clazz)) {
                return (T) data;
            }
        }
        return null;
    }

    public static <T> T getUserData(Node node, Class<T> clazz) {
        if(node != null) {
            Object data = node.getUserData();
            if(isAssignableFrom(data, clazz)) {
                return (T) data;
            }
        }
        return null;
    }

    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    public static void setupStagePositionByOffset(Stage stage, boolean isShow, double offsetX, double offsetY) {
        if(isShow) {
            stage.setX(stage.getX() + offsetX);
            stage.setY(stage.getY() + offsetY);
        }
    }

    public static Bound getScreenBound() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        return new Bound(0, 0, bounds.getWidth(), bounds.getHeight());
    }

    public static Size getImageSize(InputStream in) {
        try {
            BufferedImage image = ImageIO.read(in);
            return new Size(image.getWidth(), image.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Size();
    }

    public static Size getImageSize2(InputStream in) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(in)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    int width = reader.getWidth(reader.getMinIndex());
                    int height = reader.getHeight(reader.getMinIndex());
                    return new Size(width, height);
                } finally {
                    reader.dispose();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Size();
    }

    //Skinnable
    public static void setBackgroundImage(Region region, byte[] data) {
        setBackgroundImage(region, data, 0, 0);
    }

    public static void setBackgroundImage(Region region, byte[] data, int x, int y) {
        if(region != null) {
            region.setBackground(new Background(
                    new BackgroundImage(
                            new Image(new ByteArrayInputStream(data)),
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            new BackgroundPosition(
                                    Side.LEFT, x, false,
                                    Side.TOP, y, false),
                            BackgroundSize.DEFAULT
                    ))
            );
        }
    }

    public static void setPrefSize(Region region, double width, double height) {
        runFx(() -> region.setPrefSize(width, height));
    }

    public static void setPrefSize(Region region, Size size) {
        setPrefSize(region, size.width(), size.height());
    }

    public static void setPrefSize(Canvas canvas, Size size) {
        canvas.setWidth(size.width());
        canvas.setHeight(size.height());
    }


    public static void setFitSize(ImageView image, double width, double height) {
        image.setFitWidth(width);
        image.setFitHeight(height);
    }

    public static void setAnchorDefault(Node node, double left, double top) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, null);
        AnchorPane.setBottomAnchor(node, null);
    }

    public static void setAnchorHorizontal(Node node, double left, double right) {
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, right);
        //AnchorPane.setTopAnchor(node, null);
        //AnchorPane.setBottomAnchor(node, null);
    }

    public static void setAnchorAlignRight(Node node, double right, double top) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setLeftAnchor(node, null);
        AnchorPane.setBottomAnchor(node, null);
    }

    public static void setAnchorAlignBottomLeft(Node node, double left, double bottom) {
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setTopAnchor(node, null);
        AnchorPane.setRightAnchor(node, null);
    }

    public static void setAnchorAlignBottomRight(Node node, double right, double bottom) {
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, null);
        AnchorPane.setTopAnchor(node, null);
    }

    public static void setAnchorAll(Node node, double top, double right, double bottom, double left) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);
    }

    public static void setCssBackgroundSize(Region region, double width, double height) {
        region.setStyle(String.format("-fx-background-size: %s %s;", width, height));
    }

    public static void setCssBackgroundSize(Region region, Size size) {
        setCssBackgroundSize(region, size.width(), size.height());
    }

    public static void setCssCursor(Region region, String cursor) {
        region.setStyle(String.format("-fx-cursor: %s;", cursor));
    }

    public static void resetCssCursor(Region region) {
        setCssCursor(region, "default");
    }

    public static void setCssBackgroundPosition(Region region, int x, int y) {
        region.setStyle(String.format("-fx-background-position: %s %s;", x, y));
    }

    public static void setCssBackgroundImage(Region region, String image) {
        region.setStyle(
                String.format("-fx-background-image: url(\"%s\");", encodeUrl(image))
        );
    }

    public static void setCssBackgroundImage(Region region, String image, double x, double y) {
        region.setStyle(
                String.format("-fx-background-image: url(\"%s\");"
                                + "-fx-background-position: %s %s;",
                        encodeUrl(image), x, y
                )
        );
    }

    public static void setCssBorderImage(
            Region region, String image,
            double top, double right, double bottom, double left) {
        region.setStyle(
                String.format("-fx-border-image-source: url(\"%s\");"
                                + "-fx-border-image-width: %s %s %s %s;"
                                + "-fx-border-image-slice: %s %s %s %s fill;",
                        encodeUrl(image),
                        top, right, bottom, left,
                        top, right, bottom, left
                )
        );
    }

    public static void setCssBorderImage(Region region, String image, Rect rect) {
        setCssBorderImage(region, image, rect.x1(), rect.y1(), rect.x2(), rect.y2());
    }

    public static void cropImage(String src, String dest, int slices, int index) {
        try {
            BufferedImage  original = ImageIO.read(new File(src));
            int width = original.getWidth() / slices;
            int height = original.getHeight();
            BufferedImage cropped = original.getSubimage(index * width, 0, width, height);
            String format = guessExtName(dest);
            ImageIO.write(cropped, format, new File(dest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
