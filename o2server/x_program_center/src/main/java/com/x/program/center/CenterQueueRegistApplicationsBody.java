package com.x.program.center;

import java.util.ArrayList;

import com.x.base.core.project.Application;

public class CenterQueueRegistApplicationsBody extends ArrayList<Application> implements CenterQueueBody {

	private static final long serialVersionUID = -6222059999168636606L;

	private String node;

	public String type() {
		return TYPE_REGISTAPPLICATIONS;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

}
