package com.cxt.fly.codec;

import com.cxt.fly.util.KryoUtil;
import com.cxt.fly.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器 by ProtostuffUtil
 *
 * @author cxt
 * @date 2018/3/29
 */
@SuppressWarnings("ALL")
public class RpcProtostuffDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcProtostuffDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /* 解码器首先是获取对象的长度，是一个int类型，占四个字节*/
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        /*如果获取对象的字节数组的长度小于之前算出的长度*/
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        /*选择序列化工具*/
        out.add(ProtostuffUtil.deserialize(bytes,genericClass));
    }
}
