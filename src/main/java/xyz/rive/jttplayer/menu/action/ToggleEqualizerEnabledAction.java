package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class ToggleEqualizerEnabledAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getPlayerManager().toggleEqualizerEnabled();
    }
}
