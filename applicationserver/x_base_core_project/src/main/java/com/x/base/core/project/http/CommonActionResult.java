package com.x.base.core.project.http;

import java.util.Date;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult.Type;

public class CommonActionResult extends GsonPropertyObject {

	private Type type;

	private Object data;

	private Date date;

	private Long spent;

	private Long size;

	private Long count;

	private Long position;

	private String message;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getSpent() {
		return spent;
	}

	public void setSpent(Long spent) {
		this.spent = spent;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
