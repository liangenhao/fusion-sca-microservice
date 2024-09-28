package io.fusion.rpc.common.protocol.response;

import io.fusion.rpc.common.protocol.Packet;
import lombok.Data;

import static io.fusion.rpc.common.protocol.Command.LOGIN_RESPONSE;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;

    private String reason;


    @Override
    public Byte getCommand() {
        return LOGIN_RESPONSE;
    }
}
