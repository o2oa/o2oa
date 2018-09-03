package o2.collect.assemble.jaxrs.unit;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionNameExist(String name) {
		super("用户:" + Objects.toString(name) + "已注册.");
	}
}
