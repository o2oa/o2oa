package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionMobileNotInControllerMobileList extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	public ExceptionMobileNotInControllerMobileList(String mobile) {
		super("手机:" + mobile + ",不在登记的具有管理权限的手机中.");
	}
}
