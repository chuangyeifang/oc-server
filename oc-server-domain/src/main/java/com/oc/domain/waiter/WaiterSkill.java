package com.oc.domain.waiter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.oc.utils.ExternalizableUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
public class WaiterSkill implements Externalizable{
	
    private Long id;
    private String waiterName;
    private Integer skillCode;
    private String createTime;

	/**
	 * @param out
	 * @throws IOException
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, waiterName);
		ExternalizableUtil.getInstance().writeInt(out, skillCode);
	}

	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		waiterName = ExternalizableUtil.getInstance().readSafeUTF(in);
		skillCode = ExternalizableUtil.getInstance().readInt(in);
	}
}