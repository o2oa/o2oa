package o2.collect.assemble.jaxrs.code;

import com.x.base.core.project.exception.PromptException;

class ExceptionMaxInterval extends PromptException {

	private static final long serialVersionUID = -6234823728401916146L;

	public ExceptionMaxInterval(String mobile, Integer interval, Integer count) {
		super("短信发送请求被忽略.手机号:{}, 在{}秒内重复发送{}次.", mobile, interval, count);
	}

}