package io.fusion.rpc.client;

import io.fusion.rpc.client.handler.FirstServerHandler;
import io.fusion.rpc.client.handler.ServerHandler;
import io.fusion.rpc.common.AttributeConst;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer {

    private static final int PORT = 8000;

    public static void main(String[] args) {
        // bossGroup 表示监听端口，接收新连接的线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // workerGroup 表示处理每一个连接的数据读写的线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        // 引导服务端的启动工作
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                // 指定线程模型
                .group(bossGroup, workerGroup)
                // 指定 IO 模型
                .channel(NioServerSocketChannel.class)
                // 为 NioServerSocketChannel 指定自定义属性，可通过 channel.attr() 取出属性
                .attr(AttributeKey.newInstance(AttributeConst.SERVER_NAME_KEY), "rpc-server")
                // 为每一个连接都指定自定义属性
                .childAttr(AttributeKey.newInstance("client-key"), "client-value")
                // 为 NioServerSocketChannel 指定 TCP 参数
                // SO_BACKLOG: 用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，服务器处理创建新连接较慢，则可以适当调大这个参数。
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 为每一个连接都指定 TCP 参数
                // SO_KEEPALIVE: 是否开启TCP底层心跳机制，true表示开启
                // TCP_NODELAY: 是否开启Nagle算法，true表示关闭，false表示开启。通俗地说，如果要求高实时性，有数据发送时就马上发送，就设置为关闭；如果需要减少发送次数，减少网络交互，就设置为开启。
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 指定处理新连接数据的读写处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        // ch.pipeline().addLast(new FirstServerHandler());
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });

        bind(serverBootstrap, PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Netty Server Bind Port [{}] Succeed", port);
            } else {
                log.error("Netty Server Bind Port [{}] Failed, Caused by: {}", port, future.cause().getMessage());
                bind(serverBootstrap, port + 1);
            }
        });
    }

}