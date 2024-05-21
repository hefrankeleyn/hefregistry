package io.github.hefrankeleyn.hefregistry.controller;

import static com.google.common.base.Preconditions.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.github.hefrankeleyn.hefregistry.beans.InstanceMeta;
import io.github.hefrankeleyn.hefregistry.beans.Snapshot;
import io.github.hefrankeleyn.hefregistry.cluster.Cluster;
import io.github.hefrankeleyn.hefregistry.cluster.Server;
import io.github.hefrankeleyn.hefregistry.service.RegistryService;
import io.github.hefrankeleyn.hefregistry.service.impl.HefRegistryService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Resource
    private Cluster cluster;

    private void checkLeader() {
        checkState(cluster.self().getLeader(), "current server is not leader");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public InstanceMeta register(@RequestParam("service") String service, @RequestBody InstanceMeta instanceMeta) {
        LOGGER.debug(" ===> register {} @ {}", service, instanceMeta);
        checkLeader();
        return registryService.register(service, instanceMeta);
    }

    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    public InstanceMeta unregister(@RequestParam("service") String service, @RequestBody InstanceMeta instanceMeta) {
        LOGGER.debug(" ===> unregister {} @ {}", service, instanceMeta);
        checkLeader();
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
        checkLeader();
        return registryService.renew(instance, service);
    }

    @RequestMapping(value = "/renews", method = RequestMethod.POST)
    public Long renews(@RequestParam("service") String services, @RequestBody InstanceMeta instance) {
        LOGGER.debug(" ===> renews {} @ {}", services, instance);
        checkLeader();
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

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String info() {
        LOGGER.debug("===> info: {}", cluster.self());
        return new Gson().toJson(cluster.self());
    }

    @RequestMapping(value = "/cluster", method = RequestMethod.GET)
    public List<Server> cluster() {
        LOGGER.debug("===> cluster: {}", cluster.getServerList());
        return cluster.getServerList();
    }

    @RequestMapping(value = "/leader", method = RequestMethod.GET)
    public Server leader() {
        LOGGER.debug("===> leader: {}", cluster.leader());
        return cluster.leader();
    }

    @RequestMapping(value = "/sf", method = RequestMethod.GET)
    public Server sf() {
        cluster.self().setLeader(true);
        LOGGER.debug("===> myself: {}", cluster.self());
        return cluster.self();
    }

    @RequestMapping(value = "/snapshot", method = RequestMethod.GET)
    public Snapshot snapshot() {
        return HefRegistryService.createSnapshot();
    }



}
