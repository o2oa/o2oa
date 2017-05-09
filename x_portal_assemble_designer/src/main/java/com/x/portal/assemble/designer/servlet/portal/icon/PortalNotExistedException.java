package com.x.portal.assemble.designer.servlet.portal.icon;

import com.x.base.core.exception.PromptException;

class PortalNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalNotExistedException(String flag) {
		super("portal: {} not existed.", flag);
	}
}
