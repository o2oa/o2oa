package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordEmpty extends PromptException {

	private static final long serialVersionUID = -6234823728401916146L;

	public ExceptionPasswordEmpty() {
		super("密码为空.");
	}

}
