/**
 * 
 */
package com.oc.cluster.collection.cache.local;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.oc.exception.CannotCalculateSizeException;
import com.oc.cluster.collection.cache.Cacheable;

/**
 * @Description: 计算缓存大小（大约占用内存空间）单位为字节
 * @author chuangyeifang
 * @createDate 2020年7月23日
 * @version v 1.0
 */
public class CacheSizes {

	public static int sizeOfObject() {
		return 4;
	}
	
	public static int sizeOfString(String string) {
		if (null == string) {
			return 0;
		}
		return 4 + string.getBytes().length;
	}
	
	public static int sizeOfInt() {
		return 4;
	}
	
	public static int sizeOfChar() {
		return 2;
	}
	
	public static int sizeOfBoolean() {
		return 1;
	}
	
	public static int sizeOfLong() {
		return 8;
	}
	
	public static int sizeOfDouble() {
		return 8;
	}
	
	public static int sizeOfDate() {
		return 12;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int sizeOfMap(Map<?, ?> map) throws CannotCalculateSizeException{
		if (null == map) {
			return 0;
		}
		int size = 36;
		
		Set<? extends Map.Entry> set = map.entrySet();
		for (Map.Entry<Object, Object> entry : set) {
			size += sizeOfAnything(entry.getKey());
			size += sizeOfAnything(entry.getValue());
		}
		return size;
	}
	
	@SuppressWarnings("rawtypes")
	public static int sizeOfCollection(Collection list) 
            throws CannotCalculateSizeException {
        if (list == null) {
            return 0;
        }
        int size = 36;
        Object[] values = list.toArray();
        for (int i = 0; i < values.length; i++) {
            size += sizeOfAnything(values[i]);
        }
        return size;
    }
	
	@SuppressWarnings({ "rawtypes", "resource" })
	public static int sizeOfAnything(Object object) throws CannotCalculateSizeException{
		if (null == object) {
			return 0;
		}
		if (object instanceof Cacheable) {
			return ((Cacheable)object).getCacheSize();
		} 
		else if (object instanceof String) {
			return sizeOfString((String)object);
		}
		else if (object instanceof Long) {
			return sizeOfLong();
		}
		else if (object instanceof Integer) {
			return sizeOfInt();
		}
		else if (object instanceof Boolean) {
			return sizeOfBoolean();
		}
		else if (object instanceof Double) {
			return sizeOfDouble();
		}
		else if (object instanceof byte[]) {
			byte [] array = (byte[])object;
            return sizeOfObject() + array.length;
		}
		else if (object instanceof Collection) {
            return sizeOfCollection((Collection)object);
        }
		else if (object instanceof Map) {
            return sizeOfMap((Map)object);
        }
		else {
			int size = 1;
		    try {
			    CacheSizes.NullOutputStream out = new NullOutputStream();
			    ObjectOutputStream outObj = new ObjectOutputStream(out);
			    outObj.writeObject(object);
			    size = out.size();
		    } 
		    catch (IOException ioe) {
                throw new CannotCalculateSizeException(object);
            }
            return size;
		}
	}
	
	private static class NullOutputStream extends OutputStream {

        int size = 0;

        @Override
        public void write(int b) throws IOException {
            size++;
        }

        @Override
        public void write(byte[] b) throws IOException {
            size += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) {
            size += len;
        }

        public int size() {
            return size;
        }
    }
}
