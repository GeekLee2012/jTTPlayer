package xyz.rive.jttplayer.common;

public enum PlayState {
    UNKNOWN(-1, "未知"),
    INIT(0, "初始"),
    LOADING(1, "加载"),
    LOAD_ERROR(2, "异常"),
    LOADED(3, "准备"),
    PLAYING(4, "播放"),
    PAUSED(5, "暂停"),
    PLAY_ERROR(6, "异常"),
    STOPPING(7, "停止"),
    STOPPED(8, "停止");

    private final int value;
    private final String name;

    PlayState(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static PlayState of(int value) {
        switch (value) {
            case 0: 
                return INIT;
            case 1:
                return LOADING;
            case 2:
                return LOAD_ERROR;
            case 3:
                return LOADED;
            case 4:
                return PLAYING;
            case 5:
                return PAUSED;
            case 6:
                return PLAY_ERROR;
            case 7:
                return STOPPING;
            case 8:
                return STOPPED;
        };
        return UNKNOWN;
    }
}
