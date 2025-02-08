package com.sleeve.core.server.tcp;

import com.sleeve.core.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;


/**
 * 装饰者模式（使用 recordParser 对原有的 buffer 处理能力进行增强）
 * 该类需要继承装饰的类的顶级父类或接口
 */
@Slf4j
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    // 对RecordParser的功能进行增强，构造器需要传入给类的父类或接口
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }

    // 初始化 Parser
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 当前协议的头部大小
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            // 初始化
            int size = -1;
            // 一次完整的读取（头 + 体）
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                log.info("开始传输数据");
                if (-1 == size) {
                    // 读取消息体长度，即 bodyLength
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // 写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    // 已拼接为完整 Buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }

    @Override
    public void handle(Buffer buffer) {
        // 使用装饰后的方法进行处理
        recordParser.handle(buffer);
    }
}
