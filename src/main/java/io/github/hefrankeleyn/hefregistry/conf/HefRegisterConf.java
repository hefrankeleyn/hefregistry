package io.github.hefrankeleyn.hefregistry.conf;

import io.github.hefrankeleyn.hefregistry.cluster.Cluster;
import io.github.hefrankeleyn.hefregistry.health.HealthChecker;
import io.github.hefrankeleyn.hefregistry.health.HefHealthChecker;
import io.github.hefrankeleyn.hefregistry.service.RegistryService;
import io.github.hefrankeleyn.hefregistry.service.impl.HefRegistryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2024/5/10
 * @Author lifei
 */
@Configuration
public class HefRegisterConf {

    @Bean
    public RegistryService registryService() {
        return new HefRegistryService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(RegistryService registryService) {
        return new HefHealthChecker(registryService);
    }

    @Bean(initMethod = "init")
    public Cluster cluster(HefRegistryConfProperties hefRegistryConfProperties) {
        return new Cluster(hefRegistryConfProperties);
    }
}
