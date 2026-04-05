package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class ToggleLyricStageAlwaysTopAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getStageManager().toggleLyricAlwaysTop();
    }

}
