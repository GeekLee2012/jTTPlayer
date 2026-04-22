package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.controller.SearchTrackResourceOnlineController;

import java.util.Optional;

public class ShowSearchOnlineStageAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getStageManager().getSearchTrackResourceOnlineStage().show();
        Optional.ofNullable(getControllerManager().getController(SearchTrackResourceOnlineController.class))
                .ifPresent(SearchTrackResourceOnlineController::onShown);
    }

}
