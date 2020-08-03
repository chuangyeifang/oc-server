/**
 * 
 */
package com.oc.core.bs.message;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月29日
 * @version v 1.0
 */
public class PollErrorMessage implements Message{

	private final Map<String, Object> data;

	public PollErrorMessage() {
		this.data = new HashMap<>();
	}

    public PollErrorMessage(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }
    
    public void setAttr(String key, Object value) {
    	data.put(key, value);
    }
    
    public void removeAttr(String key) {
    	data.remove(key);
    }
}
