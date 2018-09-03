package o2.collect.assemble.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyAppLog extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEmptyAppLog() {
		super("接收到一个空数据.");
	}
}
