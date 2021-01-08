package com.x.base.core.project.message;

import com.x.base.core.project.annotation.FieldDescribe;

public class PmsMessage extends Message {

	private static final long serialVersionUID = 2038077554351155648L;

	@FieldDescribe("账号")
	private String account;

	@FieldDescribe("组织")
	private String unit;

	@FieldDescribe("密码")
	private String password;

	@FieldDescribe("ticker")
	private String ticker;

	@FieldDescribe("文本")
	private String text;

	@FieldDescribe("标题")
	private String title;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}