package xyz.rive.jttplayer.util;


import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

    public static <T> T parseJson(String value, Class<T> clazz) {
        if(value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(value, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> String stringify(T value) {
        if(value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
