package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordEmpty extends PromptException {

	private static final long serialVersionUID = 169666715828180911L;

	public ExceptionPasswordEmpty() {
		super("名称不能为空.");
	}
}
