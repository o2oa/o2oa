package com.x.query.service.processing.jaxrs.design;

import com.x.base.core.entity.JpaObject;

/**
 *
 */
public enum ModuleType {

	processPlatform, portal, cms;
	public static final int length = JpaObject.length_64B;
}
