package xyz.rive.jttplayer.common;

public class Rect {
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    public Rect(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double x1() {
        return x1;
    }

    public double y1() {
        return y1;
    }

    public double x2() {
        return x2;
    }

    public double y2() {
        return y2;
    }

    @Override
    public String toString() {
        return "Rect{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }

    public String toString2() {
        return String.format("%s %s %s %s", x1, y1, x2, y2);
    }
}
