package io.github.hefrankeleyn.hefregistry.conf;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Date 2024/5/15
 * @Author lifei
 */
@ConfigurationProperties(prefix = "hefregistry")
public class HefRegistryConfProperties {
    private List<String> serverList = Lists.newArrayList();

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }
}
