package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;

public class ParallelFactory extends ElementFactory {

	public ParallelFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Parallel pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Parallel pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Parallel.class, exceptionWhen, Parallel.FLAGS);
	}

	public List<Parallel> listWithProcess(Process process) throws Exception {
		List<Parallel> list = this.listWithProcess(Parallel.class, process);
		return list;
	}
}