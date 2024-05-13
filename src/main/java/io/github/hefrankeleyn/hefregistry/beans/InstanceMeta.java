package io.github.hefrankeleyn.hefregistry.beans;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * @Date 2024/5/10
 * @Author lifei
 */
public class InstanceMeta {

    private String schema;
    private String host;
    private Integer port;
    private String context;
    private Boolean status;
    private Map<String, String> parameters = Maps.newHashMap();

    public InstanceMeta(){}

    public InstanceMeta(String schema, String host, Integer port, String context) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toUrl() {
        return Strings.lenientFormat("%s://%s:%s/%s", schema, host, port, context);
    }

    public String toPath() {
        return Strings.lenientFormat("%s_%s_%s", host, port, context);
    }

    public String toMeta() {
        return new Gson().toJson(parameters);
    }

    public static InstanceMeta fromUrl(String url) {
        URI uri = URI.create(url);
        return new InstanceMeta(uri.getScheme(),uri.getHost(), uri.getPort(),uri.getPath().substring(1));
    }

    public InstanceMeta addParameters(Map<String, String> params) {
        this.parameters.putAll(params);
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceMeta that)) return false;
        return Objects.equals(getSchema(), that.getSchema()) && Objects.equals(getHost(), that.getHost()) && Objects.equals(getPort(), that.getPort()) && Objects.equals(getContext(), that.getContext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSchema(), getHost(), getPort(), getContext());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(InstanceMeta.class)
                .add("schema", schema)
                .add("host", host)
                .add("port", port)
                .add("context", context)
                .add("status", status)
                .add("parameters", parameters)
                .toString();
    }
}
