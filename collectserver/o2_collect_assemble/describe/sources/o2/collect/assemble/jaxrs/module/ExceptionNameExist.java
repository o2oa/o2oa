package o2.collect.assemble.jaxrs.module;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionNameExist(String name) {
		super("{} 名称已存在.", name);
	}

}
