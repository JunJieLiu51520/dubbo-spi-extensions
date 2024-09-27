package org.apache.dubbo.registry.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.client.AbstractServiceDiscovery;
import org.apache.dubbo.registry.client.ServiceInstance;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.List;
import java.util.Set;

public class EurekaServiceDiscovery extends AbstractServiceDiscovery {

    private static final String NAME = "eureka";
    private static final String NAMESPACE = "namespace";
    private static final String DASH = "-";

    private final ApplicationInfoManager applicationInfoManager;
    private final InstanceInfo instanceInfo;
    private final DiscoveryClient eurekaClient;

    public EurekaServiceDiscovery(ApplicationModel applicationModel, URL url) {
        super(applicationModel, url);
        String namespace = url.getAttribute(NAMESPACE, NAME + DASH + url.getApplication()+ DASH +url.getAddress()).toString();
        EurekaDataCenterInstanceConfig instanceConfig = new EurekaDataCenterInstanceConfig(namespace);
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
        this.instanceInfo = instanceInfo;
        this.applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        this.eurekaClient = createEurekaClient(namespace);
    }

    private DiscoveryClient createEurekaClient(String namespace) {
        return new DiscoveryClient(applicationInfoManager,new DefaultEurekaClientConfig(namespace),null);
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
