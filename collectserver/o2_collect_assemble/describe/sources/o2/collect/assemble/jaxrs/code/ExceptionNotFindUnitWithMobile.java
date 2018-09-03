package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotFindUnitWithMobile extends PromptException {

	private static final long serialVersionUID = -6234823728401916146L;

	public ExceptionNotFindUnitWithMobile(String mobile) {
		super("手机号:" + mobile + "尚未登记.");
	}

}
