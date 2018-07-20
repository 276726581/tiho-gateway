package com.tiho.http.gateway.service.impl;

import com.google.gson.Gson;
import com.tiho.http.gateway.dao.RouteConfigMapper;
import com.tiho.http.gateway.domain.RouteConfig;
import com.tiho.http.gateway.service.RouteConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RouteConfigServiceImpl implements RouteConfigService, InitializingBean {

    @Autowired
    private RouteConfigMapper routeConfigMapper;

    @Autowired
    private Gson gson;

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public void saveRouteConfig(RouteConfig routeConfig) {
        String json = gson.toJson(routeConfig);
        routeConfigMapper.saveRouteConfig(json);
    }

    @Override
    public List<RouteConfig> getRouteConfigList() {
        List<String> list = routeConfigMapper.getRouteConfigList();
        List<RouteConfig> routeConfigList = list.stream().map(new Function<String, RouteConfig>() {
            @Override
            public RouteConfig apply(String json) {
                RouteConfig routeConfig = gson.fromJson(json, RouteConfig.class);
                return routeConfig;
            }
        }).collect(Collectors.toList());
        return routeConfigList;
    }
}
