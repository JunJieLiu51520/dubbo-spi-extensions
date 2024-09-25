package org.apache.dubbo.registry.eureka;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.PropertiesInstanceConfig;


public class EurekaDataCenterInstanceConfig extends PropertiesInstanceConfig implements EurekaInstanceConfig {
    
    
    public EurekaDataCenterInstanceConfig(String namespace) {
        super(namespace);
    }

    public EurekaDataCenterInstanceConfig(String namespace, DataCenterInfo dataCenterInfo) {
        super(namespace, dataCenterInfo);
    }

}
