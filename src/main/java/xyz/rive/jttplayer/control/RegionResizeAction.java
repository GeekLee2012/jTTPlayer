package xyz.rive.jttplayer.control;

import javafx.scene.layout.Region;
import xyz.rive.jttplayer.common.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Drag and Move
 */
public final class RegionResizeAction {
    private double fromX;
    private double fromY;
    private double fromWidth;
    private double fromHeight;

    private boolean enabled = false;
    private boolean resizing;

    private Function<Size, Boolean> resizableCheck;
    private Consumer<Size[]> onResizedHandler;
    private List<Object> excludes;

    public RegionResizeAction(Region target, Region trigger) {
        if(target == null || trigger == null) {
            return;
        }
        this.setupTrigger(target, trigger).setEnable(true);
    }

    public RegionResizeAction setEnable(boolean value) {
        this.enabled = value;
        return this;
    }

    public RegionResizeAction setResizableCheck(Function<Size, Boolean> resizableCheck) {
        this.resizableCheck = resizableCheck;
        return this;
    }

    public RegionResizeAction onResized(Consumer<Size[]> onResizedHandler) {
        this.onResizedHandler = onResizedHandler;
        return this;
    }

    public RegionResizeAction addExcludeRegion(Region region) {
        if(excludes == null) {
            excludes = new ArrayList<>();
        }
        excludes.add(region);
        return this;
    }

    private RegionResizeAction setupTrigger(Region target, Region trigger) {
        trigger.setOnMousePressed(e -> {
            e.consume();
            if(!enabled) {
                return ;
            }
            if(excludes != null && excludes.contains(e.getTarget())) {
                return ;
            }
            fromX = e.getScreenX();
            fromY = e.getScreenY();
            fromWidth = target.getWidth();
            fromHeight = target.getHeight();
        });

        trigger.setOnMouseDragged(e -> {
            e.consume();
            if(!this.enabled) {
                return ;
            }
            if(excludes != null && excludes.contains(e.getTarget())) {
                return ;
            }
            if(fromX < 0 || fromY < 0
                    || fromWidth < 0 || fromHeight < 0) {
                return ;
            }
            resizing = true;

            double offsetWidth = e.getScreenX() - fromX;
            double offsetHeight = e.getScreenY() - fromY;

            double toWidth = Math.max(fromWidth + offsetWidth, 0);
            double toHeight = Math.max(fromHeight + offsetHeight, 0);

            if(resizableCheck != null
                    && !resizableCheck.apply(new Size(toWidth, toHeight))) {
                return ;
            }

            target.setPrefSize(toWidth, toHeight);
            target.setMinWidth(toWidth);

            Optional.ofNullable(onResizedHandler).ifPresent(handler -> {
                Size[] sizes = {
                        new Size(fromWidth, fromHeight),
                        new Size(target.getWidth(), target.getHeight())
                };
                handler.accept(sizes);
            });
        });

        trigger.setOnMouseReleased(e -> {
            e.consume();
            if(resizing) {
                resizing = false;
            }
        });
        return this;
    }

}


