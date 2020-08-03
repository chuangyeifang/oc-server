package com.oc.cluster.collection;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年6月25日
 * @version v 1.0
 */
public class RemoteTaskResult {

	/**
	 * code 默认100 成功
	 * 100 成功 101 无法路由当前节点 102集群不可用 103执行超时 104 任务执行被打断 105任务执行异常 106 未知错误
	 */
	private int code;
	
	private String msg;
	
	private Object result;
	
	/**
	 * @param code 状态：100 成功 101 无法路由当前节点 102集群不可用 103执行超时 104 任务执行被打断 105任务执行异常 106 未知错误
	 * @param msg 提示信息
	 */
	public RemoteTaskResult(int code, String msg) {
		this(code, msg, null);
	}
	
	/**
	 * code 100 成功 101 无法路由当前节点 102集群不可用 103执行超时 104 任务执行被打断 105任务执行异常 106 未知错误
	 * @param code 状态
	 * @param msg 提示信息
	 * @param result 远程调用执行结果
	 */
	public RemoteTaskResult(int code, String msg, Object result) {
		this.code = code;
		this.msg = msg;
		this.result = result;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
