package com.x.server.console;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

public class Versions extends CopyOnWriteArrayList<Version> {

	private static final long serialVersionUID = 2726475055157860283L;

	public Version find(String name) throws Exception {
		for (Version o : this) {
			if (StringUtils.equals(name, o.getName())) {
				return o;
			}
		}
		return null;
	}

}
