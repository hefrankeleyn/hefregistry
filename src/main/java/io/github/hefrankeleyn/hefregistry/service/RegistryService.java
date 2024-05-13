package io.github.hefrankeleyn.hefregistry.service;

import com.google.common.collect.Maps;
import io.github.hefrankeleyn.hefregistry.beans.InstanceMeta;

import java.util.List;
import java.util.Map;

public interface RegistryService {
    Map<String, Long> TIMESTEMPS = Maps.newConcurrentMap();

    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> findAllInstance(String service);

    Long renew(InstanceMeta instance, String ...service);

    Long version(String service);

    Map<String, Long> versions(String ...services);

}
