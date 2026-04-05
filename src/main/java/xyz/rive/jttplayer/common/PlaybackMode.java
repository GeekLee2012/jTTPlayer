package xyz.rive.jttplayer.common;

public enum PlaybackMode {
    OnlyOne(0, "单曲播放"),
    RepeatOne(1, "单曲循环"),
    Sequence(2, "顺序播放"),
    RepeatAll(3, "循环播放"),
    Random(4, "随机播放");

    private final int value;
    private final String name;

    PlaybackMode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static PlaybackMode of(int value) {
        switch (value) {
            case 0:
                return OnlyOne;
            case 1:
                return RepeatOne;
            case 2:
                return Sequence;
            case 3:
                return RepeatAll;
            case 4:
                return Random;
        };
        return Sequence;
    }
}
