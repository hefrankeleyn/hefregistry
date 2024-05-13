package io.github.hefrankeleyn.hefregistry.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.github.hefrankeleyn.hefregistry.beans.InstanceMeta;
import io.github.hefrankeleyn.hefregistry.service.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * registryService 的默认实现
 * @Date 2024/5/10
 * @Author lifei
 */
public class HefRegistryService implements RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HefRegistryService.class);

    private static final MultiValueMap<String, InstanceMeta> REGISTERY = new LinkedMultiValueMap<>();

    private static final Map<String, Long> VERSIONS = Maps.newConcurrentMap();

    private static final AtomicLong VERSION = new AtomicLong(0);



    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> instanceList = REGISTERY.get(service);
        if (Objects.nonNull(instanceList) && instanceList.size()>0) {
            if (instanceList.contains(instance)) {
                LOGGER.debug("instance {} already registered", instance);
                instance.setStatus(true);
                return instance;
            }
        }
        LOGGER.debug("registering instance {}", instance);
        REGISTERY.add(service, instance);
        instance.setStatus(true);
        renew(instance, service);
        VERSIONS.put(service, VERSION.getAndIncrement());
        return instance;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> instanceList = REGISTERY.get(service);
        if (Objects.isNull(instanceList) || instanceList.isEmpty()) {
            return null;
        }
        LOGGER.debug("unregister instance {}", instance.toUrl());
        instanceList.removeIf(m->m.equals(instance));
        instance.setStatus(false);
        renew(instance, service);
        VERSIONS.put(service, VERSION.getAndIncrement());
        return instance;
    }

    @Override
    public List<InstanceMeta> findAllInstance(String service) {
        return REGISTERY.get(service);
    }

    @Override
    public Long renew(InstanceMeta instance, String ...services) {
        long now = System.currentTimeMillis();
        for (String service : services) {
            TIMESTEMPS.put(Strings.lenientFormat("%s@%s", service, instance.toUrl()), now);
        }
        return now;
    }

    @Override
    public Long version(String service) {
        return VERSIONS.get(service);
    }

    @Override
    public Map<String, Long> versions(String ...services) {
        return Arrays.stream(services).collect(Collectors.toMap(x->x, VERSIONS::get, (v1,v2)->v2));
    }
}
