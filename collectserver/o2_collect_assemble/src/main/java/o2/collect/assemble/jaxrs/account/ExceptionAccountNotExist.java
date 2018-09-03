package o2.collect.assemble.jaxrs.account;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccountNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionAccountNotExist(String flag) {
		super("账户: {} 不存在.", flag);
	}
}
