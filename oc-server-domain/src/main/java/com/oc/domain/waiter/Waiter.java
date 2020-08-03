package com.oc.domain.waiter;

import com.oc.utils.ExternalizableUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;


/**
 * @author chuangyeifang
 */
@Getter
@Setter
public class Waiter implements Externalizable{
	
    private Long id;
    private String tenantCode;
    private Integer teamCode;
    private String waiterName;
    private String waiterCode;
    private String status;
    private String type;
    private String shunt;
    private Integer curReception;
    private Integer maxReception;
    private String autoReply;
    private String replyMsg;
    private String realName;
    private String mobile;
    private Long sysBusyTimestamp;

    @Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeLong(out, id == null ? 0 : id);
		ExternalizableUtil.getInstance().writeSafeUTF(out, tenantCode);
		ExternalizableUtil.getInstance().writeInt(out, teamCode);
		ExternalizableUtil.getInstance().writeSafeUTF(out, waiterName);
		ExternalizableUtil.getInstance().writeSafeUTF(out, waiterCode);
		ExternalizableUtil.getInstance().writeSafeUTF(out, status);
		ExternalizableUtil.getInstance().writeSafeUTF(out, type);
		ExternalizableUtil.getInstance().writeSafeUTF(out, shunt);
		ExternalizableUtil.getInstance().writeInt(out, curReception == null ? 0 : curReception);
		ExternalizableUtil.getInstance().writeInt(out, maxReception == null ? 0 : maxReception);
		ExternalizableUtil.getInstance().writeSafeUTF(out, autoReply);
		ExternalizableUtil.getInstance().writeSafeUTF(out, replyMsg);
        ExternalizableUtil.getInstance().writeLong(out, sysBusyTimestamp == null ? 0 : sysBusyTimestamp);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		id = ExternalizableUtil.getInstance().readLong(in);
		tenantCode = ExternalizableUtil.getInstance().readSafeUTF(in);
		teamCode = ExternalizableUtil.getInstance().readInt(in);
		waiterName = ExternalizableUtil.getInstance().readSafeUTF(in);
		waiterCode = ExternalizableUtil.getInstance().readSafeUTF(in);
		status = ExternalizableUtil.getInstance().readSafeUTF(in);
		type = ExternalizableUtil.getInstance().readSafeUTF(in);
		shunt = ExternalizableUtil.getInstance().readSafeUTF(in);
		curReception = ExternalizableUtil.getInstance().readInt(in);
		maxReception = ExternalizableUtil.getInstance().readInt(in);
		autoReply = ExternalizableUtil.getInstance().readSafeUTF(in);
		replyMsg = ExternalizableUtil.getInstance().readSafeUTF(in);
        sysBusyTimestamp = ExternalizableUtil.getInstance().readLong(in);
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

		if (obj == null) {
			return false;
		}
		
		if (obj instanceof Waiter) {
			Waiter compareWaiter = (Waiter)obj;
			if (!waiterName.equals(compareWaiter.getWaiterName())) {
			    return false;
            }
            return tenantCode.equals(compareWaiter.getTenantCode());
		} else {
			return false;
		}
    }
	
	@Override
	public int hashCode() {
        return Objects.hash(waiterName, waiterCode);
	}
}