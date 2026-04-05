package xyz.rive.jttplayer.control;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;

public class BorderTitleView extends VBox {
    private Label titleLbl;

    public BorderTitleView() {
        init();
    }

    private void init() {
        setSpacing(3);
        getStyleClass().setAll("border_title_view");
        getStylesheets().setAll(getCssUrl("border-title-view"));

        titleLbl = new Label();
        titleLbl.getStyleClass().setAll("title");

        getChildren().add(titleLbl);
    }

    public String getTitle() {
        return titleLbl.getText();
    }

    public void setTitle(String title) {
        titleLbl.setText(title);
    }
}
