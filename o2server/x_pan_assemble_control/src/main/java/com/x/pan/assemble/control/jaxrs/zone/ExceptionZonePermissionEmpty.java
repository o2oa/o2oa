package com.x.pan.assemble.control.jaxrs.zone;

import com.x.base.core.project.exception.PromptException;

class ExceptionZonePermissionEmpty extends PromptException {

	private static final long serialVersionUID = 2478583851607826853L;

	ExceptionZonePermissionEmpty() {
		super("权限列表不能为空.");
	}
}
