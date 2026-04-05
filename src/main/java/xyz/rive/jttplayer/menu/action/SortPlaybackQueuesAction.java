package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class SortPlaybackQueuesAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getPlayerManager().togglePlaybackQueueSortByName();
        getPlayerManager().sortPlaybackQueues();
    }

}
