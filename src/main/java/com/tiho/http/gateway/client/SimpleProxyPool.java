package com.tiho.http.gateway.client;

import org.apache.commons.lang.math.RandomUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleProxyPool implements ProxyPool {

    private List<InetSocketAddress> list = new CopyOnWriteArrayList<>();

    public SimpleProxyPool() {
    }

    public SimpleProxyPool(List<InetSocketAddress> list) {
        this.list = list;
    }

    public void addInetSocketAddress(String host, int port) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        addInetSocketAddress(address);
    }

    public void addInetSocketAddress(InetSocketAddress address) {
        this.list.add(address);
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        int rand = RandomUtils.nextInt(list.size());
        InetSocketAddress address = list.get(rand);
        return address;
    }
}
