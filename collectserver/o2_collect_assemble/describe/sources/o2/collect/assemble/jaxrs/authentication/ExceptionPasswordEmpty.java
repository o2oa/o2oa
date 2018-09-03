package o2.collect.assemble.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordEmpty extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionPasswordEmpty() {
		super("密码为空.");
	}

}