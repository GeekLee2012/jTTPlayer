package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class OpenFolderAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择文件夹");
        File selection = chooser.showDialog(getContext().getMainStage());
        getContext().getTrackService().appendToPlaybackQueue(selection);
    }

}
