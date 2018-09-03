package o2.collect.assemble.jaxrs.module;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameEmpty extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionNameEmpty() {
		super("名称不能为空");
	}

}
