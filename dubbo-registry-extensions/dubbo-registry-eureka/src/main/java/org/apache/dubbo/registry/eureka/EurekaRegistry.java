package org.apache.dubbo.registry.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.ServiceNameMapping;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.support.FailbackRegistry;

import java.util.ArrayList;
import java.util.List;

public class EurekaRegistry extends FailbackRegistry {
    
    private static final String NAMESPACE = "namespace";

    private final ApplicationInfoManager applicationInfoManager;
    private final InstanceInfo instanceInfo;
    private final DiscoveryClient eurekaClient;

    
    public EurekaRegistry(URL registryUrl) {
        super(registryUrl);
        String namespace = registryUrl.getAttribute(NAMESPACE, ServiceNameMapping.buildMappingKey(registryUrl)).toString();
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
    public void doRegister(URL url) {
        eurekaClient.registerHealthCheck(currentStatus -> InstanceInfo.InstanceStatus.UP);
    }
    
    @Override
    public void doUnregister(URL url) {
        eurekaClient.shutdown();
    }
    
    @Override
    public void doSubscribe(URL url, NotifyListener listener) {
        List<InstanceInfo> instanceInfoList = eurekaClient.getInstancesById(instanceInfo.getId());
        listener.notify(toUrls(instanceInfoList));
    }

    private List<URL> toUrls(List<InstanceInfo> instanceInfoList) {
        List<URL> urls = new ArrayList<>();
        for (InstanceInfo instanceInfo : instanceInfoList) {
            // TODO
        }
        return urls;
    }

    @Override
    public void doUnsubscribe(URL url, NotifyListener listener) {
        eurekaClient.shutdown();
    }
    
    @Override
    public boolean isAvailable() {
        return eurekaClient.getInstanceRemoteStatus() == InstanceInfo.InstanceStatus.UP;
    }
}
