package xyz.rive.jttplayer.menu.action;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.menu.MenuMeta;

import java.util.List;

public class SetVisualIndexAction extends AbstractMenuAction {
    private final int index;
    public SetVisualIndexAction(int index) {
        this.index = index;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getPlayerManager().setActiveVisualIndex(index);
    }
}
