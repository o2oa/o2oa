package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionChangeSite extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionChangeSite.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, String site) throws Exception {

		LOGGER.debug("execute:{}, id:{}, workId:{}, site:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> workId, () -> site);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}

			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit().build();

			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, workId);
			}

			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPerson(effectivePerson);
			if (!this.edit(attachment, effectivePerson, identities, units, business)) {
				throw new ExceptionAccessDenied(effectivePerson, attachment);
			}
			emc.beginTransaction(Attachment.class);
			attachment.setSite(site);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionChangeSite$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 3334070435843444140L;

	}

}
