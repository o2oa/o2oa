package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionSso extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSso() {
		super("sso 登录失败.");
	}
}
