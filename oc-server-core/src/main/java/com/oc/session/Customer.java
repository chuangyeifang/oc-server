package com.oc.session;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import com.oc.utils.ExternalizableUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月18日
 * @version v 1.0
 */
@Getter
@Setter
@ToString
public class Customer implements Externalizable{
	
	private static final long serialVersionUID = 1L;
	private String uid;
	private String name;
	private boolean login;
	private String tenantCode;
	private Integer teamCode;
	private Integer skillCode;
	private String skillName;
	private String goodsCode;
	private String device;
	private Integer wait;
	private long time;

	public Customer() {}
	
	public Customer(String uid, String name, boolean login,
			String tenantCode, Integer teamCode, Integer skillCode, String skillName,
			String goodsCode, String device) {
		this.uid = uid;
		this.name = name;
		this.login = login;
		this.tenantCode = tenantCode;
		this.teamCode = teamCode;
		this.skillCode = skillCode;
		this.skillName = skillName;
		this.goodsCode = goodsCode;
		this.device = device;
		this.time = System.currentTimeMillis();
		this.wait = 0;
	}

	@Override
	@SuppressWarnings("DuplicatedCode")
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, uid);
		ExternalizableUtil.getInstance().writeSafeUTF(out, name);
		ExternalizableUtil.getInstance().writeBoolean(out, login);
		ExternalizableUtil.getInstance().writeSafeUTF(out, tenantCode);
		ExternalizableUtil.getInstance().writeInt(out, teamCode);
		ExternalizableUtil.getInstance().writeInt(out, skillCode);
		ExternalizableUtil.getInstance().writeSafeUTF(out, skillName);
		ExternalizableUtil.getInstance().writeSafeUTF(out, goodsCode);
		ExternalizableUtil.getInstance().writeSafeUTF(out, device);
		ExternalizableUtil.getInstance().writeInt(out, wait);
		ExternalizableUtil.getInstance().writeLong(out, time);
	}

	@Override
	@SuppressWarnings("DuplicatedCode")
	public void readExternal(ObjectInput in) throws IOException {
		uid = ExternalizableUtil.getInstance().readSafeUTF(in);
		name = ExternalizableUtil.getInstance().readSafeUTF(in);
		login = ExternalizableUtil.getInstance().readBoolean(in);
		tenantCode = ExternalizableUtil.getInstance().readSafeUTF(in);
		teamCode = ExternalizableUtil.getInstance().readInt(in);
		skillCode = ExternalizableUtil.getInstance().readInt(in);
		skillName = ExternalizableUtil.getInstance().readSafeUTF(in);
		goodsCode = ExternalizableUtil.getInstance().readSafeUTF(in);
		device = ExternalizableUtil.getInstance().readSafeUTF(in);
		wait = ExternalizableUtil.getInstance().readInt(in);
		time = ExternalizableUtil.getInstance().readLong(in);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (obj instanceof Customer) {
			Customer that = (Customer)obj;
			return that.getUid() != null && that.getUid().equals(this.uid);
		} 
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uid);
	}
}
