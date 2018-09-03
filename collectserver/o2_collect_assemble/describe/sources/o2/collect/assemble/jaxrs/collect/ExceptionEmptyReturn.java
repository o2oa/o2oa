package o2.collect.assemble.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyReturn extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEmptyReturn(String message) {
		super("empty return: {}.", message);
	}
}
