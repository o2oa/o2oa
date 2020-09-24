package com.x.base.core.project.thread;

import java.util.Map;
import java.util.TreeMap;

public abstract class ParameterThread extends Thread {

	public Map<Object, Object> parameter;

	public ParameterThread(ThreadGroup group, ParameterThread target, String name) {
		super(group, target, name);
		parameter = target.parameter;
	}

}