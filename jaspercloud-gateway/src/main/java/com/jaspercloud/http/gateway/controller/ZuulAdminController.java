package com.jaspercloud.http.gateway.controller;

import com.jaspercloud.http.gateway.domain.RouteConfig;
import com.jaspercloud.http.gateway.service.RouteConfigService;
import com.jaspercloud.http.gateway.support.JasperCloudRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/route/admin")
public class ZuulAdminController implements ApplicationEventPublisherAware {

    @Autowired
    private RouteConfigService routeConfigService;

    @Autowired
    private JasperCloudRouteLocator jasperCloudRouteLocator;

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @PostMapping("/routeConfig")
    public void saveRouteConfig(RouteConfig routeConfig) {
        routeConfigService.saveRouteConfig(routeConfig);
    }

    @GetMapping("/list")
    public Map<String, List<RouteConfig>> getList() {
        Map<String, List<RouteConfig>> configMap = jasperCloudRouteLocator.getRouteConfigMap();
        return configMap;
    }

    @GetMapping("/refresh")
    public void refresh() {
        eventPublisher.publishEvent(new RoutesRefreshedEvent(jasperCloudRouteLocator));
    }
}
