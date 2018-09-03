package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionCaptchaInvalid extends PromptException {

	private static final long serialVersionUID = -6234823728401916146L;

	public ExceptionCaptchaInvalid() {
		super("验证码错误.");
	}

}
