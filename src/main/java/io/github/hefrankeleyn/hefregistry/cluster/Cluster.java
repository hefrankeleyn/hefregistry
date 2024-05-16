package io.github.hefrankeleyn.hefregistry.cluster;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import io.github.hefrankeleyn.hefregistry.conf.HefRegistryConfProperties;
import io.github.hefrankeleyn.hefregistry.http.HttpInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 存放集群相关内容
 * Date 2024/5/15
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

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    private static final int delay = 5_000;

    public void init() {
        try {
            this.host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();
            log.debug("===> findFirstNonLoopbackAddress: {}", this.host);
        }catch (Exception e) {
            this.host = "127.0.0.1";
        }

        this.MYSELF = new Server(Strings.lenientFormat("http://%s:%s", this.host, this.port), true, false, -1L);
        log.debug("==> myself: {}", this.MYSELF);

        List<Server> serverList = Lists.newArrayList();
        for (String url : hefRegistryConfProperties.getServerList()) {
            if (url.contains("localhost")) {
                url = url.replace("localhost", this.host);
            }else if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", this.host);
            }
            if (url.equals(this.host)) {
                serverList.add(MYSELF);
            }else {
                Server server = new Server();
                server.setUrl(url);
                server.setLeader(false);
                server.setStatus(false);
                server.setVersion(-1L);
                serverList.add(server);
            }
        }
        this.serverList = serverList;

        // 探活
        executor.scheduleWithFixedDelay(()->{
            try {
                updateServers();
                selectLeader();
            }catch (Exception e) {
                log.debug(Throwables.getStackTraceAsString(e));
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
    }

    private void selectLeader() {
        List<Server> leaderList = this.serverList.stream().filter(Server::getStatus).filter(Server::getLeader)
                .toList();
        if (leaderList.isEmpty()) {
            log.debug("===> select for no leader found: {}", this.serverList);
            select();
        } else if (leaderList.size() > 1) {
            log.debug("===> select for more than one leader: {}", serverList);
            select();
        } else {
            leaderList.get(0).setLeader(true);
            log.debug("===> selectLeader success: {}", leaderList.get(0));
        }
    }

    private void select() {
        Server candidate = null;
        for (Server server : this.serverList) {
            server.setLeader(false);
            if (!server.getStatus()) {
                continue;
            }
            if (Objects.isNull(candidate)) {
                candidate = server;
            }else if (server.hashCode() < candidate.hashCode()) {
                candidate = server;
            }
        }
        if (Objects.nonNull(candidate)) {
            candidate.setLeader(true);
            log.debug("===> select for leader : {}", candidate);
        } else {
            log.debug("===> select leader fail!");
        }
    }

    private void updateServers() {
        for (Server server : serverList) {
            try {
                Server serverInfo = HttpInvoker.httpGet(Strings.lenientFormat("%s/info", server.getUrl()), Server.class);
                if (Objects.nonNull(serverInfo)) {
                    log.debug(" ==> health check success for: {}", serverInfo);
                    server.setStatus(true);
                    server.setLeader(serverInfo.getStatus());
                    server.setVersion(serverInfo.getVersion());
                } else {
                    server.setStatus(false);
                    server.setLeader(false);
                }
            }catch (Exception e) {
                log.debug(" ==> health check error for: {}", server);
                server.setStatus(false);
                server.setLeader(false);
            }
        }
    }

    public Server self() {
        return MYSELF;
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public Server leader() {
        return this.serverList.stream().filter(Server::getStatus).filter(Server::getLeader).findAny().orElse(null);
    }
}
