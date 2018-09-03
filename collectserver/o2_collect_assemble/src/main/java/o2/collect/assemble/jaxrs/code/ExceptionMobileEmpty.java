package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionMobileEmpty extends PromptException {

	private static final long serialVersionUID = -2526413098010814131L;

	public ExceptionMobileEmpty() {
		super("手机号不能为空.");
	}

}
