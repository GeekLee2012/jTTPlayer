package xyz.rive.jttplayer.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import xyz.rive.jttplayer.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

import static xyz.rive.jttplayer.util.FileUtils.readText;
import static xyz.rive.jttplayer.util.StringUtils.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Lyric {
    private final static String TAG_BEGIN = "[";
    private final static String TAG_END = "]";

    private final static Map<String, String> META_TAGS = new HashMap<>();
    static  {
        META_TAGS.put("ti", "title");
        META_TAGS.put("ar", "artist");
        META_TAGS.put("al", "album");
        //META_TAGS.put("by", "by");
        //META_TAGS.put("offset", "offset");
    }

    private final static String TIME_REGEX = "\\d{2}:\\d{2}(:\\d{2})?(\\.\\d{2,3})?";
    private final static String TIME_LINE_REGEX = "^\\[\\d{2}:\\d{2}(:\\d{2})?(\\.\\d{2,3})?].*";

    public final static int LINE_MODE = 0;
    public final static int WORD_MODE = 1;

    private String title;
    private String artist;
    private String album;
    private String by;
    private String offset;
    private String tool;
    private int mode = LINE_MODE;
    private final TreeMap<String, String> data = new TreeMap<>(
                    (k1, k2) ->
                            Math.toIntExact(StringUtils.toMillis(k1) - StringUtils.toMillis(k2)));

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getBy() {
        return by;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public int getMode() {
        return mode;
    }

    private void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isWordMode() {
        return mode == WORD_MODE;
    }

    @JsonIgnore
    public long getOffsetAsNumber() {
       try {
           return Long.parseLong(offset);
       } catch (Exception e) {
           return 0;
       }
    }

    public TreeMap<String, String> getData() {
        return data;
    }

    //MM:ss.SSS
    private static void addLine(Lyric lyric, String mmssSSS, String text) {
        if(lyric == null) {
            return;
        }
        if (!isMatchTime(mmssSSS)) {
            return ;
        }
        String key = unifyTime(mmssSSS);
        String value = lyric.data.get(key);
        if(!isEmpty(value)) {
            text = value + "\n" + text;
        }
        lyric.data.put(key, text);
    }

    public boolean hasData() {
        return !data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    public static Lyric parseFromText(String text) {
        return parseFromText(text, null);
    }
    
    public static Lyric parseFromText(String text, String separator) {
        if (isEmpty(text)) {
            return new Lyric();
        }
        return parseFromLines(toLines(text, separator));
    }

    public static Lyric parseFromLines(List<String> lines) {
        if(lines == null) {
            return new Lyric();
        }
        return parseFromLines(lines.toArray(new String[0]));
    }

    public static Lyric parseFromLines(String[] lines) {
        Lyric lyric = new Lyric();
        Optional.ofNullable(lines).ifPresent(__ -> {
            for(String line : lines) {
                line = trim(line);
                if (line.isEmpty()) {
                    continue;
                }
                //暂时忽略处理
                //或可考虑归并到上一歌词行
                if (!line.startsWith(TAG_BEGIN) || !line.contains(TAG_END)) {
                    continue;
                }
                if (isTimeDataLine(line)) {
                    parseTimeData(lyric, line);
                } else {
                    parseMetaData(lyric, line);
                }
            }
        });
        return lyric;
    }

    private static boolean isTimeDataLine(String text) {
        return Pattern.matches(TIME_LINE_REGEX, text);
    }

    public static void parseTimeData(Lyric lyric, String text) {
        String[] tokens = text.split("[\\[\\]]");
        int len = tokens.length;
        if (len < 3) {
            return ;
        }
        if (isMatchTime(tokens[len - 1]) &&
                !isMatchTime(tokens[len - 2])) {
            lyric.setMode(WORD_MODE);
        }
        //逐字模式
        if (lyric.isWordMode()) {
            String time = trim(tokens[1]);
            String value = trim(text);
            addLine(lyric, time, value);
        } else {
            //逐行模式
            //兼容格式：[xx:xx.xx][xx:xx.xx]xxx
            String value = trim(tokens[len - 1]);
            if (value.isEmpty()) {
                return ;
            }
            for (int i = 0; i < len - 1; i++) {
                String time = trim(tokens[i]);
                if (time.isEmpty()) {
                    continue ;
                }
                addLine(lyric, time, value);
            }
        }
    }

    private static void parseMetaData(Lyric lyric, String text) {
        String[] tokens = text.split("[\\[:\\]]");
        int len = tokens.length;
        if (len < 3) {
            return;
        }
        String name = tokens[1];
        String value = tokens[2];
        if (isEmpty(name) || isEmpty(value)) {
            return ;
        }
        name = StringUtils.trimLowerCase(name);
        value = trim(value);
        if (value.isEmpty()) {
            return;
        }
        /*
        if (TITLE_TAG_NAME == name) {
            lyric.title = value
        } else if (ARTIST_TAG_NAME == name) {
            lyric.artist = value
        } else if (ALBUM_TAG_NAME == name) {
            lyric.album = value
        } else if (BY_TAG_NAME == name) {
            lyric.by = value
        } else if (OFFSET_TAG_NAME == name) {
            lyric.offset = value
        }
        */
        String propName = META_TAGS.getOrDefault(name, name);
        try {
            Field filed = Lyric.class.getDeclaredField(propName);
            filed.setAccessible(true);
            filed.set(lyric, value);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private static boolean isMatchTime(String text) {
        return Pattern.matches(TIME_REGEX, trim(text));
    }

    private static String unifyTime(String time) {
        String[] parts = time.split("\\.");
        if (parts.length >= 2) {
            String millisPart = trim(parts[1]);
            switch (millisPart.length()) {
                case 1: //格式错误，暂时当成ms处理
                    return trim(parts[0])
                            .concat(".00")
                            .concat(millisPart);
                case 2: //10ms
                    return time.concat("0");
                default: {
                    //格式错误，暂时截断，然后当成ms处理
                    if (millisPart.length() >= 3) {
                        return trim(parts[0])
                                .concat(".")
                                .concat(millisPart.substring(0, 3));
                    }
                }
            }
        }
        return time.concat(".000");
    }

    @Override
    public String toString() {
        if (data.isEmpty()) {
            return "";
        }
        String title = trim(this.title);
        String artist = trim(this.artist);
        String album = trim(this.album);
        //String by = trim(this.by);
        String by = trim("jTTPlayer");
        String offset = trim(this.offset);

        StringBuilder builder = new StringBuilder();
        builder.append("[ti: ").append(title).append("]\n")
                .append("[ar: ").append(artist).append("]\n")
                .append("[al: ").append(album).append("]\n")
                .append("[by: ").append(by).append("]\n")
                .append("[offset: ").append(offset).append("]\n")
                .append("\n");

        data.forEach((String key, String value) -> {
            //当前应用时间格式：00:00.000，一般格式：00:00.00
            //逐字
            if (isWordMode()) {
                builder.append(trim(value))
                        .append("\n");
                return ;
            }

            //逐行
            int len = key.length();
            if(len < 1) {
                return ;
            }
            String lineTime = key;
            if (len >= 9) {
                lineTime = key.substring(0, key.endsWith("0") ? 8 : 9);
            }
            String[] texts = value.split("\n");
            for (String text: texts) {
                builder.append("[").append(lineTime).append("] ")
                        .append(trim(text))
                        .append("\n");
            }
        });
        return builder.toString();
    }

    public static List<WordToken> toWordTokens(String line) {
        if (isEmpty(line)) {
            return null;
        }
        String[] parts = line.split("[\\[\\]]");
        int len = parts.length;
        if (len < 3) {
            return null;
        }
        if (!isMatchTime(parts[len - 1])) {
            return null;
        }

        List<WordToken> tokens = new ArrayList<>();
        for (int i = 1; i < len; i = i + 2) {
            if (i + 2 < len) {
                WordToken token = new WordToken();
                token.startTime = trim(parts[i]);
                //尽量不要去前后空格，比如英文需要空格分隔
                token.value = parts[i + 1];
                token.endTime = trim(parts[i + 2]);
                tokens.add(token);
            }
        }
        return tokens;
    }

    public static String mergeWordTokens(String value) {
        List<WordToken> tokens = toWordTokens(value);
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        tokens.forEach(token -> buffer.append(token.value));
        return buffer.toString();
    }

    public static class WordToken {
        public String startTime;
        public String endTime;
        public String value;

        @Override
        public String toString() {
            return String.format("[%s]%s[%s]", startTime, value, endTime);
        }

    }

}