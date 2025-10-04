package com.lightning.rpc.core;

import java.io.Serializable;

/**
 * RPC响应对象
 * 封装了远程方法调用的返回结果
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public class RpcResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 请求ID，与RpcRequest中的requestId对应
     */
    private String requestId;
    
    /**
     * 方法调用结果
     */
    private Object result;
    
    /**
     * 异常信息
     */
    private Throwable exception;
    
    /**
     * 响应状态码
     */
    private int statusCode;
    
    /**
     * 响应消息
     */
    private String message;
    
    public RpcResponse() {}
    
    public RpcResponse(String requestId) {
        this.requestId = requestId;
    }
    
    public RpcResponse(String requestId, Object result) {
        this.requestId = requestId;
        this.result = result;
        this.statusCode = 200;
        this.message = "success";
    }
    
    public RpcResponse(String requestId, Throwable exception) {
        this.requestId = requestId;
        this.exception = exception;
        this.statusCode = 500;
        this.message = exception.getMessage();
    }
    
    /**
     * 判断调用是否成功
     */
    public boolean isSuccess() {
        return exception == null && statusCode == 200;
    }
    
    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public Throwable getException() {
        return exception;
    }
    
    public void setException(Throwable exception) {
        this.exception = exception;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", result=" + result +
                ", exception=" + exception +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}