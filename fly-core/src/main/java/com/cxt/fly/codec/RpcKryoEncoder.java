package com.cxt.fly.codec;

import com.cxt.fly.common.Constants;
import com.cxt.fly.util.KryoUtil;
import com.cxt.fly.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器 by KryoUtil
 *
 * @author cxt
 * @date 2018/3/29
 */
public class RpcKryoEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public RpcKryoEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            byte[] bytes = null;

            /*选择序列化工具*/
            bytes = KryoUtil.writeToByteArray(msg);
            /* 编码器先写对象二进制的长度，方便解码器解码知道，要解码的对象到底多长*/
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
