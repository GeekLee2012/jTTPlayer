package xyz.rive.jttplayer.common;

public class FontOption {
    private String family;
    private String weight;
    private int size;

    public FontOption() {

    }

    public FontOption(String family, String weight, int size) {
        this.family = family;
        this.weight = weight;
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
