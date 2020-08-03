package com.oc.dispatcher.cache.po;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.oc.utils.ExternalizableUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 客服服务状态
 * @author chuangyeifang
 * @createDate 2020年1月1日
 * @version v 1.0
 */
@Getter
@Setter
@ToString
public class ConciseWaiter implements Externalizable{
	
	private String waiterCode;
	private Integer teamCode;
	private Long score;

	public ConciseWaiter() {}

	public ConciseWaiter(String waiterCode, Integer teamCode) {
		this.waiterCode = waiterCode;
		this.teamCode = teamCode;
		this.score = System.currentTimeMillis();
	}
	
	public ConciseWaiter(String waiterCode, Integer teamCode, Long score) {
		this.waiterCode = waiterCode;
		this.teamCode = teamCode;
		this.score = score;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, waiterCode);
		ExternalizableUtil.getInstance().writeInt(out, teamCode);
		ExternalizableUtil.getInstance().writeLong(out, score);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		waiterCode = ExternalizableUtil.getInstance().readSafeUTF(in);
		teamCode = ExternalizableUtil.getInstance().readInt(in);
		score = ExternalizableUtil.getInstance().readLong(in);
	}
}
