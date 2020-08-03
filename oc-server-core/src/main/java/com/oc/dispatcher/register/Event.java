package com.oc.dispatcher.register;

import com.oc.utils.ExternalizableUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月2日
 * @version v 1.0
 */
@Getter
@Setter
@ToString
public class Event implements Externalizable{
	
	private EventType type;
	private String uid;
	private String tenantCode;
	private Integer teamCode;
	private String waiterName;
	private String waiterCode;
	private String content;
	
	public Event() {}
	
	public Event(EventType type, String uid, String tenantCode, Integer teamCode) {
		this(type, uid, tenantCode, teamCode, null, null);
	}
	
	public Event(EventType type, String uid, String tenantCode,
				 Integer teamCode, String waiterName, String waiterCode) {
		this.type = type;
		this.uid = uid;
		this.tenantCode = tenantCode;
		this.teamCode = teamCode;
		this.waiterName = waiterName;
		this.waiterCode = waiterCode;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, uid);
		ExternalizableUtil.getInstance().writeSafeUTF(out, type.name());
		ExternalizableUtil.getInstance().writeSafeUTF(out, tenantCode);
		ExternalizableUtil.getInstance().writeInt(out, teamCode);
		ExternalizableUtil.getInstance().writeSafeUTF(out, waiterName);
		ExternalizableUtil.getInstance().writeSafeUTF(out, waiterCode);
		ExternalizableUtil.getInstance().writeSafeUTF(out, content);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		uid = ExternalizableUtil.getInstance().readSafeUTF(in);
		String typeName = ExternalizableUtil.getInstance().readSafeUTF(in);
		type = EventType.valueOf(typeName);
		tenantCode = ExternalizableUtil.getInstance().readSafeUTF(in);
		teamCode = ExternalizableUtil.getInstance().readInt(in);
		waiterName = ExternalizableUtil.getInstance().readSafeUTF(in);
		waiterCode = ExternalizableUtil.getInstance().readSafeUTF(in);
		content = ExternalizableUtil.getInstance().readSafeUTF(in);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != Event.class) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		Event that = (Event)obj;
		return this.uid.equals(that.getUid());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uid);
	}
}
