package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceVisualOptionsController extends CommonController {

    @FXML
    private VBox visual_options;
    @FXML
    private ComboBox<String> visual_types;
    @FXML
    private ComboBox<String> visual_speed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, visual_options);
        loadContent();
    }

    public void loadContent() {
        visual_types.getItems().setAll(
                "专辑封面",
                "频谱分析 <默认>",
                "频谱分析 <细线>",
                "频谱分析 <火焰>",
                "频谱分析 <能量>",
                "示波显示 <单波>",
                "示波显示 <双波>",
                "示波显示 <衍变>"
        );

        visual_types.getSelectionModel().select(
                getPlayerManager().getActiveVisualIndex()
        );

        //监听
        visual_types.valueProperty().addListener((o, ov, nv) -> {
            int index = visual_types.getItems().indexOf(nv);
            getPlayerManager().setActiveVisualIndex(index);
        });

    }

    @Override
    public void afterShowView() {
        loadContent();
    }
}
