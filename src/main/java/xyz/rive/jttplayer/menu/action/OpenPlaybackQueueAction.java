package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import xyz.rive.jttplayer.common.PlaybackQueue;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static xyz.rive.jttplayer.common.Constants.*;

public class OpenPlaybackQueueAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择列表文件");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(PLAYBACK_QUEUE_SUFFIX_DESC,
                    PLAYBACK_QUEUE_SUFFIXES.stream().map("*"::concat)
                            .collect(Collectors.toCollection(ArrayList::new))
            )
        );
        File selection = chooser.showOpenDialog(getContext().getMainStage());
        if(selection == null) {
            return ;
        }
        PlaybackQueue queue = getContext().getPlaybackQueueService().restore(selection);
        getPlayerManager().appendToPlaybackQueue(queue);
    }

}
