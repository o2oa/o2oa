package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Process;

public class CancelFactory extends ElementFactory {

	public CancelFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Cancel pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public Cancel pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Cancel.class);
	}

	public List<Cancel> listWithProcess(Process process) throws Exception {
		return this.listWithProcess(Cancel.class, process);
	}
}