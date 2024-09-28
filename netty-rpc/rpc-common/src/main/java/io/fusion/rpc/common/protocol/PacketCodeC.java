package io.fusion.rpc.common.protocol;

import io.fusion.rpc.common.protocol.request.LoginRequestPacket;
import io.fusion.rpc.common.serialize.Serializer;
import io.fusion.rpc.common.serialize.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

import static io.fusion.rpc.common.protocol.Command.LOGIN_REQUEST;

/**
 * 编解码器
 *
 * @author enhao
 */
public class PacketCodeC {
    public static final PacketCodeC INSTANCE = new PacketCodeC();

    private static final int MAGIC_NUMBER = 0x12345678;
    private static final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private static final Map<Byte, Serializer> serializerMap;

    static {
        // todo 通用化处理
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(LOGIN_REQUEST, LoginRequestPacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlgorithm(), serializer);
    }

    /**
     * {@link Packet} 编码为二进制 RPC 协议
     *
     * @param alloc
     * @param packet {@link Packet}
     * @return {@link ByteBuf}
     */
    public ByteBuf encode(ByteBufAllocator alloc, Packet packet) {
        // 1. 创建 ByteBuf 对象
        ByteBuf byteBuf = alloc.ioBuffer();
        // 2. 序列化 java 对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 3. 实际编码过程
        // 魔数(4bytes)版本号(1bytes)序列化算法(1bytes)指令(1bytes)数据长度(4bytes)数据(N bytes)
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    /**
     * 将二进制 RPC 协议解码为 {@link Packet}
     *
     * @param byteBuf {@link ByteBuf}
     * @return {@link Packet}
     */
    public Packet decode(ByteBuf byteBuf) {
        // 校验 magic number
        int magicNumber = byteBuf.readInt();
        if (MAGIC_NUMBER != magicNumber) {
            return null;
        }

        // 跳过版本号
        byteBuf.skipBytes(1);

        // 序列化算法
        byte serializeAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();

        // 数据包
        byte[] dataBytes = new byte[length];
        byteBuf.readBytes(dataBytes);

        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, dataBytes);
        }

        return null;
    }

    private Serializer getSerializer(byte serializeAlgorithm) {
        return serializerMap.get(serializeAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte command) {
        return packetTypeMap.get(command);
    }
}
