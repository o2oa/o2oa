package o2.collect.assemble.jaxrs.applog;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppLogNotExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionAppLogNotExist(String id) {
		super("提示错误记录不存在:{}.");
	}

}
