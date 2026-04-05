package xyz.rive.jttplayer.controller;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValueBase;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import xyz.rive.jttplayer.anim.LyricAnimation;
import xyz.rive.jttplayer.common.Lyric;
import xyz.rive.jttplayer.common.Rect;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;
import xyz.rive.jttplayer.skin.StandaloneXml;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static javafx.scene.input.KeyCode.ESCAPE;
import static xyz.rive.jttplayer.common.Constants.APP_TITLE_VERSION;
import static xyz.rive.jttplayer.common.Constants.LYRIC;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class LyricController extends CommonController  {

    @FXML
    private AnchorPane lyric_view;
    @FXML
    private Region view_bg;
    @FXML
    private Region lyric_content_bg;
    @FXML
    private Region view_title;
    @FXML
    private Region top;
    @FXML
    private Region desklrc_btn;
    @FXML
    private Region ontop_btn;
    @FXML
    private Region close_btn;
    @FXML
    private ScrollPane lyric_pane;
    @FXML
    private VBox lyric_content;
    @FXML
    private HBox scroll_locator;
    @FXML
    private Label scroll_locator_time;
    private LyricAnimation animation;
    private boolean escPressed;
    private final long scrollFinishedThreshold = 1888;
    private ScheduledFuture<?> scrollFuture;
    private double lyricLeftAnchor = 6;
    private double lyricRightAnchor = 6;
    private ChangeListener<? super Number> titlePosListener;
    private boolean wordMode = false;

    private LyricAnimation getAnimation() {
        if(animation == null) {
            animation = new LyricAnimation(context, lyric_pane);
            animation.postHandle(__ -> setupLyricStyle());
        }
        return animation;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, lyric_view, LYRIC);
        startLyric(false);
        setupListeners();
    }

    @Override
    public void afterShowView() {
        super.afterShowView();
        updateOntopState();
    }

    private void setupListeners() {
        onTrackChanged((o, ov, nv) -> startLyric(false));

        lyric_pane.heightProperty().addListener((o, ov, nv) -> {
            double halfHeight = (lyric_pane.getHeight() + top.getHeight()) / 2;
            AnchorPane.setTopAnchor(scroll_locator, halfHeight);
            AnchorPane.setTopAnchor(scroll_locator_time, halfHeight + 8);
            setupScrollLocatorTimeHorizontalAnchor();
        });

        top.heightProperty().addListener((o, ov, nv) -> {
            double halfHeight = (lyric_pane.getHeight() + top.getHeight()) / 2;
            AnchorPane.setTopAnchor(scroll_locator, halfHeight);
            AnchorPane.setTopAnchor(scroll_locator_time, halfHeight + 8);
            setupScrollLocatorTimeHorizontalAnchor();
        });

        //预留空白，用于播放到指定歌词行
        lyric_content.paddingProperty().bind(new ObservableValueBase<Insets>() {
            @Override
            public Insets getValue() {
                double halfHeight = lyric_pane.getHeight() / 2;
                return new Insets(halfHeight, 0, halfHeight, 0);
            }
        });

        lyric_pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lyric_pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lyric_content.setFillWidth(true);

        if(isMacOS()) {
            //macOS独有，体验较好
            setupMacOSLyricOnScrollByUser();
        } else {
            //通用方式，体验一般
            //需双击歌词行才能跳转播放
            setupUniversalLyricOnScrollByUser();
        }

        /*
        lyric_view.setOnKeyPressed(event -> {
            if(ESCAPE.getName().equalsIgnoreCase(event.getCode().getName())) {
                scroll_locator_time.setUserData(null);
                escPressed = true;
            }
        });
        */
    }

    private void setWordMode(boolean value) {
        wordMode = value;
    }

    private void onUserScrollStarted() {
        getAnimation().stop();
        scroll_locator.setVisible(true);
        scroll_locator_time.setVisible(true);
    }

    private void onUserScrollFinished() {
        if(lyric_content.getChildren().size() <= 1) {
            return ;
        }
        /*if(!escPressed) {
            if(!getPlayerManager().isLyricOffsetSetByMouseWheelMode()) {
                seekScrollLocator();
            }
            getAnimation().startLine(getUserData(scroll_locator_time, Node.class));
        }*/
        if(!getPlayerManager().isLyricOffsetSetByMouseWheelMode()) {
            seekScrollLocator();
        }
        getAnimation().startLine(getUserData(scroll_locator_time, Node.class));
        resetScrollStates();
    }

    private void resetScrollStates() {
        getAnimation().start();
        scroll_locator.setVisible(false);
        scroll_locator_time.setVisible(false);
        scroll_locator_time.setUserData(null);
        escPressed = false;
    }

    private void setupUniversalLyricOnScrollByUser() {
        lyric_content.setOnScroll(event -> {
            if(lyric_content.getChildren().size() <= 1) {
                return ;
            }

            //Started
            if(scrollFuture != null) {
                scrollFuture.cancel(true);
            }
            onUserScrollStarted();

            //Scrolling
            setupScrollLocatorTime();

            //Finished
            scrollFuture = runDelay(() -> {
                consumeEvent(event);
                //runFx(this::onUserScrollFinished);
                runFx(this::resetScrollStates);
                scrollFuture = null;
            }, scrollFinishedThreshold);
        });
    }

    private void setupMacOSLyricOnScrollByUser() {
        //平台兼容性问题 - 非macOS平台不支持ScrollStarted
        lyric_content.setOnScrollStarted(event -> {
            if(lyric_content.getChildren().size() <= 1) {
                return ;
            }
            onUserScrollStarted();
        });

        lyric_content.setOnScroll(event -> {
            if(lyric_content.getChildren().size() <= 1) {
                return ;
            }
            setupScrollLocatorTime();
        });

        //平台兼容性问题 - 非macOS平台不支持ScrollFinished
        lyric_content.setOnScrollFinished(event -> {
            consumeEvent(event);
            onUserScrollFinished();
        });
    }

    public void refreshView() {
        lyric_view.requestLayout();
    }

    private void setupScrollLocatorTime() {
        List<Node> lines = lyric_content.getChildren();
        Node currentLine = null;
        for (Node line : lines) {
            line.getStyleClass().remove("locator_current");
            if (intersects(scroll_locator, line)) {
                if(currentLine == null) {
                    currentLine = line;
                }
            }
        }

        if(currentLine != null) {
            currentLine.getStyleClass().add("locator_current");
            String key = (String) currentLine.getUserData();
            if(isEmpty(key)) {
                return ;
            }
            scroll_locator_time.setUserData(currentLine);
            scroll_locator_time.setText(toMMss(toMillis(key) / 60000D));

            if(getPlayerManager().isLyricOffsetSetByMouseWheelMode()) {
                long lineTime = toMillis(trim(key));
                long current = (long) (getPlayerManager().getTimePosition() * 1000L);
                long offset = current - lineTime;

                setLyricOffset(offset);
            }
        }
    }

    private void seekScrollLocator() {
        seekLine(getUserData(scroll_locator_time, Node.class));
    }

    private void seekLine(Node line) {
        seekLine(line, 0);
    }

    private void seekLine(Node line, long delay) {
        if(line == null) {
            return ;
        }
        String key = (String) line.getUserData();
        if(key == null) {
            return ;
        }
        long millis = toMillis(key) - getAnimation().getOffset() - delay;
        getPlayerManager().seekPlay((int) (millis / 1000D));
    }


    public void startLyric(boolean reset) {
        Optional.ofNullable(getCurrentTrack()).ifPresent(track -> {
            if(reset) {
                track.setLyric(null);
            }
            getTrackService().loadLyricSync(track);
            startLyricNow();
        });
    }

    public void startLyricNow() {
        runFx(() -> {
            setupLyric();
            getAnimation().start();
        });
    }

    private void setupLyric() {
        cancelLyric();
        Optional.ofNullable(getCurrentTrack()).ifPresent(track -> {
            Lyric lyric = null;
            if(track.hasEmbedLyric()) {
                lyric = track.getLyricEmbed();
            } else if(track.hasLyric()) {
                lyric = track.getLyric();
            }
            if(lyric == null || !lyric.hasData()) {
                return ;
            }
            setWordMode(lyric.isWordMode());
            lyric.getData().forEach(this::addLine);

            setLyricOffset(lyric.getOffsetAsNumber());
            setupLyricTrans(track);
            setupLyricStyle();
            switchZhLyric();
        });
    }

    public void setupLyricTrans(Track track) {
        if(track == null) {
            return;
        }
        Lyric lyricTrans = track.getLyricTrans();
        if(lyricTrans == null || !lyricTrans.hasData()) {
            return ;
        }

        lyric_content.getChildren().forEach(item -> {
            String key = (String) item.getUserData();
            long millis = toMillis(key);
            if(millis < 0) {
                return ;
            }
            Map<String, String> transData = lyricTrans.getData();
            for (Map.Entry<String, String> entry : transData.entrySet()) {
                long transMillis = toMillis(entry.getKey());
                if(Math.abs(millis - transMillis) <= 100) {
                    String transText = entry.getValue();
                    if(!isEmpty(transText) && !"//".equals(transText)) {
                        Label extra = new Label(transText);
                        extra.getStyleClass().add("extra_text");
                        VBox line = (VBox)item;
                        line.getChildren().add(extra);
                    }
                    break;
                }
            }
        });
    }

    public void showLyricMenu(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY) {
           getLyricContextMenu().show(event);
        } else {
            hideAllMenus(event);
        }
    }

    private void addLine(String key, String value) {
        Label text = new Label(wordMode ? Lyric.mergeWordTokens(value) : value);
        text.getStyleClass().add("text");

        VBox line = new VBox();
        line.getChildren().add(text);

        line.setFillWidth(true);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setUserData(key);
        line.getStyleClass().add("line");
        lyric_content.getChildren().add(line);

        if (!isMacOS()) {
            line.setOnMouseEntered(event -> {
                line.setCursor(Cursor.HAND);
            });

            line.setOnMouseExited(event -> {
                line.setCursor(Cursor.DEFAULT);
            });

            line.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() > 1) {
                    seekLine(line);
                    getAnimation().startLine(line);
                    resetScrollStates();
                    consumeEvent(event);
                }
            });
        }
    }

    private void addLine2(String key, String value) {
        VBox line = new VBox();
        line.setFillWidth(true);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setUserData(key);
        line.getStyleClass().add("line");
        lyric_content.getChildren().add(line);

        if (wordMode) {
            List<Lyric.WordToken> tokens = Lyric.toWordTokens(value);
            if (tokens != null) {
                TextFlow textFlow = new TextFlow();
                textFlow.getStyleClass().add("word-flow");
                line.getChildren().add(textFlow);

                tokens.forEach(token -> {
                    Text text = new Text(token.value);
                    text.setUserData(token);
                    text.getStyleClass().add("word");
                    textFlow.getChildren().add(text);
                });
                //成功则返回，失败就退回逐行模式
                return ;
            }
        }

        Label text = new Label(value);
        text.getStyleClass().add("text");
        line.getChildren().add(text);
    }

    public void cancelLyric() {
        lyric_content.getChildren().clear();
        setWordMode(false);
        Track track = getCurrentTrack();
        String key = "";
        String value = APP_TITLE_VERSION;
        if(track != null) {
            value = track.basicMetadata();
        }
        addLine(key, value);
        setupLyricStyle();
        switchZhLyric();
    }

    public void switchZhLyric() {
        lyric_content.getChildren().forEach(item -> {
            Set<Node> nodes = item.lookupAll(".line Label");
            if(nodes == null || nodes.isEmpty()) {
                return ;
            }
            nodes.forEach(node -> {
                if(node instanceof Label) {
                    Label line = (Label) node;
                    String text = line.getText();
                    if (getPlayerOptions().getLyricZhType() > 1) {
                        text = ZhConverterUtil.toTraditional(text);
                    } else {
                        text = ZhConverterUtil.toSimple(text);
                    }
                    line.setText(text);
                }
            });
        });
    }

    public void setLyricOffset(long value) {
        Optional.ofNullable(getCurrentTrack()).ifPresent(track -> {
            Lyric lyric = null;
            if(track.hasLyric()) {
                lyric = track.getLyric();
            } else if(track.hasEmbedLyric()) {
                lyric = track.getLyricEmbed();
            }
            if(lyric != null) {
                lyric.setOffset(String.valueOf(value));
            }
        });
        getAnimation().setOffset(value);
    }

    public void toggleLyricOntop() {
        getStageManger().toggleLyricAlwaysTop();
    }

    public void updateOntopState() {
        ontop_btn.getStyleClass().remove("active");
        boolean alwaysOnTop = getPlayerOptions().isLyricViewAlwaysOnTop();
        getStageManger().getLyricStage().setAlwaysOnTop(alwaysOnTop);
        if(alwaysOnTop) {
            ontop_btn.getStyleClass().add("active");
        }
    }

    public void setupLyricStyle() {
        Pos[] positions = {
                Pos.CENTER_LEFT,
                Pos.CENTER,
                Pos.CENTER_RIGHT
        };
        TextAlignment[] textAlignments = {
                TextAlignment.LEFT,
                TextAlignment.CENTER,
                TextAlignment.RIGHT
        };
        int alignment = getPlayerManager().getLyricAlignment();
        int spacing = getPlayerManager().getLyricLineSpacing();
        String textColorNormal = getPlayerManager().getLyricColorNormal();
        String textColorHilight = getPlayerManager().getLyricColorHilight();
        String bgColor = getPlayerManager().getLyricBackgroundColor();
        int fontSize = getPlayerManager().getLyricFontSize() > 0 ?
                getPlayerManager().getLyricFontSize() : 14;
        int hilightFontSize = getPlayerManager().getLyricHilightFontSize() > 0 ?
                getPlayerManager().getLyricHilightFontSize() : 16;
        if(!isEmpty(bgColor)) {
            lyric_content_bg.setStyle(
                    String.format("-fx-background-color:%s;", bgColor)
            );
        }
        lyric_content.getChildren().forEach(item -> {
            VBox line = (VBox) item;
            line.setAlignment(positions[alignment]);
            line.setStyle(
                    String.format("-fx-padding: 0 5 %s 5;", spacing)
            );
            boolean isActive = line.getStyleClass().contains("active");
            String textFill = isActive ? textColorHilight : textColorNormal;
            int textSize = isActive ? hilightFontSize : fontSize;

            line.lookupAll("Label").forEach(node -> {
                Label label = (Label) node;
                label.setStyle(
                        String.format("-fx-text-fill: %s; -fx-font-size: %s;",
                                textFill, textSize)
                );
                label.setAlignment(positions[alignment]);
                label.setTextAlignment(textAlignments[alignment]);
            });
            /*
            line.lookupAll("TextFlow").forEach(node -> {
                TextFlow textFlow = (TextFlow) node;
                textFlow.setTextAlignment(textAlignments[alignment]);
            });
            line.lookupAll("Text").forEach(node -> {
                Text text = (Text) node;
                //boolean isHilight = text.getStyleClass().contains("hilight");
                //String wordFill = isHilight ? textColorHilight : textColorNormal;
                text.setStyle(
                        String.format("-fx-fill: %s; -fx-font-size: %s;",
                                textFill, textSize)
                );
                //text.setAlignment(positions[alignment]);
                //text.setTextAlignment(textAlignments[alignment]);
            });
            */
        });

        setupScrollLocatorTimeHorizontalAnchor();
    }

    private void setupScrollLocatorTimeHorizontalAnchor() {
        int alignment = getPlayerManager().getLyricAlignment();
        Double leftAnchor = null;
        Double rightAnchor = null;
        int offset = 6;
        if(alignment == 2) {
            leftAnchor = lyricLeftAnchor + offset;
        } else {
            rightAnchor = lyricRightAnchor + offset;
        }
        AnchorPane.setLeftAnchor(scroll_locator_time, leftAnchor);
        AnchorPane.setRightAnchor(scroll_locator_time, rightAnchor);
    }

    public void adjustLyricStyle() {
        getAnimation().stop();
        setupLyricStyle();
        getAnimation().start();
    }

    public void seekOnPaused(int seconds) {
        getAnimation().scrollLyric(seconds);
    }

    public void showDesktopLyric(MouseEvent event) {
        consumeEvent(event);
        getStageManger().toggleLyricDesktopShow();
    }

    @Override
    public void setupSkin() {
        super.setupSkin();
        setItemsHidden(desklrc_btn, ontop_btn);

        SkinXml skin = getActiveSkinXml();
        SkinXmlWindowItem winItem = skin.getLyricWindow();
        //Rect resizeRect = winItem.getWindowCornersRect();
        //setCssBorderImage(view_bg, getSkinEntryPath(skin, winItem.image), resizeRect);
        //top.setPrefHeight(resizeRect.x1());

        winItem.items.forEach(item -> {
            if(item.isDesklrcItem()) {
                setItemsVisible(desklrc_btn);
                setAnchorAuto(desklrc_btn, skin, item, winItem);
            } else if (item.isOntopItem()) {
                setItemsVisible(ontop_btn);
                setAnchorAuto(ontop_btn, skin, item, winItem);
            } else if (item.isCloseItem()) {
                setAnchorAuto(close_btn, skin, item, winItem);
            } else if(item.isLyricItem()) {
                Rect rect = item.getPositionRect();

                setAnchorAll(lyric_content_bg,
                        rect.y1(),
                        winItem.width() - rect.x2(),
                        winItem.height() - rect.y2(),
                        rect.x1()
                );
                setAnchorAll(lyric_pane,
                        rect.y1(),
                        winItem.width() - rect.x2(),
                        winItem.height() - rect.y2(),
                        rect.x1());

                setAnchorHorizontal(scroll_locator, rect.x1(), winItem.width() - rect.x2());
                setLyricHorizontalAnchor(rect.x1(), winItem.width() - rect.x2());
            } else if (item.isTitleItem()) {
                setPrefSize(view_title, item.size());
                //居中显示
                if(item.isAlignCenter()) {
                    AnchorPane.setTopAnchor(view_title, (double) item.y1);
                    AnchorPane.setLeftAnchor(view_title,
                            (getStage().getWidth() - item.width()) / 2D);

                    if (titlePosListener != null) {
                        getStage().widthProperty().removeListener(titlePosListener);
                    }

                    titlePosListener = (o, ov, nv) -> {
                        AnchorPane.setTopAnchor(view_title, (double) item.y1);
                        AnchorPane.setLeftAnchor(view_title,
                                (getStage().getWidth() - item.width()) / 2D);
                    };

                    getStage().widthProperty().addListener(titlePosListener);
                } else {
                    if(titlePosListener != null) {
                        getStage().widthProperty().removeListener(titlePosListener);
                        titlePosListener = null;
                    }
                    setAnchorAuto(view_title, skin, item, winItem);
                }
            }
        });

        StandaloneXml lyricXml = context.getActiveLyricXml();
        getPlayerManager().setLyricBackgroundColor(lyricXml.bkgndColor)
                .setLyricColorNormal(lyricXml.textColor)
                .setLyricColorHilight(lyricXml.hilightColor);
    }

    private void setLyricHorizontalAnchor(double left, double right) {
        lyricLeftAnchor = left;
        lyricRightAnchor = right;
    }

}