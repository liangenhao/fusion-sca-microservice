package io.fusion.rpc.common.protocol.request;

import io.fusion.rpc.common.protocol.Command;
import io.fusion.rpc.common.protocol.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录请求
 *
 * @author enhao
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequestPacket extends Packet {

    private String userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}

