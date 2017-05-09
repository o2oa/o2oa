package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.exception.PromptException;

class PortalNotExistedException extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	PortalNotExistedException(String id) {
		super("指定的站点不存在:{}.", id);
	}

}
