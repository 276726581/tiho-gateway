package com.tiho.http.gateway.service;

import com.tiho.http.gateway.domain.RouteConfig;

import java.util.List;

public interface RouteConfigService {

    void saveRouteConfig(RouteConfig routeConfig);

    List<RouteConfig> getRouteConfigList();
}
