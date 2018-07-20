package com.tiho.http.gateway.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Enumeration;

public class RequestInfo {

    private String method;
    private HttpHeaders headers = new HttpHeaders();
    private MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    private byte[] bytes = new byte[0];

    public static RequestInfo build(HttpServletRequest request) {
        RequestInfo requestInfo = new RequestInfo();

        String method = request.getMethod();
        requestInfo.setMethod(method);

        Enumeration<String> headerNamesEnumeration = request.getHeaderNames();
        while (headerNamesEnumeration.hasMoreElements()) {
            String key = headerNamesEnumeration.nextElement();
            Enumeration<String> values = request.getHeaders(key);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                requestInfo.getHeaders().add(key, value);
            }
        }

        Enumeration<String> paramsEnumeration = request.getParameterNames();
        while (paramsEnumeration.hasMoreElements()) {
            String key = paramsEnumeration.nextElement();
            String[] values = request.getParameterValues(key);
            for (String value : values) {
                requestInfo.getParams().add(key, value);
            }
        }

        int size = request.getContentLength();
        if (size > 0) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
                byte[] buf = new byte[size];
                inputStream.read(buf);
                requestInfo.setBytes(buf);
            } catch (Exception e) {
                throw new RuntimeException();
            } finally {
                if (null != inputStream) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                }
            }
        }

        return requestInfo;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public MultiValueMap<String, String> getParams() {
        return params;
    }

    public void setParams(MultiValueMap<String, String> params) {
        this.params = params;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public RequestInfo() {
    }

    public void addHeader(String key, String value) {
        headers.add(key, value);
    }

    public void addParam(String key, String value) {
        params.add(key, value);
    }
}
