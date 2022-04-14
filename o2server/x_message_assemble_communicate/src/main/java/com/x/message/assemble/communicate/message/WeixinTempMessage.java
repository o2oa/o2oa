package com.x.message.assemble.communicate.message;

import java.util.Map;

import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * 微信发送模版消息的对象
 */
public class WeixinTempMessage extends GsonPropertyObject {

	private static final long serialVersionUID = 1577792511569896768L;
	private String touser;
	private String template_id;
	private String url;
	private String topcolor;
	private Map<String, WeixinTempMessageFieldObj> data; // 模版字段数据

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTopcolor() {
		return topcolor;
	}

	public void setTopcolor(String topcolor) {
		this.topcolor = topcolor;
	}

	public Map<String, WeixinTempMessageFieldObj> getData() {
		return data;
	}

	public void setData(Map<String, WeixinTempMessageFieldObj> data) {
		this.data = data;
	}

	/**
	 * 模版字段对象
	 */
	public static class WeixinTempMessageFieldObj extends GsonPropertyObject {

		private static final long serialVersionUID = -4230870572917531355L;
		private String value;
		private String color;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}
	}
}
