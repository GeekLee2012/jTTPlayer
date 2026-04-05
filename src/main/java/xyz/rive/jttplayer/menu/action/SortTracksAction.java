package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.SortBy;

import static xyz.rive.jttplayer.common.SortBy.None;
import static xyz.rive.jttplayer.common.SortBy.Title;

public class SortTracksAction extends AbstractMenuAction {
    private SortBy sortBy;

    public SortTracksAction() {
        this(Title);
    }

    public SortTracksAction(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        if(sortBy == getPlayerManager().getTrackSortBy()) {
            sortBy = None;
        }
        getPlayerManager().setTrackSortBy(sortBy);
    }

}
