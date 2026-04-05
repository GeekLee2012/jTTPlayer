package xyz.rive.jttplayer.control;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;
import static xyz.rive.jttplayer.util.FxUtils.runFx;

public class TabsView extends VBox {
    private HBox tabsBox;
    private Region activeTabBottom;
    private VBox container;
    private Consumer<Integer> tabChangedHandler;
    private double tabIndent = 2;
    private int activeIndex = 0;

    public TabsView() {
        init();
        createDefaultSkin();
    }

    private void init() {
        setMaxHeight(Double.MAX_VALUE);
        getStylesheets().add(getCssUrl("tabs-view"));
        getStyleClass().add("tabs_view");
    }

    protected void createDefaultSkin() {
        tabsBox = new HBox();
        tabsBox.getStyleClass().setAll("tabs");
        tabsBox.getChildren().addListener((ListChangeListener<Node>) c -> initTabs());

        activeTabBottom = new Region();
        activeTabBottom.getStyleClass().add("active_tab_bottom");

        container = new VBox(activeTabBottom);
        container.getStyleClass().add("tab_content");
        VBox.setVgrow(container, Priority.ALWAYS);
        container.getChildren().addListener((ListChangeListener<Node>) c -> {
            AtomicInteger count = new AtomicInteger(0);
            container.getChildren().forEach(item -> {
                int index = count.getAndIncrement();
                if(item == activeTabBottom) {
                    return ;
                }
                item.getStyleClass().setAll("content");
                item.setManaged(index <= 1);
                item.setVisible(index <= 1);
            });
        });

        getChildren().addAll(tabsBox, container);
    }

    public ObservableList<Node> getTabs() {
        return tabsBox.getChildren();
    }

    public ObservableList<Node> getContents() {
        return container.getChildren();
    }

    private void initTabs() {
        AtomicInteger count = new AtomicInteger(0);
        tabsBox.getChildren().forEach(tab -> {
            int index = count.getAndIncrement();
            tab.setOnMouseClicked(event -> setActiveIndex(index));
        });
    }

    private void setupTabHighlight() {
        if(tabsBox.getChildren().isEmpty()) {
            return ;
        }
        tabsBox.getChildren().forEach(tab -> {
            tab.getStyleClass().removeAll("active");
        });
        Region activeTab = (Region) tabsBox.getChildren().get(activeIndex);
        activeTab.getStyleClass().add("active");
        activeTabBottom.prefWidthProperty().bind(activeTab.widthProperty().add(-2));
        activeTabBottom.maxWidthProperty().bind(activeTab.widthProperty().add(-2));
        tabsBox.getChildren().get(0).setTranslateX(activeIndex > 0 ? tabIndent : 0);
        runFx(() -> {
            double offset = -12;
            for(int i = 0; i < activeIndex; i++) {
                Region tab = (Region) tabsBox.getChildren().get(i);
                offset += (tab.getWidth() - 1);
            }
            activeTabBottom.setTranslateX(offset);
        });
    }

    private void setupTabContent() {
        Set<Node> items = lookupAll(".tab_content > .content");
        AtomicInteger count = new AtomicInteger(0);
        items.forEach(item -> {
            boolean active = (count.getAndIncrement() == activeIndex);
            item.setManaged(active);
            item.setVisible(active);
        });
        Optional.ofNullable(tabChangedHandler).ifPresent(
                handler -> handler.accept(activeIndex));
    }

    public void loadTabContent() {
        setupTabHighlight();
        setupTabContent();
    }

    public void onTabChanged(Consumer<Integer> tabChangedHandler) {
        this.tabChangedHandler = tabChangedHandler;
    }

    public double getTabIndent() {
        return tabIndent;
    }

    public void setTabIndent(double tabIndent) {
        this.tabIndent = tabIndent;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int index) {
        activeIndex = index;
        loadTabContent();
    }
}
