package com.sleeve.core.server.tcp;

import com.sleeve.core.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

import java.util.Arrays;

/**
 * 使用tcp用于服务器传输
 */
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        System.out.println("本次获得的数据为: " + Buffer.buffer(requestData));
        // 编写处理请求的逻辑，根据requestData构造响应数据并返回
        return "Hello, client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();

        // 创建TCP服务器
        NetServer netServer = vertx.createNetServer();

        // 处理连接的请求
        netServer.connectHandler(socket -> {
            // 处理连接
            socket.handler(buffer -> {
                // 处理接收到的字节数组
                byte[] requestData = buffer.getBytes();
                // 自定义字节数组的处理逻辑，比如解析请求，调用服务，构造响应等
                byte[] responseData = handleRequest(requestData);
                // 发送响应
                socket.write(Buffer.buffer(responseData));
            });
        });

        // 启动TCP服务器并监听端口
        netServer.listen(port, result -> {
           if (result.succeeded()) {
               System.out.println("TCP server started on port: " + port);
           } else {
               System.err.println("Failed to start TCP server: " + result.cause());
           }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}














