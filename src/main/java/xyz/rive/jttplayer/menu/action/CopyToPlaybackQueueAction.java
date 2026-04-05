package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class CopyToPlaybackQueueAction extends AbstractMenuAction {
    private final boolean moving;

    public CopyToPlaybackQueueAction() {
        this(false);
    }

    public CopyToPlaybackQueueAction(boolean moving) {
        this.moving = moving;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getStageManager().getPlaybackQueueSelectionStage().show();
        getControllerManager().setupCopyTrackSelections(moving);
    }
}