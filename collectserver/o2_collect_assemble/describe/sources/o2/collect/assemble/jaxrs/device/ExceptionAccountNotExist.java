package o2.collect.assemble.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAccountNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAccountNotExist(String name) {
		super("帐号不存在:{}.", name);
	}
}
