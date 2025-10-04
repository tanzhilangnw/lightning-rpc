package com.lightning.rpc.core;

/**
 * RPC异常类
 * 封装了RPC调用过程中的各种异常
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public class RpcException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private int code;
    
    public RpcException() {
        super();
    }
    
    public RpcException(String message) {
        super(message);
    }
    
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RpcException(Throwable cause) {
        super(cause);
    }
    
    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public RpcException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    /**
     * 常见错误码
     */
    public static class ErrorCode {
        public static final int UNKNOWN = -1;
        public static final int NETWORK_ERROR = 1001;
        public static final int TIMEOUT = 1002;
        public static final int SERIALIZATION_ERROR = 1003;
        public static final int SERVICE_NOT_FOUND = 1004;
        public static final int METHOD_NOT_FOUND = 1005;
        public static final int REGISTRY_ERROR = 1006;
        public static final int LOAD_BALANCE_ERROR = 1007;
    }
}