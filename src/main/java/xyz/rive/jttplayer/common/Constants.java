package xyz.rive.jttplayer.common;

import java.util.Arrays;
import java.util.List;

public final class Constants {
    private Constants() { }

    public final static String VERSION = "1.0.0";
    public final static String MAIN = "Main";
    public final static String MAIN_MINI = "MainMiniMode";
    public final static String EQUALIZER = "Equalizer";
    public final static String PLAYBACK_QUEUE = "PlaybackQueue";
    public final static String LYRIC = "Lyric";
    public final static String PREFERENCE = "Preference";

    public static final String APP_TITLE_VERSION = "千千静听 5.7 正式版";
    public static final String APP_SLOGAN = "千千静听 尽听精彩";
    public static final List<String> AUDIO_SUFFIXES =
            Arrays.asList(".aac", ".ape", ".flac", ".mp3", ".m4a", ".ogg", ".wav", ".wma");
    public static final List<String> IMAGE_SUFFIXES =
            Arrays.asList(".png", ".jpg", ".jpeg", ".bmp");

    public static final String PLAYBACK_QUEUE_SUFFIX = ".jttpl";
    public static final List<String> PLAYBACK_QUEUE_SUFFIXES =
            Arrays.asList(PLAYBACK_QUEUE_SUFFIX, ".m3u", ".m3u8");
    public static final String PLAYBACK_QUEUE_SUFFIX_DESC = "千千播放列表文件（.jttpl, .m3u, .m3u8）";
    public static final String PLAYBACK_QUEUE_SUFFIX_PATTERN = "*" + PLAYBACK_QUEUE_SUFFIX;

    public static final String ABOUT1 = "    一个集播放、音效、转换、歌词等多种功能于一身的专业音频播放软件。\n\n" +
            "    拥有自主研发的全新音频引擎，支持DirectSound、内核音频流(Kernel Streaming)和ASIO音频流输出、AddIn插件扩展技术，具有资源占用低、运行效率高、扩展能力强等优点。\n\n" +
            "    支持MP3/mp3PRO、AAC/AAC+、M4A/MP4、WMA、APE、MPC、OGG、WAVE、CD、FLAC、RM、TTA、AIFF、AU等音频格式以及多种MOD和MIDI音乐，支持CUE音轨索引文件，支持所有格式到WAVE、MP3、APE、WMA等格式的转换，通过基于COM接口的AddIn插件可以支持更多格式的播放和转换。\n\n" +
            "    支持采样频率转换(SSRC)和多种比特输出方式，支持回放增益，支持10波段均衡器、多级杜比环绕、淡入淡出音效，兼容并可同时激活多个Winamp2的音效插件。\n\n" +
            "    支持ID3v1/v2、WMA、RM、APE和Vorbis标签，支持批量修改标签和以标签重命名文件。\n\n" +
            "    支持同步歌词滚动显示和拖动定位播放，并且支持在线歌词搜索和歌词编辑功能。\n\n" +
            "    支持多播放列表和音频文件搜索，支持多种视觉效果，采用XML格式的ZIP压缩的皮肤，同时具有磁性窗口、半透明/淡入淡出窗口、窗口阴影、任务栏图标、自定义快捷键、信息滚动、菜单功能提示等功能。\n\n";

    public static final String ABOUT = "    一个集播放、音效、转换、歌词等多种功能于一身的专业音频播放软件。\n\n" +
            "    拥有自主研发的全新音频引擎，支持DirectSound、内核音频流(Kernel Streaming)和ASIO音频流输出、AddIn插件扩展技术，" +
            "具有资源占用低、运行效率高、扩展能力强等优点。\n\n" +
            "    支持MP3/mp3PRO、AAC/AAC+、M4A/MP4、WMA、APE、MPC、OGG、WAVE、CD、FLAC、RM、TTA、AIFF、AU等音频格式以及多种MOD和MIDI音乐，" +
            "支持CUE音轨索引文件，支持所有格式到WAVE、MP3、APE、WMA等格式的转换，通过基于COM接口的AddIn插件可以支持更多格式的播放和转换。\n\n" +
            "    支持采样频率转换(SSRC)和多种比特输出方式，支持回放增益，支持10波段均衡器、多级杜比环绕、淡入淡出音效，兼容并可同时激活多个Winamp2的音效插件。\n\n" +
            "    支持ID3v1/v2、WMA、RM、APE和Vorbis标签，支持批量修改标签和以标签重命名文件。\n\n" +
            "    支持同步歌词滚动显示和拖动定位播放，并且支持在线歌词搜索和歌词编辑功能。\n\n" +
            "    支持多播放列表和音频文件搜索，支持多种视觉效果，采用XML格式的ZIP压缩的皮肤，" +
            "同时具有磁性窗口、半透明/淡入淡出窗口、窗口阴影、任务栏图标、自定义快捷键、信息滚动、菜单功能提示等功能。\n\n" +
            "    真正免费且无需注册，也不存在任何功能或时间限制。";

    public static final String TAG_MEMO = "%F:文件名     %E:扩展名     %P:上级路径    %C:编码      %B:码率\n" +
            "%A:艺术家     %T:标题        %L:专辑          %G:流派      %I:音轨号\n" +
            "%Y:年代        %D:备注        %R:星级         %(字段名):其他标签字段";

    public static final List<String> FILENAME_FORMATS = Arrays.asList(
            "",
            "%(Artist) - %(Title)",
            "%(Title) - %(Artist)",
            "%(Artist) - %(TrackNumber).%(Title)",
            "%(TrackNumber).%(Artist) - %(Title)",
            "%(Artist)\\%(Title)",
            "%(Album)\\%(TrackNumber).%(Title)",
            "%(Artist)\\%(Album)\\%(Title)",
            "%(Genre)\\%(Album)\\%(Title)"
    );

    public final static String TRACK_DIR_PLACEHOLDER = "<歌曲所在文件夹>";
    public final static String LYRIC_DOWNLOAD_DIR_PLACEHOLDER = "<歌词下载文件夹>";

    public final static String GITHUB_AUTHOR = "https://github.com/GeekLee2012";
    public final static String GITHUB_REPOSITORY = GITHUB_AUTHOR + "/jTTPlayer";
    public final static String GITHUB_RELEASES = GITHUB_REPOSITORY + "/releases";
    public final static String LICENSE_AGPL_V3 = "https://www.gnu.org/licenses/agpl-3.0.en.html";
}
