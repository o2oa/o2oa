package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitNotExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionUnitNotExist(String id) {
		super("组织不存在:{}.", id);
	}

}
