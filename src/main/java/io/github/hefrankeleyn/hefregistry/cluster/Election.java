package io.github.hefrankeleyn.hefregistry.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/5/21
 * @Author lifei
 */
public class Election {

    private static final Logger log = LoggerFactory.getLogger(Election.class);

    public void selectLeader(List<Server> serverList) {
        List<Server> leaderList = serverList.stream().filter(Server::getStatus).filter(Server::getLeader).toList();
        if (leaderList.isEmpty()) {
            log.warn("===> select for no leader found: {}", serverList);
            select(serverList);
        } else if (leaderList.size() > 1) {
            log.warn("===> select for more than one leader: {}", serverList);
            select(serverList);
        } else {
            leaderList.get(0).setLeader(true);
            log.debug("===> already has a leader: {}", leaderList.get(0));
        }
    }

    private void select(List<Server> serverList) {
        Server candidate = null;
        log.debug("====> select: {}", serverList);
        for (Server server : serverList) {
            server.setLeader(false);
            if (!server.getStatus()) {
                continue;
            }
            if (Objects.isNull(candidate)) {
                candidate = server;
            } else if (server.hashCode() < candidate.hashCode()) {
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
}
