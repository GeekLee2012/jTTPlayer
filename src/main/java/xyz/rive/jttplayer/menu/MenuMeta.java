package xyz.rive.jttplayer.menu;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class MenuMeta {
    private final static String SP = "---";
    private final static int DISABLED = -1;
    private final static int NORMAL = 0;
    private final static int ACTIVE = 1;
    private String text;
    private String iconStyle;
    private EventHandler<? super MouseEvent> action;
    private Function<MenuMeta, Integer> stateDetector;
    private List<MenuMeta> submenuList;
    private Function<MenuMeta, List<MenuMeta>> dynamicSubmenuList;
    private double width;
    private double height;

    public MenuMeta(String text) {
        this(text, null, null, null);
    }

    public MenuMeta(String text, String iconStyle) {
        this(text, iconStyle, null, null);
    }

    public MenuMeta(String text, String iconStyle, Function<MenuMeta, Integer> stateDetector) {
        this(text, iconStyle, null, stateDetector);
    }

    public MenuMeta(String text, EventHandler<? super MouseEvent> action) {
        this(text, null, action, null);
    }

    public MenuMeta(String text, EventHandler<? super MouseEvent> action, Function<MenuMeta, Integer> stateDetector) {
        this(text, null, action, stateDetector);
    }

    public MenuMeta(String text,
                    EventHandler<? super MouseEvent> action,
                    Function<MenuMeta, Integer> stateDetector,
                    double width,
                    double height) {
        this(text, null, action, stateDetector, width, height);
    }

    public MenuMeta(String text, String iconStyle,
                    EventHandler<? super MouseEvent> action) {
        this(text, iconStyle, action, null, -1);
    }

    public MenuMeta(String text, EventHandler<? super MouseEvent> action, double width) {
        this(text, null, action, null, width);
    }

    public MenuMeta(String text, String iconStyle,
                    EventHandler<? super MouseEvent> action,
                    double width) {
        this(text, iconStyle, action, null, width);
    }

    public MenuMeta(String text, String iconStyle,
                    EventHandler<? super MouseEvent> action,
                    Function<MenuMeta, Integer> stateDetector) {
        this(text, iconStyle, action, stateDetector, -1);
    }

    public MenuMeta(String text,
                    String iconStyle,
                    EventHandler<? super MouseEvent> action,
                    Function<MenuMeta, Integer> stateDetector,
                    double width) {
        this(text, iconStyle, action, stateDetector, width, -1);
    }

    public MenuMeta(String text,
                    String iconStyle,
                    EventHandler<? super MouseEvent> action,
                    Function<MenuMeta, Integer> stateDetector,
                    double width,
                    double height) {
        this.text = text;
        this.iconStyle = iconStyle;
        this.action = action;
        this.stateDetector = stateDetector;
        this.width = width;
        this.height = height;
    }

    public MenuMeta(String text, List<MenuMeta> submenuList) {
        this(text, null, submenuList, -1);
    }

    public MenuMeta(String text, List<MenuMeta> submenuList, double width) {
        this(text, null, submenuList, width);
    }

    public MenuMeta(String text, String iconStyle, List<MenuMeta> submenuList) {
        this(text, iconStyle, submenuList, -1);
    }

    public MenuMeta(String text, String iconStyle, List<MenuMeta> submenuList, double width) {
        this.text = text;
        this.iconStyle = iconStyle;
        this.submenuList = submenuList;
        this.width = width;
    }

    public MenuMeta(String text, String iconStyle, Function<MenuMeta, List<MenuMeta>> dynamicSubmenuList, double width) {
        this.text = text;
        this.iconStyle = iconStyle;
        this.dynamicSubmenuList = dynamicSubmenuList;
        this.width = width;
    }

    public static boolean isSeparator(MenuMeta meta) {
        return meta != null && SP.contentEquals(meta.getText());
    }

    public Function<MenuMeta, Integer> getStateDetector() {
        return stateDetector;
    }

    public void setStateDetector(Function<MenuMeta, Integer> stateDetector) {
        this.stateDetector = stateDetector;
    }

    public String getIconStyle() {
        return iconStyle;
    }

    public void setIconStyle(String iconStyle) {
        this.iconStyle = iconStyle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public EventHandler<? super MouseEvent> getAction() {
        return action;
    }

    public void setAction(EventHandler<? super MouseEvent> action) {
        this.action = action;
    }

    public List<MenuMeta> getSubmenuList() {
        return submenuList;
    }

    public void setSubmenuList(List<MenuMeta> submenuList) {
        this.submenuList = submenuList;
    }

    public boolean hasSubmenuList() {
        return !(submenuList == null || submenuList.isEmpty());
    }

    public Function<MenuMeta, List<MenuMeta>> getDynamicSubmenuList() {
        return dynamicSubmenuList;
    }

    public void setDynamicSubmenuList(Function<MenuMeta, List<MenuMeta>> dynamicSubmenuList) {
        this.dynamicSubmenuList = dynamicSubmenuList;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public static MenuMeta separator() {
        return new MenuMeta(SP);
    }

    public static int toActiveState(boolean active) {
        return active ? ACTIVE : NORMAL;
    }

    public static int toDisabledState(boolean disabled) {
        return disabled ? DISABLED : NORMAL;
    }

    public static int toState(boolean disabled, boolean active) {
        return disabled ? DISABLED : (active ? ACTIVE : NORMAL);
    }

    public static boolean isActive(int state) {
        return ACTIVE == state;
    }

    public static boolean isDisabled(int state) {
        return DISABLED == state;
    }

}
