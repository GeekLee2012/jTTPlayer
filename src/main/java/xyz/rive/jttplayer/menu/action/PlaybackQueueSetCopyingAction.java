package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class PlaybackQueueSetCopyingAction extends AbstractMenuAction {
    private final boolean moving;

    public PlaybackQueueSetCopyingAction() {
        this(false);
    }

    public PlaybackQueueSetCopyingAction(boolean moving) {
        this.moving = moving;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getControllerManager().setupPlaybackQueueCopying(moving);
    }
}
