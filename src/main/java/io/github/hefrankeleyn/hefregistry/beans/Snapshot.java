package io.github.hefrankeleyn.hefregistry.beans;

import com.google.common.base.MoreObjects;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

/**
 * @Date 2024/5/21
 * @Author lifei
 */
public class Snapshot {

    private LinkedMultiValueMap<String, InstanceMeta> registry;
    private Map<String, Long> versions;
    public Map<String, Long> timestemps;
    private long version;

    public Snapshot() {
    }

    public Snapshot(LinkedMultiValueMap<String, InstanceMeta> registry, Map<String, Long> versions, Map<String, Long> timestemps, long version) {
        this.registry = registry;
        this.versions = versions;
        this.timestemps = timestemps;
        this.version = version;
    }

    public LinkedMultiValueMap<String, InstanceMeta> getRegistry() {
        return registry;
    }

    public void setRegistry(LinkedMultiValueMap<String, InstanceMeta> registry) {
        this.registry = registry;
    }

    public Map<String, Long> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, Long> versions) {
        this.versions = versions;
    }

    public Map<String, Long> getTimestemps() {
        return timestemps;
    }

    public void setTimestemps(Map<String, Long> timestemps) {
        this.timestemps = timestemps;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Snapshot.class)
                .add("registry", registry)
                .add("versions", versions)
                .add("timestemps", timestemps)
                .add("version", version)
                .toString();
    }
}
