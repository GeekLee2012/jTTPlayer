package xyz.rive.jttplayer.menu.action;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.menu.MenuMeta;

import java.util.List;
import java.util.function.Function;

public class PlayFileAction extends OpenFilesAction {

    public PlayFileAction() {
        super(false);
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        getContext().runTask(() -> {
            try {
                Thread.sleep(1000);
                getPlayerManager().playLast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
