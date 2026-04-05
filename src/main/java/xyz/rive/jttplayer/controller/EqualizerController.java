package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import xyz.rive.jttplayer.control.SliderHorizontal;
import xyz.rive.jttplayer.control.SliderVertical;
import xyz.rive.jttplayer.menu.strategy.ShowItemRightStrategy;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.rive.jttplayer.common.Constants.EQUALIZER;
import static xyz.rive.jttplayer.skin.SkinXmlWindowItem.*;
import static xyz.rive.jttplayer.util.FxUtils.*;

public class EqualizerController extends CommonController {

    @FXML
    private AnchorPane equalizer_view;
    @FXML
    private Region top;
    @FXML
    private Region close_btn;
    @FXML
    private SliderHorizontal balance;
    @FXML
    private SliderHorizontal surround;
    @FXML
    private SliderVertical preamp;
    @FXML
    private Region equalizer_enabled_btn;
    @FXML
    private Region equalizer_reset_btn;
    @FXML
    private Region equalizer_profile_btn;
    private boolean equalizerBandsInit = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, equalizer_view, EQUALIZER);

        onEqualizerEnabled(__ -> setupEqualizerBands());
        onEqualizerIndexChanged(__ -> setupEqualizerBands());
        setupEqualizerBands();
    }

    private Set<Node> getEqualizerBands() {
        return equalizer_view.lookupAll(".eq_band");
    }

    private void setupEqualizerBands() {
        runFx(() -> {
            updateEqualizerEnabledBtn();
            updateEqualizerBands();
        });
    }

    private void updateEqualizerEnabledBtn() {
        boolean enabled = getPlayerManager().isEqualizerEnabled();
        equalizer_enabled_btn.getStyleClass().removeAll("active");
        if(enabled) {
            equalizer_enabled_btn.getStyleClass().add("active");
        }
    }

    public void resetEqualizer(MouseEvent event) {
        getPlayerManager().resetEqualizer();
    }

    public void showEqualizerProfile(MouseEvent event) {
        getEqualizerContextMenu()
                .setShowStrategy(new ShowItemRightStrategy(0, -2))
                .setEvent(event)
                .toggle();
    }

    private void updateEqualizerBands() {
        boolean enabled = getPlayerManager().isEqualizerEnabled();
        int[] values = getPlayerManager().getEqualizerValues();
        Set<Node> vSliderBands = getEqualizerBands();
        AtomicInteger count = new AtomicInteger();
        vSliderBands.forEach(band -> {
            SliderVertical vSlider = (SliderVertical) band;
            int index = count.getAndIncrement();
            vSlider.setValue(toSliderValue(values[index]));
            vSlider.setEnabled(enabled);
            setupTip(vSlider, toEqualizerValue(vSlider.getValue()) + "");

            if(!equalizerBandsInit) {
                vSlider.valueProperty().addListener((o, ov, nv) -> {
                    switchToCustomEqualizer(vSlider.shouldNotifyChanged(), index);
                });

                vSlider.slidingProperty().addListener((o, ov, nv) -> {
                    switchToCustomEqualizer(vSlider.shouldNotifyChanged(), index);
                });
            }
        });
        equalizerBandsInit = true;

        preamp.setEnabled(true);
    }

    private void switchToCustomEqualizer(boolean enabled, int index) {
        if(!enabled) {
            return ;
        }
        int[] values = getEqualizerBandsValues();
        if(getPlayerManager().isCustomEqualizer()) {
            getPlayerManager().setCustomEqualizer(values);
        } else {
            int[] newValues = getPlayerManager().getEqualizerValues();
            newValues[index] = values[index];
            getPlayerManager().setCustomEqualizer(newValues);
        }
    }

    private int[] getEqualizerBandsValues() {
        Set<Node> vSliderBands = getEqualizerBands();
        AtomicInteger index = new AtomicInteger();
        int[] values = new int[10];
        vSliderBands.forEach(band -> {
            SliderVertical vSlider = (SliderVertical) band;
            values[index.getAndIncrement()] = toEqualizerValue(vSlider.getValue());
        });
        return values;
    }

    private double toSliderValue(double value) {
        value = (value + 12D) / 24D * 100D;
        value = Math.max(0, value);
        value = Math.min(100, value);
        return value;
    }

    private int toEqualizerValue(double value) {
        int eq = (int)((value - 50D)/ 50D * 12D);
        eq = Math.max(-12, eq);
        eq = Math.min(12, eq);
        return eq;
    }

    public void showNavigationContextMenu(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY) {
            getEqualizerNavigationContextMenu()
                    .setMenuList(getMenuTemplates().getEqualizerMenuList())
                    .show(event);
        } else {
            hideAllMenus(event);
        }
    }

    @Override
    public void setupSkin() {
        super.setupSkin();
        SkinXml skin = getActiveSkinXml();
        SkinXmlWindowItem winItem = skin.getEqualizerWindow();
        if (winItem == null) {
            return;
        }

        winItem.items.forEach(item -> {
            if (item.isCloseItem()) {
                setAnchorAuto(close_btn, skin, item, winItem);
            } //
            else if (item.isBalanceItem()) {
                setPrefSize(balance, item.size());
                setAnchorAuto(balance, skin, item, winItem);
                balance.getStylesheets().setAll(
                        getTssManager().boostrapSlider(skin, item)
                );
            } else if (item.isSurroundItem()) {
                setPrefSize(surround, item.size());
                setAnchorAuto(surround, skin, item, winItem);
                surround.getStylesheets().setAll(
                        getTssManager().boostrapSlider(skin, item)
                );
            } else if (item.isPreampItem()) {
                setAnchorAlignBottomLeft(preamp, item.x1, winItem.height() - item.y2);
                setPrefSize(preamp, item.size());
                preamp.getStylesheets().setAll(
                        getTssManager().boostrapVSlider(skin, item)
                );
            } //
            else if (item.isEqfactorItem()) {
                Set<Node> vSliderBands = getEqualizerBands();
                AtomicInteger count = new AtomicInteger(0);
                vSliderBands.forEach(band -> {
                    SliderVertical vSlider = (SliderVertical) band;
                    int index = count.getAndIncrement();
                    int offset = index * (item.width() + winItem.eq_interval);
                    //setAnchorDefault(vSlider, item.x1 + offset, item.y1);
                    setAnchorAlignBottomLeft(vSlider, item.x1 + offset , winItem.height() - item.y2);
                    setPrefSize(vSlider, item.size());
                    vSlider.getStylesheets().setAll(
                            getTssManager().boostrapVSlider(skin, item)
                    );
                });
            } //
            else if (item.isEnabledItem()) {
                setAnchorAuto(equalizer_enabled_btn, skin, item, winItem);
            } else if (item.isResetItem()) {
                setAnchorAuto(equalizer_reset_btn, skin, item, winItem);
            } else if (item.isProfileItem()) {
                setAnchorAuto(equalizer_profile_btn, skin, item, winItem);
            }
        });
    }
}