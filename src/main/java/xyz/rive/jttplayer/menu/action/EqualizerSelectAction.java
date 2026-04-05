package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class EqualizerSelectAction extends AbstractMenuAction {
    private final int index;

    public EqualizerSelectAction(int index) {
        this.index = index;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getPlayerManager().setCurrentEqualizerIndex(index);
    }
}
