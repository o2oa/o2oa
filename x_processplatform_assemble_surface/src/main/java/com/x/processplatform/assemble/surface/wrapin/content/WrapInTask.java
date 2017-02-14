package com.x.processplatform.assemble.surface.wrapin.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.content.Task;

@Wrap(Task.class)
public class WrapInTask extends Task {

	private static final long serialVersionUID = 3083012793693272511L;

	public static List<String> Includes = new ArrayList<>();

	static {
		Includes.add("routeName");
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
