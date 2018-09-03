package o2.collect.assemble.jaxrs.module;

import com.x.base.core.project.exception.PromptException;

class ExceptionValidateUnitError extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionValidateUnitError(String unit) {
		super("{}认证错误.", unit);
	}
}
