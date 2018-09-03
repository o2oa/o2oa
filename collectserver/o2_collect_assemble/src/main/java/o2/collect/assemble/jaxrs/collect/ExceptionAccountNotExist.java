package o2.collect.assemble.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccountNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionAccountNotExist(String account) {
		super("账户不存在:{}.", account);
	}
}
