package com.x.base.core.project.scripting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassFilter implements jdk.nashorn.api.scripting.ClassFilter {

	private Set<String> blockedSet = new HashSet<>();

	public ClassFilter(Collection<String> col) {
		if ((null != col) && (!col.isEmpty())) {
			this.blockedSet.addAll(col);
		}
	}

	@Override
	public boolean exposeToScripts(String className) {
		return !blockedSet.contains(className);
	}

}
