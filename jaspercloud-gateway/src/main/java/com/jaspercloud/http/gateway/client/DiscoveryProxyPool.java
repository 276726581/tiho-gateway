package com.jaspercloud.http.gateway.client;

import com.jaspercloud.http.gateway.exception.NotFoundException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import java.net.InetSocketAddress;

public class DiscoveryProxyPool implements ProxyPool {

    private String gatewayServiceName;
    private LoadBalancerClient loadBalancerClient;

    public DiscoveryProxyPool(String gatewayServiceName, LoadBalancerClient loadBalancerClient) {
        this.gatewayServiceName = gatewayServiceName;
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        ServiceInstance instance = loadBalancerClient.choose(gatewayServiceName);
        if (null == instance) {
            throw new NotFoundException();
        }
        String host = instance.getHost();
        int port = instance.getPort();
        InetSocketAddress address = new InetSocketAddress(host, port);
        return address;
    }
}
