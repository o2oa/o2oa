package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitEmpty extends PromptException {

	private static final long serialVersionUID = -6234823728401916146L;

	public ExceptionUnitEmpty() {
		super("组织名为空.");
	}

}
