package com.x.program.center.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.program.center.core.entity.Agent;

public class WrapAgent extends Agent {

	private static final long serialVersionUID = -5550913417471975012L;

	public static WrapCopier<Agent, WrapAgent> outCopier = WrapCopierFactory.wo(Agent.class, WrapAgent.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapAgent, Agent> inCopier = WrapCopierFactory.wi(WrapAgent.class, Agent.class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}