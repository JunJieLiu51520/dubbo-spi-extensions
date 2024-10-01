package org.apache.dubbo.registry.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.*;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.ServiceNameMapping;
import org.apache.dubbo.registry.client.AbstractServiceDiscovery;
import org.apache.dubbo.registry.client.ServiceInstance;
import org.apache.dubbo.registry.client.event.ServiceInstancesChangedEvent;
import org.apache.dubbo.registry.client.event.listener.ServiceInstancesChangedListener;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EurekaServiceDiscovery extends AbstractServiceDiscovery {

    private static final String NAMESPACE = "namespace";

    private final ApplicationInfoManager applicationInfoManager;
    private final InstanceInfo instanceInfo;
    private final DiscoveryClient eurekaClient;
    private final ConcurrentMap<String,EurekaEventListener> eurekaEventListeners = new ConcurrentHashMap<>();

    public EurekaServiceDiscovery(ApplicationModel applicationModel, URL registryURL) {
        super(applicationModel, registryURL);
        String namespace = registryURL.getAttribute(NAMESPACE, ServiceNameMapping.buildMappingKey(registryURL)).toString();
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
        eurekaClient.registerHealthCheck(currentStatus -> InstanceInfo.InstanceStatus.UP);
    }
    
    @Override
    protected void doUnregister(ServiceInstance serviceInstance) {
        eurekaShutdown(serviceInstance.getServiceName());
    }
    
    @Override
    protected void doDestroy() throws Exception {
        eurekaShutdown(ServiceNameMapping.buildMappingKey(registryURL));
    }

    private void eurekaShutdown(String serviceName) {
        EurekaEventListener eventListener = eurekaEventListeners.remove(serviceName);
        if (Objects.nonNull(eventListener)){
            eurekaClient.unregisterEventListener(eventListener);
        }
        eurekaClient.shutdown();
    }

    @Override
    public void addServiceInstancesChangedListener(ServiceInstancesChangedListener listener) throws NullPointerException, IllegalArgumentException {
        listener.getServiceNames().forEach(serviceName -> registerServiceListener(serviceName, listener));
    }

    private void registerServiceListener(String serviceName, ServiceInstancesChangedListener listener) {
        eurekaEventListeners.computeIfAbsent(serviceName, serviceNameKey -> {
            EurekaEventListenerImpl eventListener = new EurekaEventListenerImpl(serviceNameKey, listener);
            eurekaClient.registerEventListener(eventListener);
            return eventListener;
        });
    }

    @Override
    public void removeServiceInstancesChangedListener(ServiceInstancesChangedListener listener) throws IllegalArgumentException {
        listener.getServiceNames().forEach(this::removeServiceListener);
    }

    private void removeServiceListener(String serviceName) {
        EurekaEventListener eventListener = eurekaEventListeners.remove(serviceName);
        if (Objects.nonNull(eventListener)){
            eurekaClient.unregisterEventListener(eventListener);
        }
    }

    /**
     * Gets all service names
     *
     * @return non-null read-only {@link Set}
     */
    @Override
    public Set<String> getServices() {
        return eurekaEventListeners.keySet();
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceName) throws NullPointerException {
        List<InstanceInfo> instanceInfoList = eurekaClient.getInstancesById(instanceInfo.getInstanceId());
        return convertToServiceInstances(instanceInfoList);
    }
    
    private List<ServiceInstance> convertToServiceInstances(List<InstanceInfo> instanceInfoList) {
        return null;
    }
    
    
    private static class EurekaEventListenerImpl implements EurekaEventListener {
        private final ServiceInstancesChangedListener listener;
        private final String serviceName;
        public EurekaEventListenerImpl(String serviceName, ServiceInstancesChangedListener listener) {
            this.serviceName = serviceName;
            this.listener = listener;
        }

        @Override
        public void onEvent(EurekaEvent event) {
            if (!(event instanceof StatusChangeEvent)) {
                return;
            }
            StatusChangeEvent changeEvent = (StatusChangeEvent) event;
            if (changeEvent.isUp()) {
                listener.onEvent(new ServiceInstancesChangedEvent(serviceName, Collections.emptyList()));
            }
        }
    }
}
