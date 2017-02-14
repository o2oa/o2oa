package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Condition;
import com.x.processplatform.core.entity.element.Process;

public class ConditionFactory extends ElementFactory {

	public ConditionFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Condition pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Condition pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Condition.class, exceptionWhen, Condition.FLAGS);
	}

	public List<Condition> listWithProcess(Process process) throws Exception {
		List<Condition> list = this.listWithProcess(Condition.class, process);
		return list;
	}
}