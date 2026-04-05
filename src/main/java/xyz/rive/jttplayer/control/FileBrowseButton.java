package xyz.rive.jttplayer.control;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;

public class FileBrowseButton extends Region {

    public FileBrowseButton() {
        getStylesheets().setAll(getCssUrl("file-browse-btn"));
        getStyleClass().add("file_browse_btn");
    }

}
