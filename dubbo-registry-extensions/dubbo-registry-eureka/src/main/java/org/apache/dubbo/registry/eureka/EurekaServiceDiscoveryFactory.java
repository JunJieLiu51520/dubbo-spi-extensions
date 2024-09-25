package org.apache.dubbo.registry.eureka;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.client.AbstractServiceDiscoveryFactory;
import org.apache.dubbo.registry.client.ServiceDiscovery;

public class EurekaServiceDiscoveryFactory extends AbstractServiceDiscoveryFactory {
    
    @Override
    protected ServiceDiscovery createDiscovery(URL registryURL) {
        return new EurekaServiceDiscovery(applicationModel, registryURL);
    }
}
