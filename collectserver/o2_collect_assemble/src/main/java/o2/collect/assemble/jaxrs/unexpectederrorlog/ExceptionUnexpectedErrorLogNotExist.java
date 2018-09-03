package o2.collect.assemble.jaxrs.unexpectederrorlog;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnexpectedErrorLogNotExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionUnexpectedErrorLogNotExist(String id) {
		super("意外错误记录不存在:{}.");
	}

}
