package o2.collect.assemble.jaxrs.warnlog;

import com.x.base.core.project.exception.PromptException;

class ExceptionWarnLogNotExist extends PromptException {

	private static final long serialVersionUID = 8982999877082823184L;

	ExceptionWarnLogNotExist(String id) {
		super("警告记录不存在:{}.");
	}

}
