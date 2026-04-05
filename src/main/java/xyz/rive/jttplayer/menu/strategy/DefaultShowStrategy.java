package xyz.rive.jttplayer.menu.strategy;

import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.menu.PopMenu;

/** 在点击处显示 */
public class DefaultShowStrategy implements ShowStrategy {

    @Override
    public Position getPosition(InputEvent event, PopMenu popMenu) {
        double x = -1;
        double y = -1;
        double offsetX = 5;
        if(event instanceof ContextMenuEvent) {
            ContextMenuEvent ctxMenuEvent = (ContextMenuEvent) event;
            x = ctxMenuEvent.getScreenX();
            y = ctxMenuEvent.getScreenY();
        } else if(event instanceof MouseEvent ) {
            MouseEvent mouseEvent = (MouseEvent) event;
            x = mouseEvent.getScreenX();
            y = mouseEvent.getScreenY();
        }
        return adjustPosition(new Position(x + offsetX, y), event, popMenu);
    }
}
