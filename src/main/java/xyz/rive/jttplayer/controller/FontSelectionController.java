package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import xyz.rive.jttplayer.common.FontOption;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.StringUtils.*;

public class FontSelectionController extends CommonController {

    @FXML
    private BorderPane font_selection_view;
    @FXML
    private TextField font_family;
    @FXML
    private ListView<Label> font_family_list;
    @FXML
    private TextField font_weight;
    @FXML
    private ListView<Label> font_weight_list;
    @FXML
    private TextField font_size;
    @FXML
    private ListView<Label> font_size_list;
    @FXML
    private Label sample_zh;
    @FXML
    private Label sample_en;
    @FXML
    private Label sample_num;
    private Consumer<FontOption> okAction;
    private FontOption option;
    private static final int DEFAULT_SIZE = 14;
    private int defaultFontSize = -1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, font_selection_view);

        setupFontFamilies();
        setupFontSizes();
        //setupFontWeights(null);
    }

    private void setupFontFamilies() {
        for(String name : Font.getFamilies()) {
            if(name.startsWith(".")) {
                continue;
            }
            Label label = new Label(name);
            //label.setStyle(String.format("-fx-font-family: \"%s\"; ", name));
            label.prefWidthProperty().bind(font_family_list.widthProperty().add(-26));
            label.setOnMouseClicked(event -> {
                font_family.setText(name);
                updateSampleStyle();
                //setupFontWeights(name);
            });
            font_family_list.getItems().add(label);
        }
    }

    private void setupFontWeights(String name) {
        String[] fontWeights = { "普通", "粗体" };
        for(String fwName : fontWeights) {
            Label label = new Label(fwName);
            label.prefWidthProperty().bind(font_weight_list.widthProperty());
            label.setOnMouseClicked(event -> {
                font_weight.setText(fwName);
                updateSampleStyle();
            });
            font_weight_list.getItems().add(label);
        }
    }

    private void setupFontSizes() {
        Arrays.asList("8", "9", "10",
                "11", "12", "14",
                "16", "18", "20",
                "22", "24", "26",
                "28", "30", "32",
                "36", "48", "72",
                "初号", "小初",
                "一号", "小一",
                "二号", "小二",
                "三号", "小三",
                "四号", "小四",
                "五号", "小五",
                "六号", "小六",
                "七号", "八号").forEach(size -> {
            Label label = new Label(size);
            label.prefWidthProperty().bind(font_size_list.widthProperty().add(-26));
            label.setOnMouseClicked(event -> {
                font_size.setText(size);
                updateSampleStyle();
            });
            font_size_list.getItems().add(label);
        });
    }

    private void updateSampleStyle() {
        String fontFamily = font_family.getText();
        String fontWeight = font_weight.getText();
        String fontSize = font_size.getText();

        String style = "";
        if(!isEmpty(fontFamily)) {
            style += String.format("-fx-font-family: \"%s\"; ",
                    trim(fontFamily));
        }

        if(!isEmpty(fontWeight)) {
            style += String.format("-fx-font-weight: %s; ",
                    contentEquals(fontWeight, "粗体") ? "bold" : "normal");
        }

        if(!isEmpty(fontSize)) {
            style += String.format("-fx-font-size: %s;",
                    trim(fontSize));
        }

        if(!isEmpty(style)) {
            sample_zh.setStyle(style);
            sample_en.setStyle(style);
            sample_num.setStyle(style);
        }
    }

    private void setupFontOption() {
        String family = trim(font_family.getText());
        String weight = trim(font_weight.getText());
        int size = parseInt(trim(font_size.getText()), getDefaultSize());

        if (option == null) {
            option = new FontOption();
        }
        option.setFamily(family);
        option.setWeight(weight);
        option.setSize(size);
    }

    public void applyClose(MouseEvent event) {
        consumeEvent(event);
        setupFontOption();

        closeView();
        Optional.ofNullable(okAction)
                .ifPresent(action -> action.accept(option));
    }

    public void setupFont(String family, String weight, int size) {
        setupFont(family, weight, size, 14);
    }

    public void setupFont(String family, String weight, int size, int defaultSize) {
        font_family.setText(family);
        font_weight.setText(weight);
        font_size.setText(String.valueOf(size));
        defaultFontSize = defaultSize;

        Arrays.asList(font_family_list, font_weight_list, font_size_list)
                        .forEach(list -> list.getSelectionModel().clearSelection());

        updateSampleStyle();
    }

    public void setOkAction(Consumer<FontOption> action) {
        this.okAction = action;
    }

    private int getDefaultSize() {
        return defaultFontSize >= 9 ? defaultFontSize : DEFAULT_SIZE;
    }


}
