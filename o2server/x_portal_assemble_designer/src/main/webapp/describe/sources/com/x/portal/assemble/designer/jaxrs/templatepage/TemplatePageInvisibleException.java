package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.PromptException;

class TemplatePageInvisibleException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	TemplatePageInvisibleException(String person, String name, String id) {
		super("person: {} can not visible templatePage name: {} id: {}.", person, name, id);
	}
}
