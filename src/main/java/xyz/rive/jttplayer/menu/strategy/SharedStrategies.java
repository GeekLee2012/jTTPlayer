package xyz.rive.jttplayer.menu.strategy;

public final class SharedStrategies {
    private static DefaultShowStrategy sharedDefault;
    private static ShowItemRightStrategy sharedShowItemRight;
    private static ShowUnderItemStrategy sharedShowUnderItem;

    public static DefaultShowStrategy getSharedDefault() {
        if(sharedDefault == null) {
            sharedDefault = new DefaultShowStrategy();
        }
        return sharedDefault;
    }

    public static ShowItemRightStrategy getSharedRight() {
        if(sharedShowItemRight == null) {
            sharedShowItemRight = new ShowItemRightStrategy();
        }
        return sharedShowItemRight;
    }

    public static ShowUnderItemStrategy getSharedUnder() {
        if(sharedShowUnderItem == null) {
            sharedShowUnderItem = new ShowUnderItemStrategy();
        }
        return sharedShowUnderItem;
    }

}
