package com.jaspercloud.http.gateway.service;

import com.jaspercloud.http.gateway.domain.RouteConfig;

import java.util.List;

public interface RouteConfigService {

    void saveRouteConfig(RouteConfig routeConfig);

    List<RouteConfig> getRouteConfigList();
}
