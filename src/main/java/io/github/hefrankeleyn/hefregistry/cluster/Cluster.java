package io.github.hefrankeleyn.hefregistry.cluster;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.hefrankeleyn.hefregistry.conf.HefRegistryConfProperties;
import io.github.hefrankeleyn.hefregistry.service.impl.HefRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.List;

/**
 * 存放集群相关内容
 * Date 2024/5/15
 *
 * @Author lifei
 */
public class Cluster {

    @Value("${server.port:8383}")
    private Integer port;

    private String host;

    private Server MYSELF;

    private static final Logger log = LoggerFactory.getLogger(Cluster.class);

    private HefRegistryConfProperties hefRegistryConfProperties;

    private List<Server> serverList;

    public Cluster(HefRegistryConfProperties hefRegistryConfProperties) {
        this.hefRegistryConfProperties = hefRegistryConfProperties;
    }

    public void init() {
        try {
            this.host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();
            log.debug("===> findFirstNonLoopbackAddress: {}", this.host);
        } catch (Exception e) {
            this.host = "127.0.0.1";
        }
        this.MYSELF = new Server(Strings.lenientFormat("http://%s:%s", this.host, this.port), true, false, -1L);
        log.debug("==> myself: {}", self());
        // 初始化server方法
        initServers();
        // 1. 状态检查； 2. 选主； 3. 通过快照同步数据；
        new ServerHealth(this).checkServerHealth();
    }

    private void initServers() {
        List<Server> serverList = Lists.newArrayList();
        for (String url : hefRegistryConfProperties.getServerList()) {
            if (url.contains("localhost")) {
                url = url.replace("localhost", this.host);
            } else if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", this.host);
            }
            if (url.equals(self().getUrl())) {
                serverList.add(self());
            } else {
                Server server = new Server();
                server.setUrl(url);
                server.setLeader(false);
                server.setStatus(false);
                server.setVersion(-1L);
                serverList.add(server);
            }
        }
        this.serverList = serverList;
    }


    public Server self() {
        MYSELF.setVersion(HefRegistryService.VERSION.get());
        return MYSELF;
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public Server leader() {
        return this.serverList.stream().filter(Server::getStatus).filter(Server::getLeader).findAny().orElse(null);
    }
}
