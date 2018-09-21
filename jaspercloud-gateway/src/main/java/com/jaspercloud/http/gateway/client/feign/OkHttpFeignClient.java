package com.jaspercloud.http.gateway.client.feign;

import com.jaspercloud.http.gateway.client.okhttp.OkHttpInterceptor;
import com.jaspercloud.http.gateway.exception.NotFoundException;
import feign.Client;
import feign.Request;
import feign.Response;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class OkHttpFeignClient implements Client {

    private OkHttpClient okHttpClient;
    private Map<Request.Options, OkHttpClient> okHttpClientMap = new ConcurrentHashMap<>();

    public OkHttpFeignClient(String gatewayAppName, LoadBalancerClient loadBalancerClient) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new OkHttpInterceptor(gatewayAppName, loadBalancerClient));
        okHttpClient = builder.build();
    }

    public OkHttpFeignClient(OkHttpClient okHttpClient) {
        List<Interceptor> interceptors = okHttpClient.interceptors();
        boolean find = false;
        for (Interceptor interceptor : interceptors) {
            if (interceptor instanceof OkHttpInterceptor) {
                find = true;
                break;
            }
        }
        if (!find) {
            throw new NotFoundException("not found OkHttpInterceptor");
        }
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        OkHttpClient client = buildClient(options);
        okhttp3.Request okHttpRequest = buildRequest(request);
        okhttp3.Response okHttpResponse = client.newCall(okHttpRequest).execute();
        Response response = buildResponse(okHttpResponse);
        return response;
    }

    private Response buildResponse(okhttp3.Response okHttpResponse) throws IOException {
        int code = okHttpResponse.code();
        byte[] bytes = okHttpResponse.body().bytes();
        Map<String, List<String>> map = okHttpResponse.headers().toMultimap();
        Map<String, Collection<String>> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            headers.put(key, value);
        }
        Response response = Response.builder()
                .status(code)
                .body(bytes)
                .headers(headers)
                .build();
        return response;
    }

    private okhttp3.Request buildRequest(Request request) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(request.url());
        RequestBody requestBody = null;
        if (!HttpMethod.GET.matches(request.method())) {
            Collection<String> headers = request.headers().get("Content-Type");
            String contentType = null;
            if (null != headers) {
                ArrayList<String> list = new ArrayList<>(headers);
                if (!list.isEmpty()) {
                    contentType = list.get(0);
                }
            }
            MediaType mediaType = null != contentType ? MediaType.parse(contentType) : null;
            requestBody = RequestBody.create(mediaType, request.body());
        }
        builder.method(request.method(), requestBody);
        for (Map.Entry<String, Collection<String>> entry : request.headers().entrySet()) {
            String key = entry.getKey();
            Collection<String> values = entry.getValue();
            for (String value : values) {
                builder.header(key, value);
            }
        }
        okhttp3.Request okHttpRequest = builder.build();
        return okHttpRequest;
    }

    private OkHttpClient buildClient(Request.Options options) {
        OkHttpClient client = okHttpClientMap.get(options);
        if (null == client) {
            OkHttpClient.Builder clientBuilder = okHttpClient.newBuilder();
            clientBuilder.connectTimeout(options.connectTimeoutMillis(), TimeUnit.MILLISECONDS);
            clientBuilder.readTimeout(options.readTimeoutMillis(), TimeUnit.MILLISECONDS);
            client = clientBuilder.build();
            okHttpClientMap.put(options, client);
        }
        return client;
    }
}
