package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	public ExceptionAccessDenied(String name) {
		super("用户:" + name + ", 权限不足.");
	}
}
