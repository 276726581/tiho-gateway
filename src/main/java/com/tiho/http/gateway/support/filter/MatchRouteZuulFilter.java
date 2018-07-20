package com.tiho.http.gateway.support.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tiho.http.gateway.domain.RequestInfo;
import com.tiho.http.gateway.domain.RouteConfig;
import com.tiho.http.gateway.support.TihoRouteLocator;
import com.tiho.http.gateway.util.ZuulConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.CompositeRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Component
public class MatchRouteZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(MatchRouteZuulFilter.class);

    @Autowired
    private CompositeRouteLocator routeLocator;

    @Autowired
    private TihoRouteLocator tihoRouteLocator;

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        URI uri = (URI) context.get(FilterConstants.REQUEST_URI_KEY);
        RequestInfo requestInfo = (RequestInfo) context.get(ZuulConstants.RequestInfo);

        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        Route route = routeLocator.getMatchingRoute(requestURI);
        if (null != route) {
            String location = route.getLocation();
            uri = buildUri(location, requestURI, queryString, route.isPrefixStripped());

            RouteConfig routeConfig = tihoRouteLocator.getRouteConfig(route, requestInfo);
            if (null != routeConfig) {
                location = routeConfig.getLocation();
                uri = buildUri(location, requestURI, queryString, routeConfig.isStripPrefix());
            }
        }
        context.set(FilterConstants.REQUEST_URI_KEY, uri);
        return null;
    }

    private URI buildUri(String location, String requestURI, String queryString, boolean stripPrefix) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(location);
        if (!stripPrefix) {
            uriComponentsBuilder.path(requestURI);
        }
        uriComponentsBuilder.replaceQuery(queryString);
        URI uri = uriComponentsBuilder.build().toUri();
        return uri;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
