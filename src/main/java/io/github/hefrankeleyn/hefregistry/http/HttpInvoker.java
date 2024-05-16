package io.github.hefrankeleyn.hefregistry.http;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public interface HttpInvoker {

    Logger loger = LoggerFactory.getLogger(HttpInvoker.class);

    HttpInvoker DEFAULT = new OkHttpInvoker(500);

    String get(String url);

    String post(String url, String requestString);

    static <T> T httpGet(String url, Class<T> clazz) {
        loger.debug(" ===> http get: {} ", url);
        String result = DEFAULT.get(url);
        loger.debug("==> http get result: {}", result);
        if (Objects.isNull(result)) {
            return null;
        }
        return new Gson().fromJson(result, clazz);
    }

    static <T> T httpPost(String url, String requestString, Class<T> clazz) {
        loger.debug(" ===> http post: {} ", url);
        String result = DEFAULT.post(url, requestString);
        loger.debug("==> http post result: {}", result);
        if (Objects.isNull(result)) {
            return null;
        }
        return new Gson().fromJson(result, clazz);
    }
}
