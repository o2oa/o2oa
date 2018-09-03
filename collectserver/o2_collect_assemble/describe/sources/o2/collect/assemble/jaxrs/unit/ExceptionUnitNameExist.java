package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitNameExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUnitNameExist(String name) {
		super("组织名称冲突:{}." + name);
	}
}
