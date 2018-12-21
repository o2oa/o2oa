package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.PromptException;

class PortalInvisibleException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalInvisibleException(String person, String name, String id) {
		super("person: {} can not visible portal name: {} id: {}.", person, name, id);
	}
}
