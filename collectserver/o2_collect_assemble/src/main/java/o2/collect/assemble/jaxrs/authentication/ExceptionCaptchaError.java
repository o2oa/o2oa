package o2.collect.assemble.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionCaptchaError extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionCaptchaError() {
		super("图片验证码错误.");
	}

}