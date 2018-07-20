package com.tiho.http.gateway.support.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class DiscoveryServiceZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(DiscoveryServiceZuulFilter.class);

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        URI uri = (URI) context.get(FilterConstants.REQUEST_URI_KEY);
        String host = uri.getHost();
        ServiceInstance serviceInstance = loadBalancerClient.choose(host);
        if (null != serviceInstance) {
            uri = loadBalancerClient.reconstructURI(serviceInstance, uri);
        }
        context.set(FilterConstants.REQUEST_URI_KEY, uri);
        return null;
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
        return 3;
    }
}
