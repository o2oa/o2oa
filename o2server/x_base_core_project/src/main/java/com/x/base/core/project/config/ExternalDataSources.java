package com.x.base.core.project.config;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;

public class ExternalDataSources extends CopyOnWriteArrayList<ExternalDataSource> {

	private static final long serialVersionUID = 4502077979125945875L;

	public static ExternalDataSources defaultInstance() {
		return new ExternalDataSources();
	}

	public ExternalDataSources() {
		super();
	}

	public Boolean enable() {
		if (this.isEmpty()) {
			return false;
		}
		for (ExternalDataSource o : this) {
			if (BooleanUtils.isTrue(o.getEnable())) {
				return true;
			}
		}
		return false;
	}

}
