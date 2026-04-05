package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.Track;

import java.util.Optional;

public class SetRatingAction extends AbstractMenuAction {
    private final String rating;

    public SetRatingAction(String rating) {
        this.rating = rating;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        Optional.ofNullable(getContextMenuData(Track.class))
                .ifPresent(track -> {
                    getContext().getMetadataService().writeRating(track, rating);
                });
    }

}
