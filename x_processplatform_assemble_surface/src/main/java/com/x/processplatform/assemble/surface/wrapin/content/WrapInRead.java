package com.x.processplatform.assemble.surface.wrapin.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.content.Read;

@Wrap(Read.class)
public class WrapInRead extends Read {

	private static final long serialVersionUID = -7613931473817952597L;
	public static List<String> Includes = new ArrayList<>();;
	static {
		Includes.add("opinion");
	}

	private List<String> identityList;

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

}