package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.project.exception.PromptException;

class QueryStatAccessDeniedException extends PromptException {

	private static final long serialVersionUID = 5192798633373774203L;

	QueryStatAccessDeniedException(String person, String queryStat, String application) {
		super("person:{} access queryStat :{}, denied, in application: {}.", person, queryStat, application);
	}

}
