package com.cxt.fly.model;

/**
 *  rpc响应的封装
 * @author cxt
 * @date   2018/2/8
 */
public class RpcResponse {
    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException(){
        if (null != exception){
            return true;
        }
        return false;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
