package org.apache.dubbo.registry.eureka;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.client.AbstractServiceDiscovery;
import org.apache.dubbo.registry.client.ServiceInstance;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.List;
import java.util.Set;

public class EurekaServiceDiscovery extends AbstractServiceDiscovery {
    
    public EurekaServiceDiscovery(ApplicationModel applicationModel, URL registryURL) {
        super(applicationModel, registryURL);
    }
    
    @Override
    protected void doRegister(ServiceInstance serviceInstance) throws RuntimeException {
    
    }
    
    @Override
    protected void doUnregister(ServiceInstance serviceInstance) {
    
    }
    
    @Override
    protected void doDestroy() throws Exception {
    
    }
    
    /**
     * Gets all service names
     *
     * @return non-null read-only {@link Set}
     */
    @Override
    public Set<String> getServices() {
        return null;
    }
    
    @Override
    public List<ServiceInstance> getInstances(String serviceName) throws NullPointerException {
        return null;
    }
}
