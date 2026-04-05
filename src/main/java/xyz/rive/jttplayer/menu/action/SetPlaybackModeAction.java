package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.PlaybackMode;

public class SetPlaybackModeAction extends AbstractMenuAction {
    private final int mode;

    public SetPlaybackModeAction(PlaybackMode mode) {
        this.mode = mode.getValue();
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getPlayerManager().setPlaybackMode(mode);
    }

}
