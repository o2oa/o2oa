package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Process;

public class InvokeFactory extends ElementFactory {

	public InvokeFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Invoke pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public Invoke pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Invoke.class);
	}

	public List<Invoke> listWithProcess(Process process) throws Exception {
		return this.listWithProcess(Invoke.class, process);
	}
}