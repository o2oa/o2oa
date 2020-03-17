package com.x.portal.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionPortalNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionPortalNotExist(String flag) {
		super("站点:{}不存在.", flag);
	}
}
