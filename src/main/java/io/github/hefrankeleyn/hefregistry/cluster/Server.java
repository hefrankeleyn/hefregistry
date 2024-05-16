package io.github.hefrankeleyn.hefregistry.cluster;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * 集群的实例
 * @Date 2024/5/15
 * @Author lifei
 */
public class Server {
    private String url;
    private Boolean status;
    private Boolean leader;
    private Long version;

    public Server() {
    }

    public Server(String url, Boolean status, Boolean leader, Long version) {
        this.url = url;
        this.status = status;
        this.leader = leader;
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getLeader() {
        return leader;
    }

    public void setLeader(Boolean leader) {
        this.leader = leader;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server server)) return false;
        return Objects.equals(getUrl(), server.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUrl());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Server.class)
                .add("url", url)
                .add("status", status)
                .add("leader", leader)
                .add("version", version)
                .toString();
    }
}
