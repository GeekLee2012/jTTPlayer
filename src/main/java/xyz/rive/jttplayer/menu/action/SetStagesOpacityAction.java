package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class SetStagesOpacityAction extends AbstractMenuAction {
    private final double opacity;
    public SetStagesOpacityAction(double opacity) {
        this.opacity = opacity;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getStageManager().setStagesOpacity(opacity);
    }

}
