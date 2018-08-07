package com.jaspercloud.http.gateway.support.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.jaspercloud.http.gateway.domain.RequestInfo;
import com.jaspercloud.http.gateway.util.ZuulConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Component
public class BuildRouteUriZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(BuildRouteUriZuulFilter.class);

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        RequestInfo requestInfo = RequestInfo.build(request);
        context.set(ZuulConstants.RequestInfo, requestInfo);

        URI uri = getRequestUri(request);
        context.set(FilterConstants.REQUEST_URI_KEY, uri);
        return null;
    }

    private URI getRequestUri(HttpServletRequest request) {
        ServletServerHttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpRequest(httpRequest);
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
        return 0;
    }
}
