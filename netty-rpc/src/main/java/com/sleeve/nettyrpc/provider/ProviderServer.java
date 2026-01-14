package com.sleeve.nettyrpc.provider;

import com.sleeve.nettyrpc.message.RpcRequest;
import com.sleeve.nettyrpc.codec.RpcResponseEncoder;
import com.sleeve.nettyrpc.codec.BusinessDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ProviderServer {

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private final int port;

    public ProviderServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup(4);
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    .addLast(new BusinessDecoder())   // byteBuf => RpcRequest
                                    .addLast(new RpcResponseEncoder()) // RpcResponse => byteBuf
                                    .addLast(new SimpleChannelInboundHandler<RpcRequest>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
                                            System.out.println(request);
                                        }
                                    });
                        }
                    });
            serverBootstrap.bind(port).sync();
        } catch (Exception e) {
            throw new RuntimeException("服务启动失败", e);
        }
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }


    private static int add(int a, int b) {
        return a + b;
    }
}
