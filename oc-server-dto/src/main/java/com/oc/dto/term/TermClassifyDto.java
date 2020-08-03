/**
 * 
 */
package com.oc.dto.term;

import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月13日
 * @version v 1.0
 */
public class TermClassifyDto {
	
	private int id;
	
	private String name;
	
	private int sortNum;
	
	private String staff;
	
	private String type;
	
	private List<TermWordDto> comwords;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<TermWordDto> getComwords() {
		return comwords;
	}

	public void setComwords(List<TermWordDto> comwords) {
		this.comwords = comwords;
	}
}
