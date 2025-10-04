package com.lightning.rpc.protocol;


public enum MessageType {
    
    
    REQUEST((byte) 1),
    
    
    RESPONSE((byte) 2),
    
    
    HEARTBEAT((byte) 3);
    
    private final byte value;
    
    MessageType(byte value) {
        this.value = value;
    }
    
    public byte getValue() {
        return value;
    }
    
    
    public static MessageType fromValue(byte value) {
        for (MessageType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + value);
    }
}
