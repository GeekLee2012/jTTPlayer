package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.Optional;

public class ShowStageAction extends AbstractMenuAction {
    private final Stage stage;
    public ShowStageAction(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Optional.ofNullable(stage).ifPresent(__ -> {
            stage.show();
            stage.requestFocus();
        });
    }

}
