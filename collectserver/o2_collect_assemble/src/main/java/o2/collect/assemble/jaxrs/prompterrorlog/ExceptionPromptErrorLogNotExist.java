package o2.collect.assemble.jaxrs.prompterrorlog;

import com.x.base.core.project.exception.PromptException;

class ExceptionPromptErrorLogNotExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionPromptErrorLogNotExist(String id) {
		super("提示错误记录不存在:{}.");
	}

}
