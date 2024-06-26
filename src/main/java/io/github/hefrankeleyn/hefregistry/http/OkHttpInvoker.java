package io.github.hefrankeleyn.hefregistry.http;

import static com.google.common.base.Preconditions.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/5/15
 * @Author lifei
 */
public class OkHttpInvoker implements HttpInvoker{

    private static final Logger log = LoggerFactory.getLogger(OkHttpInvoker.class);

    private final OkHttpClient client;

    public OkHttpInvoker(long timeout) {
        this.client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkState(response.isSuccessful(), "Unexpected code " + response);
            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                String result = body.string();
                log.debug(" ====> get result : {}", result);
                return result;
            }
            return null;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public String post(String url, String requestString) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestString, JSON))
                .build();
        try (Response response = client.newCall(request).execute()){
            checkState(response.isSuccessful(), "Unexpected code " + response);
            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                String result = body.string();
                log.debug(" ====> post result: {}", result);
                return result;
            }
            return null;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
