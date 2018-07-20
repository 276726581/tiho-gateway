package com.tiho.http.gateway.support.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tiho.http.gateway.domain.RequestInfo;
import com.tiho.http.gateway.util.ZuulConstants;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Component
public class RouteZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(RouteZuulFilter.class);

    @Autowired
    private OkHttpClient okHttpClient;

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        URI uri = (URI) context.get(FilterConstants.REQUEST_URI_KEY);
        RequestInfo requestInfo = (RequestInfo) context.get(ZuulConstants.RequestInfo);

        try {
            Request httpRequest = buildHttpRequest(requestInfo, request, uri);
            logger.info(String.format("src=%s, dest=%s", request.getRequestURL().toString(), uri.toString()));
            long startTime = System.currentTimeMillis();
            Response resp = okHttpClient.newCall(httpRequest).execute();
            long endTime = System.currentTimeMillis();
            writeResponse(resp, context);
            logger.info(String.format("time=%s, code=%s, src=%s", (endTime - startTime), resp.code(), request.getRequestURL().toString()));
        } catch (Exception e) {
            logger.info(String.format("exception src=%s", request.getRequestURL().toString()));
            throw new ZuulException(e, 500, e.getMessage());
        }
        return null;
    }

    private void writeResponse(Response resp, RequestContext context) throws Exception {
        HttpServletResponse response = context.getResponse();
        int code = resp.code();
        byte[] body = resp.body().bytes();

        response.setStatus(code);
        Map<String, List<String>> headerMap = resp.headers().toMultimap();
        for (Map.Entry<String, List<String>> headers : headerMap.entrySet()) {
            String key = headers.getKey();
            List<String> list = headers.getValue();
            for (String value : list) {
                response.addHeader(key, value);
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(body);
            outputStream.flush();
        } finally {
            if (null != outputStream) {
                outputStream.close();
            }
        }
    }

    private Request buildHttpRequest(RequestInfo requestInfo, HttpServletRequest request, URI uri) {
        Request.Builder builder = new Request.Builder();
        String url = uri.toString();
        builder.url(url);

        String method = request.getMethod();
        if (!HttpMethod.GET.matches(method)) {
            String contentType = request.getContentType();
            byte[] bytes = requestInfo.getBytes();
            MediaType mediaType = null != contentType ? MediaType.parse(contentType) : null;
            RequestBody requestBody = RequestBody.create(mediaType, bytes);
            builder.method(method, requestBody);
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            if (ZuulConstants.GatewayHost.equalsIgnoreCase(key)) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(key);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                if (ZuulConstants.Host.equalsIgnoreCase(key)) {
                    value = uri.getHost();
                }
                builder.addHeader(key, value);
            }
        }

        Request req = builder.build();
        return req;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
