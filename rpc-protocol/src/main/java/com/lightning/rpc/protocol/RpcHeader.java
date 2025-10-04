package com.lightning.rpc.protocol;

import java.io.Serializable;


public class RpcHeader implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    
    private short magic = 0x1234;
    
    
    private byte version = 1;
    
    
    private MessageType messageType;
    
    
    private byte serializerType;
    
    
    private byte compressType;
    
    
    private String requestId;
    
    
    private int bodyLength;
    
    public RpcHeader() {}
    
    public RpcHeader(MessageType messageType, String requestId) {
        this.messageType = messageType;
        this.requestId = requestId;
    }
    
    // Getters and Setters
    public short getMagic() {
        return magic;
    }
    
    public void setMagic(short magic) {
        this.magic = magic;
    }
    
    public byte getVersion() {
        return version;
    }
    
    public void setVersion(byte version) {
        this.version = version;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    
    public byte getSerializerType() {
        return serializerType;
    }
    
    public void setSerializerType(byte serializerType) {
        this.serializerType = serializerType;
    }
    
    public byte getCompressType() {
        return compressType;
    }
    
    public void setCompressType(byte compressType) {
        this.compressType = compressType;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public int getBodyLength() {
        return bodyLength;
    }
    
    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }
    
    @Override
    public String toString() {
        return "RpcHeader{" +
                "magic=" + magic +
                ", version=" + version +
                ", messageType=" + messageType +
                ", serializerType=" + serializerType +
                ", compressType=" + compressType +
                ", requestId='" + requestId + '\'' +
                ", bodyLength=" + bodyLength +
                '}';
    }
}
