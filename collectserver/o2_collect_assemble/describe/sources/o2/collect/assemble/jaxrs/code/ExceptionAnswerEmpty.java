package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionAnswerEmpty extends PromptException {

	private static final long serialVersionUID = -2526413098010814131L;

	public ExceptionAnswerEmpty() {
		super("值不能为空.");
	}

}
