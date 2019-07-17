package com.x.base.core.project.test.gson;

import com.google.gson.annotations.SerializedName;

public class Foo {

	@SerializedName("bbb")
	public String getBbb() {
		return bbb + "ttt";
	}

	public String aaa = "aaa";

	private String bbb = "bbb";

	public String getAaa() {
		return aaa;
	}

	public void setAaa(String aaa) {
		this.aaa = aaa;
	}

	public void setBbb(String bbb) {
		this.bbb = bbb;
	}

}
