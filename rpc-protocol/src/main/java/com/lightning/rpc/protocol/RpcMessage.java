package com.lightning.rpc.protocol;

import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.RpcResponse;


public class RpcMessage {
    
    
    private RpcHeader header;
    
    
    private Object body;
    
    public RpcMessage() {}
    
    public RpcMessage(RpcHeader header, Object body) {
        this.header = header;
        this.body = body;
    }
    
    
    public static RpcMessage createRequest(RpcRequest request) {
        RpcHeader header = new RpcHeader();
        header.setMessageType(MessageType.REQUEST);
        header.setRequestId(request.getRequestId());
        return new RpcMessage(header, request);
    }
    
    
    public static RpcMessage createResponse(RpcResponse response) {
        RpcHeader header = new RpcHeader();
        header.setMessageType(MessageType.RESPONSE);
        header.setRequestId(response.getRequestId());
        return new RpcMessage(header, response);
    }
    
    
    public static RpcMessage createHeartbeat() {
        RpcHeader header = new RpcHeader();
        header.setMessageType(MessageType.HEARTBEAT);
        return new RpcMessage(header, null);
    }
    
    // Getters and Setters
    public RpcHeader getHeader() {
        return header;
    }
    
    public void setHeader(RpcHeader header) {
        this.header = header;
    }
    
    public Object getBody() {
        return body;
    }
    
    public void setBody(Object body) {
        this.body = body;
    }
    
    @Override
    public String toString() {
        return "RpcMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
