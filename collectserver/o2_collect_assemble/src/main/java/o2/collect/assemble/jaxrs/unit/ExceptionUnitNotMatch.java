package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitNotMatch extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUnitNotMatch(String name) {
		super("组织名称不符:{}." + name);
	}
}
