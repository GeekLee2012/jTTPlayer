package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.Track;

import java.util.Optional;

import static xyz.rive.jttplayer.util.FxUtils.*;

public class ShowInFolderAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        Optional.ofNullable(getContextMenuData(Track.class))
                .ifPresent(track -> showInFolder(track.getUrl()));
    }


}
