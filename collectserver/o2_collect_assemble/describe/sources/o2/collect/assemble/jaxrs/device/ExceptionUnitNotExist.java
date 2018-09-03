package o2.collect.assemble.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUnitNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUnitNotExist(String flag) {
		super("组织不存在:{}.", flag);
	}
}
