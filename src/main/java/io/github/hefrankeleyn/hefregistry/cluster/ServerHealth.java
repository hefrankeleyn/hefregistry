package io.github.hefrankeleyn.hefregistry.cluster;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import io.github.hefrankeleyn.hefregistry.beans.Snapshot;
import io.github.hefrankeleyn.hefregistry.http.HttpInvoker;
import io.github.hefrankeleyn.hefregistry.service.impl.HefRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/5/21
 * @Author lifei
 */
public class ServerHealth {

    private static final Logger log = LoggerFactory.getLogger(ServerHealth.class);

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static final int delay = 5_000;

    private final Cluster cluster;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    public void checkServerHealth() {
        // 探活
        executor.scheduleWithFixedDelay(() -> {
            try {
                // 更新状态
                updateServers();
                // 选择主节点
                selectLeader();
                // 从主节点同步数据
                syncSnapshotFromLeader();
            } catch (Exception e) {
                log.debug(Throwables.getStackTraceAsString(e));
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
    }

    public void selectLeader() {
        new Election().selectLeader(cluster.getServerList());
    }

    private void updateServers() {
        cluster.getServerList().parallelStream().forEach(server -> {
            try {
                if (server.equals(cluster.self())) {
                    return;
                }
                Server info = HttpInvoker.httpGet(Strings.lenientFormat("%s/info", server.getUrl()), Server.class);
                if (Objects.nonNull(info)) {
                    log.debug(" ==> url: {}, health check success for: {}", server.getUrl(), info);
                    server.setStatus(true);
                    server.setLeader(info.getLeader());
                    server.setVersion(info.getVersion());
                } else {
                    server.setStatus(false);
                    server.setLeader(false);
                }
            } catch (Exception e) {
                log.debug(" ==> health check error for: {}, error eason: {}", server, e.getMessage());
                server.setStatus(false);
                server.setLeader(false);
            }
        });
    }



    private void syncSnapshotFromLeader() {
        if (Objects.nonNull(cluster.leader()) && !cluster.self().getLeader() && cluster.self().getVersion() < cluster.leader().getVersion()) {
            // 拿到主节点当前所有的数据
            log.debug("===> syncSnapshotFromLeader: {}", cluster.leader());
            log.debug("===> leader version: {}, myself version: {}", cluster.leader().getVersion(), cluster.self().getVersion());
            Snapshot snapshot = HttpInvoker.httpGet(Strings.lenientFormat("%s/snapshot", cluster.leader().getUrl()), Snapshot.class);
            long snapshotVersion = HefRegistryService.restore(snapshot);
            log.debug("===> snapshotVersion: {}", snapshotVersion);
        }
    }
}
