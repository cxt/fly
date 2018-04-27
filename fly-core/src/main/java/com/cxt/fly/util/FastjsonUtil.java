package com.cxt.fly.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * fastjson 序列化工具
 * @author cxt
 * @date   2018/4/26
 */
public class FastjsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastjsonUtil.class);

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj) throws UnsupportedEncodingException {
        /* 加上SerializerFeature.WriteClassName 可以记录对象的类型 */
        String jsonString = JSON.toJSONString(obj,SerializerFeature.WriteClassName);
        return jsonString.getBytes("UTF-8");
    }

    /**
     * 反序列化
     * @param bytes
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] bytes,Class<T> cls) throws UnsupportedEncodingException {
        String jsonStr = new String(bytes,"UTF-8");
        T t = JSON.parseObject(jsonStr, cls);
        return t;
    }
}
