package xyz.rive.jttplayer.menu.strategy;

import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import xyz.rive.jttplayer.common.Bound;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.menu.PopMenu;

import static xyz.rive.jttplayer.util.FxUtils.getScreenBound;

/** 在正下方显示 */
public class ShowUnderItemStrategy implements ShowStrategy {
    private final double offsetX;
    private final double offsetY;
    private final boolean allowUp;

    public ShowUnderItemStrategy() {
        this(0, 0);
    }

    public ShowUnderItemStrategy(double offsetX, double offsetY) {
        this(offsetX, offsetY, false);
    }

    public ShowUnderItemStrategy(double offsetX, double offsetY, boolean allowUp) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.allowUp = allowUp;
    }

    @Override
    public Position getPosition(InputEvent event, PopMenu popMenu) {
        double nX = -1;
        double nY = -1;
        Region source = (Region) event.getSource();
        //double width = source.getWidth();
        double height = source.getHeight();
        if(event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            double screenX = mouseEvent.getScreenX();
            double screenY = mouseEvent.getScreenY();
            nX = screenX - x + offsetX;
            nY = screenY + height - y + offsetY;

            if(allowUp) {
                Bound bound = getScreenBound();
                double maxWidth = bound.getWidth();
                double maxHeight = bound.getHeight();
                double offsetHeight = nY + popMenu.getHeight() - maxHeight;
                //显示在正上方
                if(offsetHeight >= -88) {
                    nY = screenY - y - popMenu.getHeight() - offsetY;
                }
                return new Position(nX, nY);
            }
        }
        return adjustPosition(new Position(nX, nY), event, popMenu);
    }
}
