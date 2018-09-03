package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionMobileInvalid extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	public ExceptionMobileInvalid(String mobile) {
		super("手机号格式错误:" + mobile + ".");
	}
}
