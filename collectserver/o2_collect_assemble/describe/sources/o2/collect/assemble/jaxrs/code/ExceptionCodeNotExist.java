package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionCodeNotExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionCodeNotExist(String id) {
		super("短信认证码不存在:{}.", id);
	}

}
