package xyz.rive.jttplayer.menu;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.manager.MenuManager;
import xyz.rive.jttplayer.manager.PlayerManager;
import xyz.rive.jttplayer.manager.StageManager;
import xyz.rive.jttplayer.menu.action.AbstractMenuAction;
import xyz.rive.jttplayer.menu.strategy.SharedStrategies;
import xyz.rive.jttplayer.menu.strategy.ShowStrategy;
import xyz.rive.jttplayer.menu.strategy.SubmenuShowStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class PopMenu extends Stage {
    protected final ApplicationContext context = ApplicationContext.getInstance();
    private double prefMenuWidth = -1;
    private double prefMenuHeight = -1;
    private double computedMenuHeight = 0;
    private double initialWidth = -1;
    private ShowStrategy showStrategy;
    private Object source;
    private boolean slogan = false;
    private VBox sloganBox;
    private InputEvent event;
    private BiConsumer<MouseEvent, MenuMeta> itemClickedHandler;
    private BiConsumer<MouseEvent, MenuMeta> itemEnteredHandler;
    private BiConsumer<MouseEvent, MenuMeta> itemExitedHandler;
    private List<MenuMeta> menuList;
    private PopMenu childPopMenu;
    private PopMenu parentPopMenu;
    private Region lastHoveredItem;


    public PopMenu() {
        this(null);
    }

    public PopMenu(Window owner) {
        this(owner, false);
    }

    public PopMenu(Window owner, boolean slogan) {
        this(owner, slogan ? 171 : 145);
        setupSlogan(slogan);
    }

    public PopMenu(Window owner, double width) {
        Scene scene = new Scene(loadResource("pop-menu.fxml"));
        scene.setFill(null);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        initOwner(owner);
        //setAlwaysOnTop(true);
        setWidth(width);
        setMinWidth(width);
        setMaxWidth(width);
        initialWidth = width;

        setOnShowing(event -> {
            updateDynamicMenuContents();
            updateMenuContentActiveStates();
            setupPosition();
        });

        setOnHidden(event -> {
            Optional.ofNullable(childPopMenu).ifPresent(
                    __ -> childPopMenu.hide());
        });
    }

    public boolean isSloganEnable() {
        return slogan;
    }

    public StageManager getStageManager() {
        return context.getStageManager();
    }

    public MenuManager getMenuManager() {
        return context.getMenuManager();
    }

    public PlayerManager getPlayerManager() {
        return context.getPlayerManager();
    }

    public MenuTemplates getMenuTemplates() {
        return getMenuManager().getMenuTemplates();
    }

    public PopMenu setEvent(InputEvent event) {
        this.event = event;
        source = event.getSource(); //同源检测
        if(isShowing()) {
            setupPosition();
        }
        event.consume();
        return this;
    }

    public PopMenu setMenuList(List<MenuMeta> menuList) {
        this.menuList = menuList;
        buildMenuContent();
        return this;
    }

    public PopMenu setShowStrategy(ShowStrategy showStrategy) {
        this.showStrategy = showStrategy;
        return this;
    }

    public ShowStrategy getShowStrategy() {
        return showStrategy;
    }

    public void setupPosition() {
        if(showStrategy == null) {
            showStrategy = SharedStrategies.getSharedDefault();
        }
        if(event == null) {
            return ;
        }
        setupPosition(showStrategy.getPosition(event, this));
    }

    public void setupPosition(Position position) {
        if(position == null) {
            return ;
        }
        setX(position.x());
        setY(position.y());
    }

    public void onItemClicked(BiConsumer<MouseEvent, MenuMeta> handler) {
        this.itemClickedHandler = handler;
    }

    public void onItemEntered(BiConsumer<MouseEvent, MenuMeta> handler) {
        this.itemEnteredHandler = handler;
    }

    public void onItemExited(BiConsumer<MouseEvent, MenuMeta> handler) {
        this.itemExitedHandler = handler;
    }

    private VBox getMenuBox() {
        return (VBox) getScene().lookup(".menu_box");
    }

    private void buildMenuContent() {
        VBox menusBox = getMenuBox();
        menusBox.getChildren().clear();
        computedMenuHeight = 0;
        prefMenuWidth = -1;
        prefMenuHeight = -1;

        Optional.ofNullable(menuList).ifPresent(menuMetas -> {
            menuMetas.forEach(meta -> {
                //确定Size: Custom  > Initial/Default
                prefMenuWidth = Math.max(meta.getWidth(), prefMenuWidth);
                prefMenuHeight = Math.max(meta.getHeight(), prefMenuHeight);

                HBox item = new HBox();
                setupItem(item, meta);
                setupItemHovered(item, meta);
                menusBox.getChildren().add(item);
            });
        });

        doSetMenuSize((menusBox.getChildren().size() - 1), slogan ? 4 : 6);
    }

    private void setupItem(HBox item, MenuMeta meta) {
        //item.getStyleClass().add("menu_item");
        //item.setUserData(meta);
        item.setFillHeight(false);
        item.setDisable(false);

        Region icon = new Region();
        icon.getStyleClass().add("icon");

        VBox iconBox = new VBox(icon);
        iconBox.getStyleClass().add("icon_box");

        ImageView iconShadow = new ImageView();
        //iconShadow.setPrefSize(16, 16);
        iconShadow.getStyleClass().add("icon_shadow");
        iconShadow.setVisible(false);

        Label label = new Label();
        Region flag = new Region();
        flag.getStyleClass().add("submenu_flag");

        AnchorPane container = new AnchorPane(iconShadow, iconBox, label, flag);
        container.getStyleClass().add("container");
        AnchorPane.setLeftAnchor(iconShadow, 3.5D);
        AnchorPane.setTopAnchor(iconShadow, 3.5D);

        AnchorPane.setLeftAnchor(iconBox, 0D);
        AnchorPane.setTopAnchor(iconBox, 0D);

        AnchorPane.setBottomAnchor(label, 0D);
        AnchorPane.setLeftAnchor(label, 30D);
        AnchorPane.setRightAnchor(label, 12D);
        AnchorPane.setTopAnchor(label, 0D);
        AnchorPane.setBottomAnchor(label, 0D);

        AnchorPane.setRightAnchor(flag, 5D);
        AnchorPane.setTopAnchor(flag, 3D);

        item.getChildren().add(container);
        HBox.setHgrow(container, Priority.ALWAYS);

        if(MenuMeta.isSeparator(meta)) {
            AnchorPane.setLeftAnchor(label, 28D);
            AnchorPane.setRightAnchor(label, 3D);
            item.getStyleClass().setAll("menuitem_sp");
            computedMenuHeight += 5;
            return ;
        }

        computedMenuHeight += 24;

        label.setText(meta.getText());
        String iconStyle = meta.getIconStyle();
        if(!isEmpty(iconStyle)) {
            icon.setStyle(iconStyle);
        } else {
            item.getStyleClass().add("selected");
        }

        Optional.ofNullable(meta.getStateDetector()).ifPresent(stateDetector -> {
            if(MenuMeta.isActive(stateDetector.apply(meta))) {
                item.getStyleClass().add("active");
            }
            if(MenuMeta.isDisabled(stateDetector.apply(meta))) {
                item.getStyleClass().add("disabled");
                item.setDisable(true);
            }
        });

        item.setOnMouseClicked(event -> {
            if(meta.getSubmenuList() == null
                    && meta.getDynamicSubmenuList() == null) {
                hide();
            }
            Optional.ofNullable(meta.getAction()) .ifPresent(action -> {
                action.handle(event);
                if(action instanceof AbstractMenuAction) {
                    ((AbstractMenuAction)action).postHandle(item);
                }
            });
            Optional.ofNullable(parentPopMenu).ifPresent(__ -> {
                parentPopMenu.updateMenuContentActiveStates();
            });
            Optional.ofNullable(itemClickedHandler).ifPresent(__ -> {
                itemClickedHandler.accept(event, meta);
            });
        });
    }

    private void setupItemHovered(Region item, MenuMeta meta) {
        //静态子菜单
        List<MenuMeta> sumenuList = meta.getSubmenuList();
        /*
        if(!meta.hasSubmenuList() ) {
            //动态子菜单，运行时通过函数调用获取
            Function<MenuMeta, List<MenuMeta>> getSubmenuList = meta.getDynamicSubmenuList();
            if(getSubmenuList != null) {
                sumenuList = getSubmenuList.apply(meta);
            }
        }*/

        //动态子菜单，由onShowing()方法单独处理，此处略过
        if(!meta.hasSubmenuList() && meta.getDynamicSubmenuList() != null) {
            return ;
        }

        List<MenuMeta> list = sumenuList;
        if (list != null) {
            item.getStyleClass().add("more_menu");
        }
        item.setOnMouseEntered(event -> {
            setupItemIconEffect(item, meta);
            if (list != null) {
                getChildPopMenu().setEvent(event)
                        .setMenuList(list)
                        .show();
            }
            Optional.ofNullable(itemEnteredHandler).ifPresent(__ -> {
                itemEnteredHandler.accept(event, meta);
            });
        });

        item.setOnMouseExited(event -> {
            resetItemIconEffect(item);
            if (list != null) {
                if(isMouseHover(getChildPopMenu(),
                        event.getScreenX(),
                        event.getScreenY())) {
                    return ;
                }
                getChildPopMenu().hide();
            }
            Optional.ofNullable(itemExitedHandler).ifPresent(__ -> {
                itemExitedHandler.accept(event, meta);
            });
        });
    }

    private void setupItemIconEffect(Region item, MenuMeta meta) {
        resetItemIconEffect(lastHoveredItem);
        if(item == null || item.getStyleClass().contains("active")) {
            return ;
        }

        Region icon = (Region) item.lookup(".icon");
        ImageView iconShadow = (ImageView) item.lookup(".icon_shadow");

        //开启Effect
        if(icon == null || isEmpty(meta.getIconStyle())
                || iconShadow == null) {
            return ;
        }

        if(iconShadow.getEffect() == null) {
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setSaturation(-1.0); //去色（变灰度）
            grayscale.setBrightness(-0.3); //变暗
            grayscale.setContrast(0);
            grayscale.setHue(0);

            iconShadow.setEffect(grayscale);

            WritableImage image = new WritableImage(
                    (int) icon.getWidth(),
                    (int) icon.getHeight());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            icon.snapshot(params, image);
            iconShadow.setImage(image);
            iconShadow.setScaleX(1.01);
            iconShadow.setScaleY(1.01);
        }

        icon.setTranslateX(-1);
        icon.setTranslateY(-1);
        iconShadow.setVisible(true);

        lastHoveredItem = item;
    }

    private void resetItemIconEffect(Region item) {
        if(item == null) {
            return ;
        }

        Region icon = (Region) item.lookup(".icon");
        ImageView iconShadow = (ImageView) item.lookup(".icon_shadow");

        if(icon != null) {
            icon.setTranslateX(0);
            icon.setTranslateY(0);
        }

        if(iconShadow != null) {
            iconShadow.setVisible(false);
        }
    }

    private void doSetMenuSize(double vSpacings, double vPaddings) {
        computedMenuHeight += vSpacings + vPaddings;

        setWidth(initialWidth);
        setMinWidth(initialWidth);
        setMaxWidth(initialWidth);

        setHeight(computedMenuHeight);
        setMinHeight(computedMenuHeight);
        setMaxHeight(computedMenuHeight);

        if(prefMenuWidth > 0) {
            setWidth(prefMenuWidth);
            setMinWidth(prefMenuWidth);
            setMaxWidth(prefMenuWidth);
        }

        if(prefMenuHeight > 0) {
            setHeight(prefMenuHeight);
            setMinHeight(prefMenuHeight);
            setMaxHeight(prefMenuHeight);
        }
    }

    private void updateDynamicMenuContents() {
        VBox menusBox = getMenuBox();
        AtomicInteger count = new AtomicInteger(0);
        menusBox.getChildren().forEach(item -> {
            int index = count.getAndIncrement();
            MenuMeta meta = menuList.get(index);
            Function<MenuMeta, List<MenuMeta>> getSubmenuList = meta.getDynamicSubmenuList();
            if(getSubmenuList == null) {
                return ;
            }
            Optional.ofNullable(getSubmenuList.apply(meta)).ifPresent(list -> {
                item.getStyleClass().add("more_menu");
                item.setOnMouseEntered(event -> {
                    setupItemIconEffect((Region) item, meta);
                    getChildPopMenu().setEvent(event)
                            .setMenuList(list)
                            .show();
                    Optional.ofNullable(itemEnteredHandler).ifPresent(__ -> {
                        itemEnteredHandler.accept(event, meta);
                    });
                });

                item.setOnMouseExited(event -> {
                    resetItemIconEffect((Region) item);
                    if(isMouseHover(getChildPopMenu(),
                            event.getScreenX(),
                            event.getScreenY())) {
                        return ;
                    }
                    getChildPopMenu().hide();
                    if(parentPopMenu == null) {
                        resetItemIconEffect(lastHoveredItem);
                    }
                    Optional.ofNullable(itemExitedHandler).ifPresent(__ -> {
                        itemExitedHandler.accept(event, meta);
                    });
                });
            });
        });
    }

    private void updateMenuContentActiveStates() {
        VBox menusBox = getMenuBox();
        AtomicInteger count = new AtomicInteger(0);
        menusBox.getChildren().forEach(item -> {
            int index = count.getAndIncrement();
            MenuMeta meta = menuList.get(index);
            item.getStyleClass().removeAll("active");
            item.getStyleClass().removeAll("disabled");
            item.setDisable(false);
            Optional.ofNullable(meta.getStateDetector()).ifPresent(stateDetector -> {
                if(MenuMeta.isActive(stateDetector.apply(meta))) {
                    item.getStyleClass().add("active");
                }
                if(MenuMeta.isDisabled(stateDetector.apply(meta))) {
                    item.getStyleClass().add("disabled");
                    item.setDisable(true);
                }
            });
        });
    }

    private double computeHeight() {
        double computedHeight = 0;
        if(menuList != null) {
            for(MenuMeta meta : menuList) {
                computedHeight += MenuMeta.isSeparator(meta) ? 5 : 24;
            }
            computedHeight += (menuList.size() - 1) + 5;
        }
        return computedHeight;
    }

    private void setupSlogan(boolean slogan) {
        this.slogan = slogan;

        Node root = getScene().lookup(".pop_menu");
        root.getStyleClass().removeAll("with_slogan");
        if(slogan) {
            root.getStyleClass().add("with_slogan");
        }

        sloganBox = (VBox) getScene().lookup(".slogan_box");
        sloganBox.setManaged(slogan);
        sloganBox.setVisible(slogan);
    }

    public void toggle() {
        if(isShowing()) {
            //同源
            if(source == event.getSource()) {
                //点击事件
                if(event instanceof MouseEvent) {
                    MouseEvent mouseEvent = (MouseEvent)event;
                    if(mouseEvent.getClickCount() > 0) {
                        hide();
                        return;
                    }
                }
            }
        }
        show();
    }

    public void show(InputEvent event) {
        setEvent(event).show();
    }

    public void toggle(InputEvent event) {
        setEvent(event).toggle();
    }

    private void setParentPopMenu(PopMenu popMenu) {
        parentPopMenu = popMenu;
    }

    public PopMenu getParentPopMenu() {
        return parentPopMenu;
    }

    public PopMenu getChildPopMenu() {
        if(childPopMenu == null) {
            childPopMenu = new PopMenu(super.getOwner());
            childPopMenu.setParentPopMenu(this);
            childPopMenu.setAlwaysOnTop(super.isAlwaysOnTop());
            childPopMenu.setShowStrategy(new SubmenuShowStrategy());

            Optional.ofNullable(childPopMenu.getScene()).ifPresent(scene -> {
                scene.setOnMouseEntered(event -> {
                    childPopMenu.show();
                });
                scene.setOnMouseExited(event -> {
                    if(childPopMenu.getChildPopMenu().isShowing()) {
                        return ;
                    }
                    childPopMenu.hide();
                    if(parentPopMenu == null) {
                        resetItemIconEffect(lastHoveredItem);
                    }
                });
            });
        }
        return childPopMenu;
    }

    public void refresh() {
        buildMenuContent();
    }


}
