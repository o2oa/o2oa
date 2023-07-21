package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;

public class EndFactory extends ElementFactory {

	public EndFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public End pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public End pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, End.class);
	}

	public List<End> listWithProcess(Process process) throws Exception {
		return this.listWithProcess(End.class, process);
	}
}