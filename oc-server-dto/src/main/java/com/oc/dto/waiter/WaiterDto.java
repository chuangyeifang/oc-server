/**
 * 
 */
package com.oc.dto.waiter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月15日
 * @version v 1.0
 */
@Getter
@Setter
@ToString
public class WaiterDto {

	private Long id;
	private String tenantCode;
	private String teamCode;
	private String teamName;
	private String waiterName;
	private String waiterCode;
	private String status;
	private String type;
	private String shunt;
	private String curReception;
	private String maxReception;
}
