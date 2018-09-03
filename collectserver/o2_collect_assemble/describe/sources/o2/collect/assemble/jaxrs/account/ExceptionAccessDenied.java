package o2.collect.assemble.jaxrs.account;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionAccessDenied(String name) {
		super("访问拒绝,用户:{}.", name);
	}
}
