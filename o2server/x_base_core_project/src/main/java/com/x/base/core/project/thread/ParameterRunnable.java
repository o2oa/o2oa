package com.x.base.core.project.thread;

import java.util.Map;
import java.util.TreeMap;

public abstract class ParameterRunnable implements Runnable {

	public Map<Object, Object> parameter;

	public ParameterRunnable() {
		parameter = new TreeMap<>();
	}

}