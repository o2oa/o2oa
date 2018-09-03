package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUnitNotExist(String flag) {
		super("组织不存在:{}.", flag);
	}
}
