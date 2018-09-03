package o2.collect.assemble.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInvalidCode extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	public ExceptionInvalidCode() {
		super("验证码错误.");
	}
}
