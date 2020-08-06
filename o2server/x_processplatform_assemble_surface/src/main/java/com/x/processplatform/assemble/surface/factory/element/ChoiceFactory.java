package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Process;

public class ChoiceFactory extends ElementFactory {

	public ChoiceFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Choice pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public Choice pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Choice.class);
	}

	public List<Choice> listWithProcess(Process process) throws Exception {
		return this.listWithProcess(Choice.class, process);
	}
}