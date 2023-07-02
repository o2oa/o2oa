package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Agent;

public class WrapAgent extends Agent {

	private static final long serialVersionUID = -7592184343034018992L;

	public static final WrapCopier<Agent, WrapAgent> outCopier = WrapCopierFactory.wo(Agent.class, WrapAgent.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapAgent, Agent> inCopier = WrapCopierFactory.wi(WrapAgent.class, Agent.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);

}
