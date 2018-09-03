package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccountNotMatch extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAccountNotMatch(String name) {
		super("帐号名称不符:{}." + name);
	}
}
