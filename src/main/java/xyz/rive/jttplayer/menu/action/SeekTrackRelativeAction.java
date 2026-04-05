package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class SeekTrackRelativeAction extends AbstractMenuAction {
    private final int seconds;

    public SeekTrackRelativeAction(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getPlayerManager().seekPlayRelative(seconds);
    }

}
