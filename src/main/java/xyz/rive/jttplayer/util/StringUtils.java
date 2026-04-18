package xyz.rive.jttplayer.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static xyz.rive.jttplayer.util.FxUtils.*;

public final class StringUtils {

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static boolean isEmpty(String value) {
        value = trim(value);
        return value.isEmpty()
                || "null".equalsIgnoreCase(value);
    }

    public static String trimLowerCase(String value) {
        return trim(value).toLowerCase();
    }

    public static String trimUpperCase(String value) {
        return trim(value).toUpperCase();
    }

    public static int compareToIgnoreCase(String s1, String s2) {
        return trim(s1).compareToIgnoreCase(trim(s2));
    }

    public static boolean containsIgnoreCase(String s1, String s2) {
        return trimLowerCase(s1).contains(trimLowerCase(s2));
    }

    public static boolean contentEquals(String s1, String s2) {
        return contentEquals(s1, s2, false);
    }

    public static boolean contentEqualsIgnoreCase(String s1, String s2) {
        return contentEquals(s1, s2, true);
    }

    public static boolean contentEquals(String s1, String s2, boolean ignoreCase) {
        return ignoreCase ? trim(s1).equalsIgnoreCase(trim(s2))
                : trim(s1).equals(trim(s2));
    }

    public static boolean contains(String s1, String s2, boolean ignoreCase) {
        return ignoreCase ? trimLowerCase(s1).contains(trimLowerCase(s2))
                : trim(s1).contains(trim(s2));
    }

    public static String[] toLines(String text) {
        return toLines(text, null);
    }

    public static String[] toLines(String text, String separator) {
        if(isEmpty(text)) {
            return null;
        }
        if(separator == null) {
            separator = "\n";
        }
        return trim(text).split(separator);
    }

    public static long toMillis(String mmssSSS) {
        try {
            String[] timeParts = trim(mmssSSS).split(":");
            int minutes = parseInt(trim(timeParts[0]), -1);
            if(minutes < 0) {
                return -1;
            }
            timeParts = trim(timeParts[1]).split("\\.");
            int seconds = parseInt(trim(timeParts[0]));
            if(seconds < 0) {
                return -1;
            }
            long millis = 0;
            if (timeParts.length > 1) {
                String millisPart = trim(timeParts[1]);
                switch (millisPart.length()) {
                    case 0:
                    case 1:
                    case 3:
                        millis = parseInt(millisPart, -1);
                        break;
                    case 2:
                        millis = parseInt(millisPart, -1) * 10;
                        break;
                    default:
                        millis = parseInt(millisPart.substring(0, 3), -1);
                }
            }
            if(millis < 0) {
                return -1;
            }
            return (minutes * 60L + seconds) * 1000L + millis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean startsWithIgnoreCase(String value, String prefix) {
        if(isEmpty(value) || isEmpty(prefix)) {
            return false;
        }
        return trimLowerCase(value).startsWith(trimLowerCase(prefix));
    }

    public static String toMMss(double value) {
        if(value <= 0) {
            return "0:00";
        }
        int mm = (int)value;
        int ss = (int)((value - mm) * 60);
        return String.format("%1$s:%2$02d", mm, ss);
    }

    public static String toMMss(long seconds) {
        if(seconds <= 0) {
            return "00:00";
        }
        long mm = seconds / 60;
        long ss = seconds - mm * 60;
        return String.format("%1$02d:%2$02d", mm, ss);
    }

    public static String toMMssSSS(long millis) {
        if(millis <= 0) {
            return "00:00.000";
        }
        long mm = millis / 60000;
        double fullSecs = ((millis % 60000D) / 1000D);
        long ss = (long) fullSecs;
        double fullMillis = (fullSecs - ss) * 1000D;
        long SSS = (long) fullMillis;
        if((fullMillis - SSS) * 1000D >= 500) {
            SSS += 1;
        }
        return String.format("%1$02d:%2$02d.%3$03d", mm, ss, SSS);
    }

    public static String getPackage(Class<?> clazz) {
        return clazz.getPackage().getName();
    }

    public static String getPackageSlash(Class<?> clazz) {
        return getPackage(clazz).replaceAll("\\.", "/");
    }


    public static String getAppDataPath() {
        String dataRoot = "Library/Application Support";
        if(isWindows()) {
            dataRoot = "AppData/Roaming";
        } else if(isLinux()) {
            dataRoot = ".config";
        } else {
            dataRoot = "Documents";
        }
        return String.format("%1$s/%2$s/jTTPlayer",
                getUserHome(), dataRoot);
    }

    public static String getAppDataPath(String filename) {
        return String.format("%s/%s", getAppDataPath(), filename);
    }

    public static String utf8(String text) {
        try {
            return new String(trim(text).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trim(text);
    }

    public static String generateSpacing(int num) {
        String spacing = "";
        for (int i = 0; i < num; i++) {
            spacing += " ";
        }
        return spacing;
    }

    public static boolean pinyinMatch(String source, String target) {
        if(isEmpty(source) || isEmpty(target)) {
            return false;
        }
        target = trimLowerCase(target);
        if(toPinyin(source).startsWith(target)) {
            return true;
        }
        List<String> pyList = toPinyinList(source);
        int hit = 0, miss = 0;
        for (int i = 0, j = 0; i < pyList.size(); i++) {
            String py = pyList.get(i);
            if(isEmpty(py)) {
                continue;
            }
            char[] chs = py.toCharArray();
            for (int k = 0; k < chs.length && j < target.length(); k++) {
                if(target.charAt(j) == chs[k]) {
                    hit++;
                    j++;
                    miss = 0;
                } else if (miss > 0){
                    return false;
                } else {
                    miss++;
                    break ;
                }
            }
        }
        return hit == target.length();
    }

    public static String toPinyin(char ch) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        try {
            String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(ch, format);
            return pinyinArr == null || pinyinArr.length < 1 ? "" : pinyinArr[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<String> toPinyinList(String value) {
        if(isEmpty(value)) {
            return null;
        }
        char[] chs = trim(value).toCharArray();
        List<String> list = new ArrayList<>();
        for (char ch : chs) {
            list.add(toPinyin(ch));
        }
        return list;
    }

    public static String toPinyin(String value) {
        if(isEmpty(value)) {
            return "";
        }
        char[] chs = trim(value).toCharArray();
        StringBuffer buffer = new StringBuffer();
        for (char ch : chs) {
            buffer.append(toPinyin(ch));
        }
        return trim(buffer.toString());
    }

    public static int parseInt(String value) {
        return parseInt(value, 0);
    }

    public static int parseInt(String value, int defaultValue) {
        try {
            if (!isEmpty(value)) {
                return Integer.parseInt(trim(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static String encodeUrl(String url) {
        return trim(url).replaceAll(" ","%20");
    }

}
