/**
 * 
 */
package com.oc.core.coder;

import java.io.IOException;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
public interface JsonSupport {

	/**
	 * 读取字节流 stream to object
	 * @param is
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	<T> T read(ByteBufInputStream is, Class<T> clazz) throws IOException;

	/**
	 * 写入字节楼 object to stream
	 * @param out
	 * @param value
	 * @throws IOException
	 */
	void write(ByteBufOutputStream out, Object value) throws IOException;

	/**
	 * object to string
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	String writeString(Object obj) throws IOException;

	/**
	 * object to byte[]
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	byte[] writeBytes(Object obj) throws IOException;

	/**
	 * object to JsonNode
	 * @param o
	 * @return
	 * @throws IOException
	 */
	Object read(Object o) throws IOException;

	/**
	 * object to (T)object
	 * @param o
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	<T> T readClass(Object o, Class<T> clazz) throws IOException;
}
