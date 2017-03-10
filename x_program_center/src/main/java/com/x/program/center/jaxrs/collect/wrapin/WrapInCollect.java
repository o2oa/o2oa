package com.x.program.center.jaxrs.collect.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.base.core.project.server.Collect;

@Wrap(Collect.class)
public class WrapInCollect extends Collect {

	public static List<String> Excludes = new ArrayList<>();

	private String mobile;

	private String codeAnswer;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCodeAnswer() {
		return codeAnswer;
	}

	public void setCodeAnswer(String codeAnswer) {
		this.codeAnswer = codeAnswer;
	}
}
