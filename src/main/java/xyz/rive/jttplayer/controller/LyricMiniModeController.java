package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.common.Lyric;
import xyz.rive.jttplayer.common.Track;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import static xyz.rive.jttplayer.common.Constants.APP_TITLE_VERSION;
import static xyz.rive.jttplayer.util.StringUtils.*;


public class LyricMiniModeController extends CommonController {
    @FXML
    private AnchorPane lyric_mini_mode;
    @FXML
    private VBox lyric_content;
    @FXML
    private Label lyric_line;
    private Lyric lyric;
    private boolean wordMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, lyric_mini_mode);
        setupListeners();
    }

    private void setupListeners() {
        onTrackChanged((o, ov, nv) -> loadLyric());
        onTimePosition((o, ov, nv) -> renderLyric((Double) nv));
        lyric_line.prefHeightProperty().bind(lyric_content.prefHeightProperty().add(4));
        loadLyric();
    }

    private void setWordMode(boolean value) {
        wordMode = value;
    }

    public void loadLyric() {
        cancelLyric();
        Optional.ofNullable(getCurrentTrack()).ifPresent(track -> {
            getTrackService().loadLyricSync(track);

            if(track.hasLyric()) {
                lyric = track.getLyric();
            } else if(track.hasEmbedLyric()) {
                lyric = track.getLyricEmbed();
            }
            if(lyric == null || !lyric.hasData()) {
                return ;
            }
            setWordMode(lyric.isWordMode());
            renderLyric(getPlayerManager().getTimePosition());
        });
    }

    public void cancelLyric() {
        Track track = getCurrentTrack();
        String text = track != null ? track.basicMetadata() : APP_TITLE_VERSION;
        updateLyricLine(text);
        setWordMode(false);
        lyric = null;
    }

    private void renderLyric(Double seconds) {
        if(seconds == null || seconds < 0) {
            return ;
        }
        if(lyric == null || !lyric.hasData()) {
            return ;
        }
        long offset = lyric.getOffsetAsNumber();
        long trackTime = (long) (seconds * 1000L - offset);
        String currentKey = null;
        for(String key : lyric.getData().keySet()) {
            if(isEmpty(key)) {
                continue ;
            }
            long millis = toMillis(key);
            if(trackTime < millis) {
                break;
            }
            currentKey = key;
        }
        Optional.ofNullable(currentKey)
                .ifPresent(key -> updateLyricLine(lyric.getData().get(key)));
    }

    private void updateLyricLine(String text) {
        if(isEmpty(text)) {
           return ;
        }
        runFx(() -> {
            lyric_line.setText(wordMode ?
                    Lyric.mergeWordTokens(trim(text)) :
                    trim(text));
        });
    }

}
