package com.cxt.fly.codec;

import com.cxt.fly.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器
 * @author
 * @date   2018/3/29
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /* 解码器首先是获取对象的长度，是一个int类型，占四个字节*/
        if (in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if(in.readableBytes() < length){
            in.resetReaderIndex();
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        out.add(KryoUtil.readFromByteArray(bytes));
    }
}
