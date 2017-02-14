package com.x.base.core.project.server;

public class Token {

	private static final String surfix = "o2platform";

	private String key;

	private String password;

	private String sso;
	
	private String ssl;

	public String getKey() {
		return key + surfix;
	}

	public String getCipher() {
		return password + surfix;
	}

	public String getSso() {
		return sso;
	}

	public String getPassword() {
		return password;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setSso(String sso) {
		this.sso = sso;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSsl() {
		return ssl;
	}

	public void setSsl(String ssl) {
		this.ssl = ssl;
	}

}
