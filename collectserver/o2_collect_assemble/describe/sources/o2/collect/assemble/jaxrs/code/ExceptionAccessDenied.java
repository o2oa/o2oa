package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -6234823728401916146L;

	public ExceptionAccessDenied(String name) {
		super("访问拒绝,用户:{}.", name);
	}

}