package com.x.program.center.jaxrs.agent;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.core.entity.Agent;

class ActionUpdate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Agent agent = emc.flag(flag, Agent.class );
			if (null == agent) {
				throw new ExceptionAgentNotExist(flag);
			}
			String text = new String(bytes, DefaultCharset.name);
			emc.beginTransaction(Agent.class);
			agent.setText(text);
			this.addComment(agent);
			emc.commit();
			wo.setId(agent.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
