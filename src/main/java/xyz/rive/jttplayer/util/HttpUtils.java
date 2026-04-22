package xyz.rive.jttplayer.util;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static xyz.rive.jttplayer.util.JsonUtils.parseJson;
import static xyz.rive.jttplayer.util.JsonUtils.stringify;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;


public class HttpUtils {
    private final static OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static <T> T getJson(String url, Class<T> clazz) {
        return parseJson(requestString(url, null, null, null, false), clazz);
    }

    public static String post(String url, Map<String, Object> params) {
        return requestString(url, "POST", null, params, false);
    }

    public static String request(String url) {
        return requestString(url, null, null, null, false);
    }

    public static byte[] requestBytes(String url) {
        return requestBytes(url, null, null, null, false);
    }


    public static <T> T request(
            String url,
            String method,
            Map<String, String> headers,
            Map<String, Object> params,
            boolean isJsonRequestBody,
            Function<ResponseBody, T> responseBodyHandle) {
        Request.Builder builder = new Request.Builder();
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return null;
        }
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        method = isEmpty(method) ? "GET" : method;
        if ("GET".equalsIgnoreCase(method)
                && (params != null && !params.isEmpty())) {
            HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
            for (String key : params.keySet()) {
                urlBuilder.addQueryParameter(key, String.valueOf(params.get(key)));
            }
            builder.url(urlBuilder.build());
        } else if ("POST".equalsIgnoreCase(method)
                && (params != null && !params.isEmpty())) {
            if (isJsonRequestBody) {
                builder.post(RequestBody.create(JSON, stringify(params)));
            } else {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                for (String key : params.keySet()) {
                    formBodyBuilder.add(key, String.valueOf(params.get(key)));
                }
                builder.post(formBodyBuilder.build());
            }
            builder.url(url);
        } else {
            builder.url(url);
        }
        Request request = builder.build();
        try (Response response = CLIENT.newCall(request).execute()){
            return responseBodyHandle != null ?
                    responseBodyHandle.apply(response.body())
                    : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String requestString(
            String url,
            String method,
            Map<String, String> headers,
            Map<String, Object> params,
            boolean isJsonRequestBody) {
        return request(url, method, headers, params, isJsonRequestBody,
                responseBody -> {
                    try {
                        return responseBody != null ? responseBody.string() : null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    public static byte[] requestBytes(
            String url,
            String method,
            Map<String, String> headers,
            Map<String, Object> params,
            boolean isJsonRequestBody) {
        return request(url, method, headers, params, isJsonRequestBody,
                responseBody -> {
                    try {
                        return responseBody != null ? responseBody.bytes() : null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }
}
