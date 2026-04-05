package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;

import static xyz.rive.jttplayer.common.Constants.*;


public class AddPlaybackQueueAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择列表文件");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        PLAYBACK_QUEUE_SUFFIX_DESC,
                        PLAYBACK_QUEUE_SUFFIX_PATTERN)
        );
        File selection = chooser.showOpenDialog(getContext().getMainStage());
        if(selection == null) {
            return ;
        }
        getPlayerManager().addPlaybackQueue(selection);
    }
}
