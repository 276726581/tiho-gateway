package com.tiho.http.gateway.support.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tiho.http.gateway.exception.LoopRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.URI;

@Component
public class LoopRequestZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(LoopRequestZuulFilter.class);

    private static final InetAddress localhost;

    static {
        try {
            localhost = InetAddress.getLocalHost();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        URI uri = (URI) context.get(FilterConstants.REQUEST_URI_KEY);
        if (isLoopRequest(uri, request.getLocalPort())) {
            throw new LoopRequestException(uri.getHost() + ":" + uri.getPort());
        }
        return null;
    }

    private boolean isLoopRequest(URI uri, int serverPort) {
        String host = uri.getHost();
        int port = uri.getPort();
        if (-1 == port) {
            if (uri.getScheme().equals("http")) {
                port = 80;
            } else {
                port = 443;
            }
        }
        if (("127.0.0.1".equalsIgnoreCase(host)
                || "localhost".equalsIgnoreCase(host)
                || localhost.getHostAddress().equalsIgnoreCase(host)
                || localhost.getHostName().equalsIgnoreCase(host))
                && serverPort == port) {
            return true;
        } else {
            return false;
        }
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
        return 4;
    }
}
