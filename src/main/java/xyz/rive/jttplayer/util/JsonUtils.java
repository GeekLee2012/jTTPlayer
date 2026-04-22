package xyz.rive.jttplayer.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

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

    public static <T> T parseJson(JsonNode value, Class<T> clazz) {
        if(value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.treeToValue(value, clazz);
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

    public static JsonNode json(String value) {
        return json(value, null);
    }

    public static JsonNode json(String value, String path) {
        if(isEmpty(value)) {
            return null;
        }
        path = trim(path);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (node == null) {
            return null;
        }
        if (isEmpty(path) || "/".equals(path)) {
            return node;
        }
        String[] fieldNames = path.split("/");
        for (String name : fieldNames) {
            if (isEmpty(name)) {
                continue ;
            }
            node = node.path(name);
            if (node == null) {
                break ;
            }
        }
        return node;
    }
}
