package xyz.rive.jttplayer.skin;

public final class Constants {
    private Constants() { }

    public final static String DEFAULT_SKIN_SNAME = "DEFAULT";
    //public final static String DEFAULT_SKIN_SNAME = "PurpleMyth";
    public final static String DEFAULT_SKIN_NAME = DEFAULT_SKIN_SNAME + ".skn";

    //Skin.xml
    public final static String ENTRY_SKIN_XML = "Skin.xml";

    public static final String PLAYER_WINDOW = "player_window";
    public static final String LYRIC_WINDOW = "lyric_window";
    public static final String EQUALIZER_WINDOW = "equalizer_window";
    public static final String PLAYLIST_WINDOW = "playlist_window";
    public static final String MINI_WINDOW = "mini_window";
    public static final String DESKLRC_BAR = "desklrc_bar";

    public static final String ATTR_VERSION = "version";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_AUTHOR = "author";
    public static final String ATTR_URL = "url";
    public static final String ATTR_EMAIL = "email";
    public static final String ATTR_TRANSPARENT_COLOR = "transparent_color";

    public static final String ATTR_IMAGE = "image";
    public static final String ATTR_POSITION = "position";
    public static final String ATTR_RESIZE_RECT = "resize_rect";
    public static final String ATTR_EQ_INTERVAL = "eq_interval";

    public static final String ATTR_ICON = "icon";
    public static final String ATTR_COLOR = "color";
    public static final String ATTR_FONT = "font";
    public static final String ATTR_FONT_SIZE = "font_size";
    public static final String ATTR_ALIGN = "align";
    public static final String ATTR_HOT_IMAGE = "hot_image";
    public static final String ATTR_BAR_IMAGE = "bar_image";
    public static final String ATTR_THUMB_IMAGE = "thumb_image";
    public static final String ATTR_FILL_IMAGE = "fill_image";
    public static final String ATTR_FILL_IMAGE2 = "fill_image2";
    public static final String ATTR_FLASH_IMAGE = "flash_image";
    public static final String ATTR_VERTICAL = "vertical";
    public static final String ATTR_BUTTONS_IMAGE = "buttons_image";
    public static final String ATTR_FLASH_MODE = "flash_mode";
    public static final String ATTR_FRAME_COUNT = "frame_count";
    public static final String ATTR_FRAME_INTERVAL = "frame_interval";

    //Lyric.xml
    //Playlist.xml
    public final static String ENTRY_LYRIC_XML = "Lyric.xml";
    public final static String ENTRY_PLAYLIST_XML = "Playlist.xml";
    public final static String ENTRY_VISUAL_XML = "Visual.xml";

    public final static String ELEMENT_STA_LYRIC_ROOT = "ttplayer_lyric";
    public final static String ELEMENT_STA_PLAYLIST_ROOT = "ttplayer_playlist";
    public final static String ELEMENT_STA_VISUAL_ROOT = "ttplayer_visual";
    public final static String ELEMENT_STA_LYRIC_SUB_ROOT = "Lyric";
    public final static String ELEMENT_STA_PLAYLIST_SUB_ROOT = "PlayList";
    public final static String ELEMENT_STA_VISUAL_SUB_ROOT = "Visual";
    public static final String ATTR_STA_FONT = "Font";
    public static final String ATTR_STA_TEXT_COLOR = "TextColor";
    public static final String ATTR_STA_HILIGHT_COLOR = "HilightColor";
    public static final String ATTR_STA_BKGND_COLOR = "BkgndColor";
    public static final String ATTR_STA_BKGND_COLOR2 = "BkgndColor2";
    public static final String ATTR_STA_COLOR_TEXT = "Color_Text";
    public static final String ATTR_STA_COLOR_HILIGHT = "Color_Hilight";
    public static final String ATTR_STA_COLOR_BKGND = "Color_Bkgnd";
    public static final String ATTR_STA_COLOR_BKGND2 = "Color_Bkgnd2";
    public static final String ATTR_STA_COLOR_DURATION = "Color_Duration";
    public static final String ATTR_STA_COLOR_NUMBER = "Color_Number";
    public static final String ATTR_STA_COLOR_SELECT = "Color_Select";
    public static final String ATTR_STA_COLOR_SELECT_TEXT = "Color_SelText";
    //Visual
    public static final String ATTR_STA_SPECTRUM_TOP_COLOR = "SpectrumTopColor";
    public static final String ATTR_STA_SPECTRUM_BTM_COLOR = "SpectrumBtmColor";
    public static final String ATTR_STA_SPECTRUM_MID_COLOR = "SpectrumMidColor";
    public static final String ATTR_STA_SPECTRUM_PEAK_COLOR = "SpectrumPeakColor";
    public static final String ATTR_STA_SPECTRUM_WIDE = "SpectrumWide";
    public static final String ATTR_STA_BLUR_SPEED = "BlurSpeed";
    public static final String ATTR_STA_BLUR = "Blur";
    public static final String ATTR_STA_BLUR_SCOPE_COLOR = "BlurScopeColor";

    public static class Item {
        //Player Window
        public static final String PLAY = "play";
        public static final String PAUSE = "pause";
        public static final String PREV = "prev";
        public static final String NEXT = "next";
        public static final String STOP = "stop";
        public static final String OPEN = "open";
        public static final String MODE_SINGLE = "mode_single";
        public static final String MODE_LOOP = "mode_loop";
        public static final String MODE_SLIDER = "mode_slider";
        public static final String MODE_CIRCLE = "mode_circle";
        public static final String MODE_RANDOM = "mode_random";
        public static final String SET = "set";
        public static final String MUTE = "mute";
        public static final String LYRIC = "lyric";
        public static final String EQUALIZER = "equalizer";
        public static final String EQ = "eq";
        public static final String PLAYLIST = "playlist";
        public static final String BROWSER = "browser";
        public static final String MINIMIZE = "minimize";
        public static final String MINI = "mini";
        public static final String MINIMODE = "minimode";
        public static final String EXIT = "exit";
        public static final String PROGRESS = "progress";
        public static final String VOLUME = "volume";
        public static final String ICON = "icon";
        public static final String INFO = "info";
        public static final String LED = "led";
        public static final String VISUAL = "visual";
        public static final String STEREO = "stereo";
        public static final String STATUS = "status";

        //Lyric Window
        public static final String CLOSE = "close";
        public static final String ONTOP = "ontop";
        public static final String DESKLRC = "desklrc";

        //Equalizer Window
        public static final String ENABLED = "enabled";
        public static final String RESET = "reset";
        public static final String PROFILE = "profile";
        public static final String BALANCE = "balance";
        public static final String SURROUND = "surround";
        public static final String PREAMP = "preamp";
        public static final String EQFACTOR = "eqfactor";

        //Playlist Window
        public static final String TOOLBAR = "toolbar";
        public static final String SCROLLBAR = "scrollbar";
        public static final String STATIC_TIP = "static_tip";

        //Mini Window

        //Desklrc Bar
        public static final String DESKLRC_BAR = "desklrc_bar";
        public static final String LIST = "list";
        public static final String SETTINGS = "settings";
        public static final String KALAOK = "kalaok";
        public static final String LINES = "lines";
        public static final String LOCK = "lock";
        public static final String RETURN = "return";
        public static final String TITLE = "title";
        public static final String MINI_BORDER = "mini_border";

    }


}
