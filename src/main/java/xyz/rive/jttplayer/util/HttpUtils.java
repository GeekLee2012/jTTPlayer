package xyz.rive.jttplayer.util;

import okhttp3.*;

import static xyz.rive.jttplayer.util.JsonUtils.parseJson;


public class HttpUtils {
    private final static OkHttpClient httpClient = new OkHttpClient();

    public static String getRaw(String url) {
        Request request = new Request.Builder().url(url).build();
        try(Response response = httpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            return body != null ? body.toString() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getJson(String url, Class<T> clazz) {
        return parseJson(getRaw(url), clazz);
    }

}
