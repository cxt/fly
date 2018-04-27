package com.cxt.fly.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * rpc请求封装
 *
 * @author cxt
 * @date 2018/2/8
 */
public class RpcRequest implements Serializable {
    private String requestId;
    private String serviceName;
    private String version;
    private String method;
    private Class<?>[] paramTypes;
    private Object[] params;

    public static RpcRequest build(String serviceName,
                                   String version,
                                   String method,
                                   Class<?>[] paramTypes,
                                   Object[] params) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethod(method);
        request.setParams(params);
        request.setParamTypes(paramTypes);
        request.setServiceName(serviceName);
        request.setVersion(version);
        return request;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RpcRequest{");
        sb.append("requestId='").append(requestId).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", method='").append(method).append('\'');
        sb.append(", paramTypes=").append(Arrays.toString(paramTypes));
        sb.append(", params=").append(Arrays.toString(params));
        sb.append('}');
        return sb.toString();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

}
