package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.PlaybackQueue;

public class RenamePlaybackQueueAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        PlaybackQueue queue = getContextMenuData(PlaybackQueue.class);
        if(queue != null) {
            getStageManager().getRenamePlaybackQueueStage().show();
            getControllerManager().renamePlaybackQueue(queue);
        }
    }
}
