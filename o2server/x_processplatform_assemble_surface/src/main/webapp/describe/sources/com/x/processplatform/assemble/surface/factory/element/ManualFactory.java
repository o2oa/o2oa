package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;

public class ManualFactory extends ElementFactory {

	public ManualFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Manual pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Manual pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Manual.class);
	}

	public List<Manual> listWithProcess(Process process) throws Exception {
		List<Manual> list = this.listWithProcess(Manual.class, process);
		return list;
	}
}