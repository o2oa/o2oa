package com.o2platform.website.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class WebSiteFeedbackContent implements Serializable {

	private static final long serialVersionUID = 1L;

	public WebSiteFeedbackContent() {
		Date date = new Date();
		if ( null == this.createTime ) {
			this.createTime = date;
		}
	}
	public static String createId() {
		return UUID.randomUUID().toString();
	}
	
	private String id = createId();
	private Date createTime;
	private String content = "";

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}