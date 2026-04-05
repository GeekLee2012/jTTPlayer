package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import xyz.rive.jttplayer.common.PlaybackQueue;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PlaybackQueueSelectionController extends CommonController {

    @FXML
    private BorderPane playback_queue_selection_view;
    @FXML
    private ListView<Label> playback_queue_list;

    private PlaybackQueue selectedPlaybackQueue;

    private Consumer<PlaybackQueue> okAction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, playback_queue_selection_view);
    }

    private void setSelectedPlaybackQueue(PlaybackQueue queue) {
        this.selectedPlaybackQueue = queue;
    }

    public void loadContent() {
        playback_queue_list.getItems().clear();
        setSelectedPlaybackQueue(null);
        Optional.ofNullable(context.getPlaybackQueueService().listAll())
                .ifPresent(list -> {
                    list.forEach(item -> {
                        Label label = new Label(item.getName());
                        label.setOnMouseClicked(event -> {
                            setSelectedPlaybackQueue(item);

                            //DblClick
                            if(event.getButton() == MouseButton.PRIMARY
                                    && event.getClickCount() >= 2) {
                                applyClose(event);
                            }
                        });
                        playback_queue_list.getItems().add(label);
                    });
                });
    }


    public void applyClose(MouseEvent event) {
        consumeEvent(event);
        closeView();
        Optional.ofNullable(okAction)
                .ifPresent(__ -> okAction.accept(selectedPlaybackQueue));
    }

    public void createPlaybackQueue(MouseEvent event) {
        consumeEvent(event);
        getPlayerManager().createPlaybackQueue(null);
        loadContent();
    }

    public void setOkAction(Consumer<PlaybackQueue> okAction) {
        this.okAction = okAction;
    }

    @Override
    public void afterShowView() {
        super.afterShowView();
        loadContent();
    }
}
