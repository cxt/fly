package com.cxt.fly.util;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * hessian 序列化工具
 * @author cxt
 * @date   2018/4/26
 */
public class HessianUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HessianUtil.class);

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        try {
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            return  outputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error(obj.toString() + "serialize error.",e);
        }
        finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error(obj.toString() + "serialize error.",e);
            }
        }
        return null;
    }

    /**
     * 反序列化
     * @param bytes
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] bytes,Class<T> cls){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(inputStream);
        T obj = null;
        try {
            obj = (T) hessian2Input.readObject(cls);
            return obj;
        } catch (IOException e) {
            LOGGER.error("deserialize error!",e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("deserialize error!",e);
            }
        }
        return null;
    }
}
