package io.github.hefrankeleyn.hefregistry;

import io.github.hefrankeleyn.hefregistry.conf.HefRegistryConfProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {HefRegistryConfProperties.class})
public class HefregistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HefregistryApplication.class, args);
    }

}
