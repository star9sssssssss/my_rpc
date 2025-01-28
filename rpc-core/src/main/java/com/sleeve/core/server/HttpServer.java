package com.sleeve.core.server;

/**
 * HTTP 服务接口
 */
public interface HttpServer {

    /**
     * 启动服务器
     * @param port 根据端口启动
     */
    void doStart(int port);
}
