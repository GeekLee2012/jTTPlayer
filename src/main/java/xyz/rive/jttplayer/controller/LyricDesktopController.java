package xyz.rive.jttplayer.controller;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.common.Lyric;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.control.StageDnmAction;
import xyz.rive.jttplayer.menu.strategy.SharedStrategies;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlItem;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;

import java.net.URL;
import java.util.*;

import static xyz.rive.jttplayer.common.Constants.APP_SLOGAN;
import static xyz.rive.jttplayer.util.FxUtils.getUserData;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class LyricDesktopController extends CommonController {

    @FXML
    private VBox lyric_desktop_view;
    @FXML
    private HBox header;
    @FXML
    private AnchorPane toolbar;
    @FXML
    private Region app_logo;
    public Region prev_btn;
    @FXML
    private Region play_btn;
    @FXML
    private Region pause_btn;
    @FXML
    private Region next_btn;
    @FXML
    private Region list_btn;
    @FXML
    private Region settings_btn;
    @FXML
    private Region kalaok_btn;
    @FXML
    private Region lines_btn;
    @FXML
    private Region lock_btn;
    @FXML
    private Region return_btn;
    @FXML
    private Region ontop_btn;
    @FXML
    private Region close_btn;
    private Lyric lyric;
    @FXML
    private VBox lyric_content;
    @FXML
    private Label line1;
    @FXML
    private Label extra1;
    @FXML
    private Label line2;
    @FXML
    private Label extra2;
    private boolean isDnmTriggersAdded = false;
    private boolean isAppMainContextMenuShowing = false;
    private boolean wordMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, lyric_desktop_view);
        setupListeners();
    }

    private void setupListeners() {
        onPlayState((o, ov, nv) -> updateActionsStates());
        onTrackChanged((o, ov, nv) -> loadLyric());
        onTimePosition((o, ov, nv) -> renderLyric((Double) nv));
        onLyricDesktopLocked(this::updateLyricDesktopLockState);
        loadLyric();
    }

    public void updateActionsStates() {
        /*
        int state = context.getPlayState();
        boolean playable = state == PlayState.PLAYING.getValue()
                || state == PlayState.PAUSED.getValue();
        */
        if(isPlaying()) {
            setItemsHidden(play_btn);
            setItemsVisible(pause_btn);
        } else {
            setItemsHidden(pause_btn);
            setItemsVisible(play_btn);
        }
    }

    public void toggleLyricDesktopContextMenu(MouseEvent event) {
        consumeEvent(event);
        if(event.getButton() == MouseButton.PRIMARY) {
            hideAllMenus(event);
        } else if(event.getButton() == MouseButton.SECONDARY) {
            getLyricDesktopContextMenu().show(event);
        }
    }

    public void toggleAppMenu(MouseEvent event) {
        consumeEvent(event);
        if(event.getButton() == MouseButton.PRIMARY) {
            getMenuManager().getAppMainContextMenu().setOnHidden(e -> {
                isAppMainContextMenuShowing = false;
            });
            if(getMenuManager().getAppMainContextMenu().isShowing()) {
                hideAllMenus(event);
            } else if (event.getClickCount() >= 2){
                showAppMenu(event, SharedStrategies.getSharedUnder());
                isAppMainContextMenuShowing = true;
            }
        } else if(event.getButton() == MouseButton.SECONDARY) {
            getLyricDesktopContextMenu().show(event);
        }
    }

    public void normalize(MouseEvent event) {
        consumeContextMenuEvent(event);
        getStageManager().toggleLyricDesktopShow();
    }


    public void closeLyric(MouseEvent event) {
        consumeContextMenuEvent(event);
        getStageManager().toggleLyricShow();
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

    public String getLyricTrans(String key) {
        Track track = getCurrentTrack();
        if(track == null) {
            return null;
        }
        Lyric lyricTrans = track.getLyricTrans();
        if(lyricTrans == null || !lyricTrans.hasData()) {
            return null;
        }
        long millis = toMillis(key);
        if(millis < 0) {
            return null;
        }
        Map<String, String> transData = lyricTrans.getData();
        for (Map.Entry<String, String> entry : transData.entrySet()) {
            long transMillis = toMillis(entry.getKey());
            if(Math.abs(millis - transMillis) <= 100) {
                String transText = trim(entry.getValue());
                boolean isTextValid = !isEmpty(transText) && !"//".equals(transText);
                return isTextValid ? transText : null;
            }
        }
        return null;
    }

    public void cancelLyric() {
        resetLyricLinesState();
        Track track = getCurrentTrack();
        String text = track != null ? track.basicMetadata() : APP_SLOGAN;
        runFx(() -> line1.setText(getZhLyricText(text)));
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
                        .ifPresent(this::updateLyricLines);
    }

    private String getZhLyricText(String text) {
        if(isEmpty(text)) {
            return null;
        }
        if (getPlayerOptions().getLyricZhType() > 1) {
            text = ZhConverterUtil.toTraditional(text);
        } else {
            text = ZhConverterUtil.toSimple(text);
        }
        return trim(text);
    }

    private void resetLyricLinesState() {
        setTextGradient(getPlayerManager().getLyricDesktopTextGradient());
        adjustLyricDesktopStyle();
        extra1.setManaged(false);
        extra1.setVisible(false);

        extra2.setManaged(false);
        extra2.setVisible(false);

        runFx(() -> {
            extra1.setText("");
            extra2.setText("");
        });
    }

    private void updateLyricLines(String key) {
        setTextGradient(getPlayerManager().getLyricDesktopTextGradient());
        runFx(() ->{
            if(lyric == null || !lyric.hasData()) {
                return ;
            }

            String text = getZhLyricText(lyric.getData().get(key));
            String extra = getZhLyricText(getLyricTrans(key));
            text = wordMode ? Lyric.mergeWordTokens(text) : text;
            line1.setText(text);

            if(!isEmpty(extra)) {
                extra = wordMode ? Lyric.mergeWordTokens(extra) : extra;
                extra1.setText(extra);
                extra1.setManaged(true);
                extra1.setVisible(true);
            }
            /*
            Stage stage = getStageManger().getLyricDesktopStage();
            stage.setHeight(extra1.isVisible() ?
                    Math.max(stage.getHeight(), 139)
                    : Math.min(stage.getHeight(), 99));
             */
            adjustLyricDesktopStyle();
        });
    }

    public void showToolbar(MouseEvent event) {
        consumeEvent(event);
        //header.setManaged(true);
        header.setVisible(true);
        lyric_content.getStyleClass().removeAll("transparent");
    }

    public void hideToolbar(MouseEvent event) {
        consumeEvent(event);
        //header.setManaged(false);
        header.setVisible(false);
        lyric_content.getStyleClass().setAll("transparent");
    }

    public void hideToolbarOnMouseExited(MouseEvent event) {
        if (isAppMainContextMenuShowing
                || getMenuManager().isLyricDesktopContextMenuShowing()
                || getMenuManager().isLyricDesktopTextSettingsPopMenuShowing()) {
            consumeEvent(event);
            return ;
        }
        hideToolbar(event);
    }

    public void toggleTextSettings(MouseEvent event) {
        //consumeEvent(event);
        getMenuManager().getLyricDesktopTextSettingsPopMenu().toggle(event);
    }

    public void setTextGradient(String styleClass) {
        if(isEmpty(styleClass)) {
            return ;
        }
        getPlayerManager().setLyricDesktopTextGradient(styleClass);
        Label[] lines = { line1, extra1, line2, extra2 };
        for (Label line : lines) {
            if (line != null && line.isVisible()
                    && line.getStyleClass() != null
                    && !line.getStyleClass().isEmpty()) {
                line.getStyleClass().setAll(styleClass);
            }
        }
    }

    public void switchZhLyric() {
        Label[] lines = { line1, extra1, line2, extra2 };
        for (Label line : lines) {
            if (line != null && line.isVisible()) {
                String text = line.getText();
                if (getPlayerOptions().getLyricZhType() > 1) {
                    text = ZhConverterUtil.toTraditional(text);
                } else {
                    text = ZhConverterUtil.toSimple(text);
                }
                line.setText(text);
            }
        }
    }

    public void toggleOnTop(MouseEvent event) {
        consumeEvent(event);
        getStageManager().toggleLyricAlwaysTop();
    }

    @Override
    public void afterShowView() {
        super.afterShowView();
        updateOntopState();
        if (!isDnmTriggersAdded) {
            StageDnmAction action = getUserData(getStageManager().getLyricDesktopStage(), StageDnmAction.class);
            if(action != null) {
                action.addTrigger(line1);
                action.addTrigger(extra1);
                action.addTrigger(line2);
                action.addTrigger(extra2);
                isDnmTriggersAdded = true;
            }
        }
    }

    public void updateOntopState() {
        ontop_btn.getStyleClass().remove("active");
        boolean alwaysOnTop = getConfiguration().getPlayerOptions().isLyricViewAlwaysOnTop();
        getStageManager().getLyricDesktopStage().setAlwaysOnTop(alwaysOnTop);
        if(alwaysOnTop) {
            ontop_btn.getStyleClass().add("active");
        }
    }

    private void updateLyricDesktopLockState(boolean locked) {
        //内部鼠标事件穿透，不响应鼠标事件以实现锁定效果
        //但JavaFX并不支持穿透到外部，所以仍会遮挡其他程序
        lyric_desktop_view.setMouseTransparent(locked);
        if (locked) {
            hideAllMenus(null);
            hideToolbar(null);
        }
    }

    public void lockLyric() {
        getStageManager().setLyricDesktopLocked(true);
    }

    public void adjustLyricDesktopStyle() {
        boolean textShadow = getPlayerManager().isLyricDesktopFontShadow();
        int fontSize = getPlayerManager().getLyricDesktopFontSize() > 0 ?
                getPlayerManager().getLyricDesktopFontSize() : 36;
        Label[] lines = { line1, extra1, line2, extra2 };
        for (Label line : lines) {
            if (line != null && line.isVisible()) {
                line.setStyle(String.format("-fx-text-shadow: %s;-fx-font-size: %s;",
                        textShadow, fontSize));
            }
        }
    }

    @Override
    public void afterCloseView() {
        super.afterCloseView();
        if(getPlayerManager().isLyricDesktopAutoUnlock()) {
            getStageManager().setLyricDesktopLocked(false);
        }
    }

    @Override
    public void setupSkin() {
        super.setupSkin();

        SkinXml skin = getActiveSkinXml();
        SkinXmlWindowItem barItem = skin.getLyricDesktopBar() ;
        if (barItem == null) {
            skin = getSkinManager().getDefaultSkinXml();
            barItem = skin.getLyricDesktopBar();
        }

        for (SkinXmlItem item : barItem.items) {
            if (item.isIconItem()) {
                setAnchorAuto(app_logo, skin, item, barItem);
            } else if (item.isPrevItem()) {
                setAnchorAuto(prev_btn, skin, item, barItem);
            } else if (item.isPlayItem()) {
                setAnchorAuto(play_btn, skin, item, barItem);
            } else if (item.isPauseItem()) {
                setAnchorAuto(pause_btn, skin, item, barItem);
            } else if (item.isNextItem()) {
                setAnchorAuto(next_btn, skin, item, barItem);
            } else if (item.isListItem()) {
                setAnchorAuto(list_btn, skin, item, barItem);
            } else if (item.isSettingsItem()) {
                setAnchorAuto(settings_btn, skin, item, barItem);
            } else if (item.isKalaokItem()) {
                setAnchorAuto(kalaok_btn, skin, item, barItem);
            } else if (item.isLinesItem()) {
                setAnchorAuto(lines_btn, skin, item, barItem);
            } else if (item.isLockItem()) {
                setAnchorAuto(lock_btn, skin, item, barItem);
            } else if (item.isOntopItem()) {
                setAnchorAuto(ontop_btn, skin, item, barItem);
            } else if (item.isReturnItem()) {
                setAnchorAuto(return_btn, skin, item, barItem);
            } else if (item.isCloseItem()) {
                setAnchorAuto(close_btn, skin, item, barItem);
            }
        }
    }
}
