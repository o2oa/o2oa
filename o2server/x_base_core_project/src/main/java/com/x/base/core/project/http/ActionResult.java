package com.x.base.core.project.http;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.gson.XGsonBuilder;

public class ActionResult<T> implements Serializable {

	private static final long serialVersionUID = 1484547449073789817L;

	protected static Gson gson = XGsonBuilder.instance();

	public enum Type {
		success, warn, error, connectFatal
	}

	protected Type type = Type.success;

	protected T data;

	protected String message = "";

	protected Date date = new Date();

	protected Long spent = -1L;

	protected Long size = -1L;

	protected Long count = 0L;

	protected Long position = 0L;

	protected String prompt;

	public transient Throwable throwable;

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

	/**
	 * 此方法不会抛出execption 与 ActionResponse 带类型的方法不同
	 * 
	 * @see ActionResponse
	 * 
	 * @return
	 */

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

	public String toCompactJson() {
		this.spent = (new Date()).getTime() - date.getTime();
		String str = XGsonBuilder.compactInstance().toJson(this);
		return str;
	}

	public void error(Throwable th) {
		this.throwable = th;
		if (th instanceof PromptException) {
			this.type = Type.error;
			this.prompt = th.getClass().getName();
			this.append(th.getMessage());
		} else {
			this.type = Type.error;
			this.append(th.getMessage());
		}
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

	public String getPrompt() {
		return prompt;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSpent(Long spent) {
		this.spent = spent;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

}