package com.x.meeting.assemble.control.jaxrs.attachment;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.role.RoleDefinition;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutAttachment;

class ActionListPrev extends StandardJaxrsAction {
	ActionResult<List<WrapOutAttachment>> execute(EffectivePerson effectivePerson, String id, Integer count)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (effectivePerson.isNotManager() && (!business.organization().role().hasAny(RoleDefinition.Manager,
					RoleDefinition.MeetingManager))) {
				throw new Exception("person{" + effectivePerson.getName() + "} has  Insufficient permissions.");
			}
			result = this.standardListPrev(WrapTools.attachmentOutCopier, id, count, "sequence", null, null, null, null,
					null, null, null, true, DESC);
			return result;
		}
	}
}
