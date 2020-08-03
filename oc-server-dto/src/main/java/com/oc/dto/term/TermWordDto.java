/**
 * 
 */
package com.oc.dto.term;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月13日
 * @version v 1.0
 */
public class TermWordDto {
	
	private int id;
	
	private int groupId;
	
	private String staff;
	
	private String keyword;
	
	private String content;
	
	private int sortNum;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
}
