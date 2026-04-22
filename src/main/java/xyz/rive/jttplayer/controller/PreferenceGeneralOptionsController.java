package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.common.GeneralOptions;

import java.net.URL;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.common.Constants.*;

public class PreferenceGeneralOptionsController extends CommonController {
    @FXML
    private VBox general_options;
    @FXML
    private CheckBox allow_tray_icon;
    @FXML
    private CheckBox allow_metadata_auto_switch;
    @FXML
    private Spinner<Integer> metadata_auto_switch_interval;
    @FXML
    private CheckBox allow_win_auto_attach;
    @FXML
    private Spinner<Integer> win_auto_attach_limit;
    @FXML
    private Label version;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, general_options);
        loadContent();
    }

    private void loadContent() {
        GeneralOptions options = getConfiguration().getGeneralOptions();
        allow_tray_icon.selectedProperty().addListener((o, ov, nv) -> {
            options.setAllowTrayIcon(nv);
            getMenuManager().setupSystemTray();
        });

        allow_metadata_auto_switch.selectedProperty().addListener((o, ov, nv) -> {
            options.setAllowMetadataAutoSwitch(nv);
            getControllerManager().updateTrackMetadata();
        });

        metadata_auto_switch_interval.valueProperty().addListener((o, ov, nv) -> {
            options.setMetadataAutoSwitchInterval(nv);
            getControllerManager().updateTrackMetadata();
        });

        allow_win_auto_attach.selectedProperty().addListener((o, ov, nv) -> {
            options.setAllowWinAutoAttach(nv);
        });

        win_auto_attach_limit.valueProperty().addListener((o, ov, nv) -> {
            options.setWinAutoAttachLimit(nv);
        });

        version.setText(VERSION);
    }

    private void setupData() {
        GeneralOptions options = getConfiguration().getGeneralOptions();
        allow_tray_icon.setSelected(options.isAllowTrayIcon());
        allow_metadata_auto_switch.setSelected(options.isAllowMetadataAutoSwitch());
        metadata_auto_switch_interval.getEditor().setText(
                String.valueOf(options.getMetadataAutoSwitchInterval())
        );
        allow_win_auto_attach.setSelected(options.isAllowWinAutoAttach());
        win_auto_attach_limit.getEditor().setText(
                String.valueOf(options.getWinAutoAttachLimit())
        );
    }

    @Override
    public void afterShowView() {
        setupData();
    }

    public void visitAuthor(MouseEvent event) {
        consumeEvent(event);
        context.browseUrl(GITHUB_AUTHOR);
    }

    public void visitReleases(MouseEvent event) {
        consumeEvent(event);
        context.browseUrl(GITHUB_RELEASES);
    }

    public void visitLicense(MouseEvent event) {
        consumeEvent(event);
        context.browseUrl(LICENSE_AGPL_V3);
    }

    public void visitRepository(MouseEvent event) {
        consumeEvent(event);
        context.browseUrl(GITHUB_REPOSITORY);
    }
}
