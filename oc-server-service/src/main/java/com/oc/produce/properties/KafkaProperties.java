
package com.oc.produce.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月8日
 * @version v 1.0
 */

@ConfigurationProperties("custom.kafka")
public class KafkaProperties {
	
	private Boolean open;
	
	private String brokerList;
	
	private String serializerClass;
	
	private String requestRequiredAcks;

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public String getBrokerList() {
		return brokerList;
	}

	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}

	public String getSerializerClass() {
		return serializerClass;
	}

	public void setSerializerClass(String serializerClass) {
		this.serializerClass = serializerClass;
	}

	public String getRequestRequiredAcks() {
		return requestRequiredAcks;
	}

	public void setRequestRequiredAcks(String requestRequiredAcks) {
		this.requestRequiredAcks = requestRequiredAcks;
	}

	@Override
	public String toString() {
		return "KafkaProperties [open=" + open + ", brokerList=" + brokerList + ", serializerClass=" + serializerClass
				+ ", requestRequiredAcks=" + requestRequiredAcks + "]";
	}
}
