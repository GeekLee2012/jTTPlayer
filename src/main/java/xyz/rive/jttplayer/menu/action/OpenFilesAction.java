package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.rive.jttplayer.common.Constants.AUDIO_SUFFIXES;

public class OpenFilesAction extends AbstractMenuAction {
    private boolean multi = true;
    private boolean canceled = false;

    public OpenFilesAction() {

    }

    public OpenFilesAction(boolean multi) {
        this.multi = multi;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void handle(MouseEvent event) {
        super.handle(event);
        setCanceled(false);

        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择音频文件");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("音频文件",
                        AUDIO_SUFFIXES.stream().map("*"::concat)
                                .collect(Collectors.toCollection(ArrayList::new))
                )
        );
        List<File> selections = null;
        if(multi) {
            selections = chooser.showOpenMultipleDialog(getContext().getMainStage());
        } else {
            File selection = chooser.showOpenDialog(getContext().getMainStage());
            if(selection != null) {
                selections = new ArrayList<>();
                selections.add(selection);
            }
        }
        if(selections == null || selections.isEmpty()) {
            setCanceled(true);
            return ;
        }
        getContext().getTrackService().appendToPlaybackQueue(selections);
    }
}
