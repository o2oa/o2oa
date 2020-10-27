package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;

public class Web extends ConfigObject {

	private static final long serialVersionUID = -1549522764856326338L;

	public static Web defaultInstance() {
		return new Web();
	}

	public Web() {

	}

	@FieldDescribe("使用Post模拟Put,Get模拟Delete的模块.")
	private List<String> mocks = new ArrayList<>();

	public List<String> getMocks() {
		return mocks;
	}

	public void setMocks(List<String> mocks) {
		this.mocks = mocks;
	}

}
