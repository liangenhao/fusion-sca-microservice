package io.fusion.rpc.common.protocol;

import lombok.Data;

/**
 * @author enhao
 */
@Data
public abstract class Packet {

    /**
     * 协议版本
     */
    private Byte version = 1;


    public abstract Byte getCommand();
}
