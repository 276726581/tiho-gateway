package com.jaspercloud.http.gateway.client;

import java.net.InetSocketAddress;

public class SingleProxyPool extends SimpleProxyPool {

    public SingleProxyPool(String host, int port) {
        this(new InetSocketAddress(host, port));
    }

    public SingleProxyPool(InetSocketAddress address) {
        addInetSocketAddress(address);
    }
}
