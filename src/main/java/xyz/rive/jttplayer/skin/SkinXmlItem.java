package xyz.rive.jttplayer.skin;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trimLowerCase;

public class SkinXmlItem extends PositionBasedItem {
    public int flashMode;
    public int frameCount;
    public int frameInterval;
    public String color;
    public String font;
    public int fontSize;
    public String align;
    public String hotImage;
    public String barImage;
    public String thumbImage;
    public String buttonsImage;
    public String fillImage;
    public String fillImage2;
    public String flashImage;
    public boolean vertical;
    public String icon;
    public String bkgnd;
    public String transparentColor;
    public String selectedImage;

    public SkinXmlItem(String name) {
        this.name = name;
    }

    public String icon() {
        return isEmpty(icon) ? image : icon;
    }

    private boolean isAlign(String value) {
        return trimLowerCase(align).contentEquals(value);
    }

    public boolean isAlignRight() {
        return isAlign("right");
    }

    public boolean isAlignTopRight() {
        return isAlign("top+right")
                || isAlign("right+top");
    }

    public boolean isAlignBottomLeft() {
        return isAlign("bottom+left")
                || isAlign("left+bottom");
    }

    public boolean isAlignBottomRight() {
        return isAlign("bottom+right")
                || isAlign("right+bottom");
    }

    public boolean isAlignBottomCenter() {
        return isAlign("bottom+center")
                || isAlign("center+bottom");
    }

    public boolean isAlignTopCenter() {
        return isAlign("top+center")
                || isAlign("center+top");
    }

    public boolean isAlignCenter() {
        return isAlign("center");
    }

}
