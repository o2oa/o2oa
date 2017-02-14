package com.x.base.core.http;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.exception.JaxrsBusinessLogicException;
import com.x.base.core.gson.XGsonBuilder;

public class ActionResult<T> implements Serializable {

	private static final long serialVersionUID = 1484547449073789817L;

	private Type type = Type.success;

	public enum Type {
		success, warn, error
	}

	private T data;

	private Date date = new Date();

	private Long spent = -1L;

	private Long size = -1L;

	private Long count = 0L;

	private Long position = 0L;

	private String message = "";
	
	private String userMessage = "";

	public void setData(T data) {
		this.data = data;
		if (data != null) {
			if (Collection.class.isInstance(data)) {
				this.size = (long) ((Collection<?>) data).size();
			} else {
				this.size = -1L;
			}
		}
	}

	private void append(String str) {
		String o = StringUtils.isBlank(str) ? " " : str;
		if (StringUtils.isBlank(this.message)) {
			this.message = o;
		} else {
			message += " , " + str;
		}
	}

	public T getData() {
		return data;
	}

	public Type getType() {
		return type;
	}

	public String toJson() {
		this.spent = (new Date()).getTime() - date.getTime();
		String str = XGsonBuilder.instance().toJson(this);
		return str;
	}

	public void error(JaxrsBusinessLogicException e) {
		this.type = Type.error;
		this.append(e.getMessage());
	}

	public void error(Exception e) {
		this.type = Type.error;
		this.append(e.toString());
	}

	public void error(Throwable th) {
		this.type = Type.error;
		this.append(th.toString());
	}

	public void warn(String message) {
		if (this.type.equals(Type.success)) {
			this.type = Type.warn;
		}
		this.append(message);
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public Long getSize() {
		return size;
	}

	public String toString() {
		return this.toJson();
	}

	public Long getSpent() {
		return spent;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

}
