package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionCodeAnswerEmpty extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	public ExceptionCodeAnswerEmpty() {
		super("手机验证码不能为空.");
	}
}
