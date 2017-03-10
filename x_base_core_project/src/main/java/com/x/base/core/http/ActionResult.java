package com.x.base.core.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.exception.PromptException;
import com.x.base.core.gson.XGsonBuilder;

public class ActionResult<T> implements Serializable {

	private static final long serialVersionUID = 1484547449073789817L;

	private static Gson gson = XGsonBuilder.instance();

	private Type type = Type.success;

	public enum Type {
		success, warn, error, connectFatal
	}

	private T data;

	private Date date = new Date();

	private Long spent = -1L;

	private Long size = -1L;

	private Long count = 0L;

	private Long position = 0L;

	private String message = "";

	private String prompt;

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

	public T getData(Class<T> clz) throws Exception {
		if (Objects.equals(this.type, Type.connectFatal)) {
			throw new Exception(this.message);
		}
		if (null == data) {
			return null;
		}
		return XGsonBuilder.instance().fromJson(gson.toJsonTree(data), clz);
	}

	public List<T> getListData(Class<T> clz) throws Exception {
		if (Objects.equals(this.type, Type.connectFatal)) {
			throw new Exception(this.message);
		}
		if (null == data) {
			return new ArrayList<T>();
		}
		java.lang.reflect.Type listType = new TypeToken<ArrayList<T>>() {
		}.getType();
		return XGsonBuilder.instance().fromJson(gson.toJsonTree(data), listType);
	}

	public Type getType() {
		return type;
	}

	public String toJson() {
		this.spent = (new Date()).getTime() - date.getTime();
		String str = XGsonBuilder.instance().toJson(this);
		return str;
	}

	public void error(Throwable th) {
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

}
