package io.github.hefrankeleyn.hefregistry.health;

import io.github.hefrankeleyn.hefregistry.beans.InstanceMeta;
import io.github.hefrankeleyn.hefregistry.service.RegistryService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/5/10
 * @Author lifei
 */
public class HefHealthChecker implements HealthChecker{

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private RegistryService registryService;

    public HefHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    private int timeout = 20 * 1000;

    @Override
    public void start() {
        executor.scheduleWithFixedDelay(()->{
            long now = System.currentTimeMillis();
            for (String serviceAndInstance : RegistryService.TIMESTEMPS.keySet()) {
                if (now - RegistryService.TIMESTEMPS.get(serviceAndInstance) > timeout) {
                    int index = serviceAndInstance.indexOf("@");
                    String service = serviceAndInstance.substring(0, index);
                    String instance = serviceAndInstance.substring(index + 1);
                    InstanceMeta instanceMeta = InstanceMeta.fromUrl(instance);
                    registryService.unregister(service, instanceMeta);
                    RegistryService.TIMESTEMPS.remove(serviceAndInstance);
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
