package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.ActionSource;
import xyz.rive.jttplayer.common.PlaybackQueue;

import java.util.Optional;


public class SavePlaybackQueueAction extends AbstractMenuAction {

    public SavePlaybackQueueAction() {
        super();
    }

    public SavePlaybackQueueAction(ActionSource actionSource) {
        super(actionSource);
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        PlaybackQueue queue = isContextMenuAction() ?
                getContextMenuData(PlaybackQueue.class) :
                getPlayerManager().getCurrentPlaybackQueue();
        Optional.ofNullable(queue)
                .ifPresent(getPlayerManager()::savePlaybackQueue);
    }


}
