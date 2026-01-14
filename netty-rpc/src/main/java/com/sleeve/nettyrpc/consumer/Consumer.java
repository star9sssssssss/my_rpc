package com.sleeve.nettyrpc.consumer;

import com.sleeve.nettyrpc.message.RpcRequest;
import com.sleeve.nettyrpc.codec.RpcRequestEncoder;
import com.sleeve.nettyrpc.message.RpcResponse;
import com.sleeve.nettyrpc.codec.BusinessDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;

public class Consumer {

    public int add(int a,int b) throws Exception {
        CompletableFuture<Integer> addResultFuture = new CompletableFuture<>();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(4))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new BusinessDecoder()) // ByteBuf => RpcResponse
                                .addLast(new RpcRequestEncoder()) // rpcRequest => ByteBuf
                                .addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
                                        System.out.println(response);
                                        addResultFuture.complete(1);
                                    }
                                });
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect("localhost", 8888).sync();
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName("testMethod");
        rpcRequest.setServiceName("testService");
        rpcRequest.setParameterTypes(new Class[]{int.class, int.class});
        rpcRequest.setArgs(new Object[]{1, 2});
        channelFuture.channel().writeAndFlush(rpcRequest);
        return addResultFuture.get();
    }
}
