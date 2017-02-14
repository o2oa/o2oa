package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Process;

public class AgentFactory extends ElementFactory {

	public AgentFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Agent pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Agent pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Agent.class, exceptionWhen, Agent.FLAGS);
	}

	public List<Agent> listWithProcess(Process process) throws Exception {
		List<Agent> list = this.listWithProcess(Agent.class, process);
		return list;
	}

}