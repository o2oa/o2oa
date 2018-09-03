package o2.collect.assemble.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordNotMatch extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionPasswordNotMatch(String credential) {
		super("用户名:{},密码不匹配.", credential);
	}

}