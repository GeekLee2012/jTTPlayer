package xyz.rive.jttplayer.menu.strategy;

import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.menu.PopMenu;

public class SubmenuShowStrategy implements ShowStrategy {

    @Override
    public Position getPosition(InputEvent event, PopMenu popMenu) {
        double nX = -1;
        double nY = -1;
        if(event instanceof MouseEvent) {
            HBox source = (HBox) event.getSource();
            double width = source.getWidth();
            //double height = source.getHeight();

            MouseEvent mouseEvent = (MouseEvent) event;
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();

            double screenX = mouseEvent.getScreenX();
            double screenY = mouseEvent.getScreenY();
            nX = screenX + width - x + 1;
            nY = screenY - y - 1;
        }
        return adjustPosition(new Position(nX, nY), event, popMenu);
    }

}
