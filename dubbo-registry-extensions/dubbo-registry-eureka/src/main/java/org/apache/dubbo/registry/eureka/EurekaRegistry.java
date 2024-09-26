package org.apache.dubbo.registry.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.support.FailbackRegistry;

public class EurekaRegistry extends FailbackRegistry {
    
    private static final String NAME = "eureka";
    
    private final ApplicationInfoManager applicationInfoManager;
    private final EurekaClient eurekaClient;
    
    
    public EurekaRegistry(URL url) {
        super(url);
        EurekaDataCenterInstanceConfig instanceConfig = new EurekaDataCenterInstanceConfig(NAME);
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
        applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        this.eurekaClient = createEurekaClient(url);
    }
    
    private EurekaClient createEurekaClient(URL url) {
        DefaultEurekaClientConfig eurekaClientConfig = new DefaultEurekaClientConfig(NAME);
        return new DiscoveryClient(applicationInfoManager,eurekaClientConfig);
    }
    
    @Override
    public void doRegister(URL url) {
        eurekaClient.re
    }
    
    @Override
    public void doUnregister(URL url) {
    
    }
    
    @Override
    public void doSubscribe(URL url, NotifyListener listener) {
    
    }
    
    @Override
    public void doUnsubscribe(URL url, NotifyListener listener) {
    
    }
    
    
    @Override
    public boolean isAvailable() {
        return false;
    }
}
