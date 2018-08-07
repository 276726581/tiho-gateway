package com.jaspercloud.http.gateway.support;

import com.jaspercloud.http.gateway.domain.RequestInfo;
import com.jaspercloud.http.gateway.domain.RouteConfig;
import com.jaspercloud.http.gateway.service.RouteConfigService;
import com.jaspercloud.http.gateway.util.LockCallback;
import com.jaspercloud.http.gateway.util.MatcherUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class JasperCloudRouteLocator extends SimpleRouteLocator implements InitializingBean, RefreshableRouteLocator {

    public JasperCloudRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
    }

    private Route ROUTE = new Route("route", "/**", "route", "", false, Collections.EMPTY_SET);

    private PathMatcher pathMatcher = new AntPathMatcher();
    private List<Route> dbRouteList = new ArrayList<>();
    private Map<String, List<RouteConfig>> routeConfigMap = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Autowired
    private RouteConfigService routeConfigService;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public Map<String, List<RouteConfig>> getRouteConfigMap() {
        Map<String, List<RouteConfig>> map = LockCallback.readLock(readWriteLock, new LockCallback.Callback<Map<String, List<RouteConfig>>>() {
            @Override
            public Map<String, List<RouteConfig>> onCall() {
                return routeConfigMap;
            }
        });
        return map;
    }

    public void addRouteConfig(RouteConfig routeConfig) {
        Route route = new Route(routeConfig.getId(), routeConfig.getPath(), routeConfig.getLocation(),
                "", false, Collections.EMPTY_SET, routeConfig.isStripPrefix());
        dbRouteList.add(route);

        List<RouteConfig> routeConfigList = routeConfigMap.get(routeConfig.getPath());
        if (null == routeConfigList) {
            routeConfigList = new ArrayList<>();
            routeConfigMap.put(routeConfig.getPath(), routeConfigList);
        }
        routeConfigList.add(routeConfig);
    }

    public RouteConfig getRouteConfig(Route route, RequestInfo requestInfo) {
        RouteConfig routeConfig = LockCallback.readLock(readWriteLock, new LockCallback.Callback<RouteConfig>() {
            @Override
            public RouteConfig onCall() {
                List<RouteConfig> routeConfigList = routeConfigMap.get(route.getPath());
                if (null == routeConfigList) {
                    return null;
                }
                for (RouteConfig rc : routeConfigList) {
                    boolean match = false;

                    List<String> domains = rc.getDomains();
                    if (!domains.isEmpty()) {
                        for (String domain : domains) {
                            if (MatcherUtil.match(domain, requestInfo.getHeaders().getFirst("Host"))) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            continue;
                        }
                    }

                    match = false;

                    List<String> methods = rc.getMethods();
                    if (!methods.isEmpty()) {
                        for (String method : methods) {
                            if ("*".equals(method) || method.equalsIgnoreCase(requestInfo.getMethod())) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            continue;
                        }
                    }

                    match = false;

                    Map<String, String> headers = rc.getHeaders();
                    if (!headers.isEmpty()) {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            String header = requestInfo.getHeaders().getFirst(key);
                            if (MatcherUtil.match(value, header)) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            continue;
                        }
                    }

                    match = false;

                    Map<String, String> params = rc.getParams();
                    if (!params.isEmpty()) {
                        for (Map.Entry<String, String> param : params.entrySet()) {
                            String key = param.getKey();
                            String value = param.getValue();
                            String p = requestInfo.getParams().getFirst(key);
                            if (MatcherUtil.match(value, p)) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            continue;
                        }
                    }

                    return rc;
                }
                return null;
            }
        });
        return routeConfig;
    }

    @Override
    public Route getMatchingRoute(String path) {
        Route route = super.getMatchingRoute(path);
        if (null != route) {
            return route;
        }
        route = LockCallback.readLock(readWriteLock, new LockCallback.Callback<Route>() {
            @Override
            public Route onCall() {
                for (Route r : dbRouteList) {
                    if (pathMatcher.match(r.getPath(), path)) {
                        return r;
                    }
                }
                return null;
            }
        });
        return route;
    }

    @Override
    public void refresh() {
        LockCallback.writeLock(readWriteLock, new LockCallback.Callback<Void>() {
            @Override
            public Void onCall() {
                dbRouteList.clear();
                routeConfigMap.clear();
                List<RouteConfig> routeConfigList = routeConfigService.getRouteConfigList();
                routeConfigList.forEach(new Consumer<RouteConfig>() {
                    @Override
                    public void accept(RouteConfig routeConfig) {
                        addRouteConfig(routeConfig);
                    }
                });
                return null;
            }
        });
    }

    @Override
    public List<Route> getRoutes() {
        List<Route> routes = LockCallback.readLock(readWriteLock, new LockCallback.Callback<List<Route>>() {
            @Override
            public List<Route> onCall() {
                return dbRouteList;
            }
        });
        List<Route> result = new ArrayList<>();
        List<Route> superList = super.getRoutes();
        result.addAll(superList);
        result.addAll(routes);
        result.add(ROUTE);
        return result;
    }
}
