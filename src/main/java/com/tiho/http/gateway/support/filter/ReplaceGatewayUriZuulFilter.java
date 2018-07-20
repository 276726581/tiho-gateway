package com.tiho.http.gateway.support.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tiho.http.gateway.util.ZuulConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Component
public class ReplaceGatewayUriZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(ReplaceGatewayUriZuulFilter.class);

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        URI uri = (URI) context.get(FilterConstants.REQUEST_URI_KEY);
        uri = replaceGatewayUri(uri, request);
        context.set(FilterConstants.REQUEST_URI_KEY, uri);
        return null;
    }

    private URI replaceGatewayUri(URI uri, HttpServletRequest request) {
        String gatewayHost = request.getHeader(ZuulConstants.GatewayHost);
        if (null == gatewayHost) {
            return uri;
        }
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        URI gatewayUri = URI.create(gatewayHost);
        uriComponentsBuilder.scheme(gatewayUri.getScheme());
        uriComponentsBuilder.host(gatewayUri.getHost());
        uriComponentsBuilder.port(gatewayUri.getPort());
        URI result = uriComponentsBuilder.build().toUri();
        return result;
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
        return 1;
    }
}
