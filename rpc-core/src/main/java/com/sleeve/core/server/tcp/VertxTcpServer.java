package com.sleeve.core.server.tcp;

import com.sleeve.core.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 使用tcp用于服务器传输
 */
@Slf4j
public class VertxTcpServer implements HttpServer {


    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();

        // 创建TCP服务器
        NetServer netServer = vertx.createNetServer();

        // 处理连接的请求
        netServer.connectHandler(new TcpServerHandler());

        // 启动TCP服务器并监听端口
        netServer.listen(port, result -> {
           if (result.succeeded()) {
               System.out.println("TCP server started on port " + port);
           } else {
               System.err.println("Failed to start TCP server: " + result.cause().getMessage());
           }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}














