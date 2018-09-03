package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordNotMatch extends PromptException {

	private static final long serialVersionUID = -6234823728401916146L;

	public ExceptionPasswordNotMatch(String name) {
		super("组织{},密码错误.", name);
	}

}
