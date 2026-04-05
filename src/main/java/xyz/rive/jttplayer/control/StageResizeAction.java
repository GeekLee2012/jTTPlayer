package xyz.rive.jttplayer.control;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import xyz.rive.jttplayer.common.Bound;
import xyz.rive.jttplayer.common.Size;

import java.util.Optional;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.FxUtils.getScreenBound;

/**
 * Drag and Move
 */
public final class StageResizeAction {
    private double fromX;
    private double fromY;
    private double fromWidth;
    private double fromHeight;
    private double fromStageX;
    private double fromStageY;

    private boolean enabled = false;
    private boolean alwaysOnTop;
    private boolean resizing;

    private double minWidth = -1;
    private double maxWidth = -1;

    private double minHeight = -1;
    private double maxHeight = -1;
    private final static int NONE = -1;
    private final static int HORIZONTAL = 0;
    private final static int HORIZONTAL_LEFT = 1;
    private final static int VERTICAL_TOP = 2;
    private final static int VERTICAL = 3;
    private final static int SOUTH_EAST = 4;
    private int direction = NONE;

    private Consumer<Size[]> onResizedHandler;

    public StageResizeAction(Stage stage) {
        if(stage == null) {
            return;
        }
        this.setupTrigger(stage).setEnable(true);
    }

    public StageResizeAction setEnable(boolean value) {
        this.enabled = value;
        return this;
    }

    public StageResizeAction setMinSize(double width, double height) {
        this.minWidth = width;
        this.minHeight = height;
        return this;
    }

    public StageResizeAction setMaxSize(double width, double height) {
        this.maxWidth = width;
        this.maxHeight = height;
        return this;
    }

    public StageResizeAction onResized(Consumer<Size[]> onResizedHandler) {
        this.onResizedHandler = onResizedHandler;
        return this;
    }

    private StageResizeAction setupTrigger(Stage stage) {
        Node trigger = stage.getScene().getRoot();
        trigger.setOnMouseMoved(e -> {
            e.consume();
            //Cursor
            resetCursorStyle(stage);
            if(!this.enabled) {
                return ;
            }

            String styleClass = "h_resize";
            switch (getDirection(stage, e)) {
                case NONE:
                    return ;
                case VERTICAL:
                case VERTICAL_TOP:
                    styleClass = "v_resize";
                    break;
                case SOUTH_EAST:
                    styleClass = "se_resize";
                    break;
            }

            stage.getScene().getRoot()
                    .getStyleClass()
                    .addAll(styleClass);
        });

        trigger.setOnMousePressed(e -> {
            e.consume();
            if(!enabled) {
                return ;
            }

            alwaysOnTop = stage.isAlwaysOnTop();
            fromX = e.getScreenX();
            fromY = e.getScreenY();
            fromWidth = stage.getWidth();
            fromHeight = stage.getHeight();
            fromStageX = stage.getX() + stage.getWidth();
            fromStageY = stage.getY();
            direction = getDirection(stage, e);
        });

        trigger.setOnMouseDragged(e -> {
            e.consume();
            if(!this.enabled) {
                return ;
            }
            if (direction == NONE) {
                return ;
            }

            resizing = true;

            stage.setAlwaysOnTop(true); //移动时保持置顶

            double offsetWidth = e.getScreenX() - fromX;
            double offsetHeight = e.getScreenY() - fromY;
            if(direction == HORIZONTAL_LEFT) {
                offsetWidth *= -1;
            }
            if(direction == VERTICAL_TOP) {
                offsetHeight *= -1;
            }

            double toWidth = fromWidth + offsetWidth;
            double toHeight = fromHeight + offsetHeight;

            Bound bound = getScreenBound();
            if (maxWidth < 0) maxWidth = bound.getWidth();
            if (maxHeight < 0) maxHeight = bound.getHeight();

            if(minWidth > -1) toWidth = Math.max(minWidth, toWidth);
            if(minHeight > -1) toHeight = Math.max(minHeight, toHeight);
            if(maxWidth > -1) toWidth = Math.min(maxWidth, toWidth);
            if(maxHeight > -1) toHeight = Math.min(maxHeight, toHeight);

            if(direction == HORIZONTAL || direction == SOUTH_EAST) {
                stage.setWidth(toWidth);
            } else if(direction == HORIZONTAL_LEFT) {
                stage.setWidth(toWidth);
                stage.setX(fromStageX - toWidth);
            }
            if(direction == VERTICAL || direction == SOUTH_EAST) {
                stage.setHeight(toHeight);
            } else if(direction == VERTICAL_TOP) {
                stage.setHeight(toHeight);
                stage.setY(fromStageY - offsetHeight);
            }

            Optional.ofNullable(onResizedHandler).ifPresent(handler -> {
                Size[] sizes = {
                        new Size(fromWidth, fromHeight),
                        new Size(stage.getWidth(), stage.getHeight())
                };
                handler.accept(sizes);
            });
        });

        trigger.setOnMouseReleased(e -> {
            e.consume();
            resetCursorStyle(stage);
            if(resizing) {
                stage.setAlwaysOnTop(alwaysOnTop);
                resizing = false;
            }
        });
        return this;
    }

    private int getDirection(Stage stage, MouseEvent event) {
        double x = event.getScreenX();
        double y = event.getScreenY();
        double minX = stage.getX() + stage.getWidth() - 5;
        double minY = stage.getY() + stage.getHeight() - 7;
        if(x >= stage.getX() && x <= (stage.getX() + 5)
                && y > stage.getY() && y <= minY) {
            return HORIZONTAL_LEFT;
        }
        if(x > stage.getX() && x <= minX
            && y >= stage.getY() && y <= (stage.getY() + 5)) {
            //return VERTICAL_TOP;
            //体验不佳，暂时禁用
            return NONE;
        }
        if(x < minX && y < minY) {
            return NONE;
        }
        if(y < stage.getY() + 30) {
            return NONE;
        }

        if(x >= minX && y >= minY) {
            return SOUTH_EAST;
        } else if(x < minX) {
            return VERTICAL;
        }
        return HORIZONTAL;
    }

    private void resetCursorStyle(Stage stage) {
        Optional.ofNullable(stage)
                .ifPresent(__ -> stage.getScene()
                        .getRoot()
                        .getStyleClass()
                        .removeAll("h_resize", "v_resize", "se_resize"));
    }

}


