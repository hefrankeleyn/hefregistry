package io.github.hefrankeleyn.hefregistry.controller;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.github.hefrankeleyn.hefregistry.beans.InstanceMeta;
import io.github.hefrankeleyn.hefregistry.service.RegistryService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @Date 2024/5/10
 * @Author lifei
 */
@RestController
public class HefRegisterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HefRegisterController.class);

    @Resource
    private RegistryService registryService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public InstanceMeta register(@RequestParam("service") String service, @RequestBody InstanceMeta instanceMeta) {
        LOGGER.debug(" ===> register {} @ {}", service, instanceMeta);
        return registryService.register(service, instanceMeta);
    }

    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    public InstanceMeta unregister(@RequestParam("service") String service, @RequestBody InstanceMeta instanceMeta) {
        LOGGER.debug(" ===> unregister {} @ {}", service, instanceMeta);
        return registryService.unregister(service, instanceMeta);
    }

    @RequestMapping(value = "/findAllInstances", method = RequestMethod.GET)
    public List<InstanceMeta> findAllInstances(@RequestParam("service") String service) {
        LOGGER.debug(" ====> findAllInstances {}", service);
        return registryService.findAllInstance(service);
    }

    @RequestMapping(value = "/renew", method = RequestMethod.POST)
    public Long renew(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        LOGGER.debug(" ===> renew {} @ {}", service, instance);
        return registryService.renew(instance, service);
    }

    @RequestMapping(value = "/renews", method = RequestMethod.POST)
    public Long renews(@RequestParam("service") String services, @RequestBody InstanceMeta instance) {
        LOGGER.debug(" ===> renews {} @ {}", services, instance);
        List<String> serviceList = Lists.newArrayList(Splitter.on(",").omitEmptyStrings().trimResults().split(Optional.ofNullable(services).orElse("")));
        String[] serviceArray = serviceList.toArray(new String[]{});
        return registryService.renew(instance,serviceArray);
    }


    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Long version(@RequestParam("service") String service) {
        return registryService.version(service);
    }

    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    public Map<String, Long> versions(@RequestParam("service") String services) {
        List<String> serviceList = Lists.newArrayList(Splitter.on(",").omitEmptyStrings().trimResults().split(Optional.ofNullable(services).orElse("")));
        String[] serviceArray = serviceList.toArray(new String[]{});
        return registryService.versions(serviceArray);
    }


}
