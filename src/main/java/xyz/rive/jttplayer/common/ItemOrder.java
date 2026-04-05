package xyz.rive.jttplayer.common;

public class ItemOrder {
    private String path;
    private boolean deep;
    private int order;

    public ItemOrder() {

    }

    public ItemOrder(String path, boolean deep, int order) {
        this.path = path;
        this.deep = deep;
        this.order = order;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDeep() {
        return deep;
    }

    public void setDeep(boolean deep) {
        this.deep = deep;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
