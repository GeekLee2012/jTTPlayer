package xyz.rive.jttplayer.menu.strategy;

import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.menu.PopMenu;

/** 在右边显示 */
public class ShowItemRightStrategy implements ShowStrategy {
    private double offsetX;
    private double offsetY;

    public ShowItemRightStrategy() {
        this(0, 0);
    }

    public ShowItemRightStrategy(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public Position getPosition(InputEvent event, PopMenu popMenu) {
        double nX = -1;
        double nY = -1;
        Region source = (Region) event.getSource();
        double width = source.getWidth();
        double height = source.getHeight();
        if(event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            double screenX = mouseEvent.getScreenX();
            double screenY = mouseEvent.getScreenY();
            nX = screenX + width - x + offsetX;
            nY = screenY - y + offsetY;
        }
        return adjustPosition(new Position(nX, nY), event, popMenu);
    }
}
