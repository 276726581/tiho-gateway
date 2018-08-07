package com.jaspercloud.http.gateway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteConfig {

    private String id;
    private String path;
    private String location;
    private List<String> domains = new ArrayList<>();
    private List<String> methods = new ArrayList<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    /**
     * 删除前缀
     */
    private boolean stripPrefix = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public boolean isStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public RouteConfig() {
    }

    public void addDomain(String domain) {
        this.domains.add(domain);
    }

    public void addMethod(String method) {
        this.methods.add(method);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addParam(String key, String value) {
        this.params.put(key, value);
    }

    @Override
    public String toString() {
        return "RouteConfig{" +
                "id='" + id + '\'' +
                ", path='" + path + '\'' +
                ", location='" + location + '\'' +
                ", domains=" + domains +
                ", methods=" + methods +
                ", headers=" + headers +
                ", params=" + params +
                ", stripPrefix=" + stripPrefix +
                '}';
    }
}
