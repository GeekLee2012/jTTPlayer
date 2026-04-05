package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.PlaybackQueue;

import java.util.Optional;

public class SwitchToPlaybackQueueAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        Optional.ofNullable(getContextMenuData(PlaybackQueue.class))
                .ifPresent(getControllerManager()::switchPlaybackQueue);
    }

}
