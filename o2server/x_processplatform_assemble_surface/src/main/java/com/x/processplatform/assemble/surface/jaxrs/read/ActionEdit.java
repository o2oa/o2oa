package com.x.processplatform.assemble.surface.jaxrs.read;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.assemble.surface.jaxrs.read.ActionEditWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			if (BooleanUtils.isTrue(read.getCompleted())) {
				WorkCompleted workCompleted = emc.find(read.getWorkCompleted(), WorkCompleted.class);
				Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
						.enableAllowReadProcessing().build();
				if (BooleanUtils.isNotTrue(control.getAllowReadProcessing())) {
					throw new ExceptionAccessDenied(effectivePerson, read);
				}
			} else {
				Work work = emc.find(read.getWork(), Work.class);
				Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowReadProcessing()
						.build();
				if (BooleanUtils.isNotTrue(control.getAllowReadProcessing())) {
					throw new ExceptionAccessDenied(effectivePerson, read);
				}
			}
			emc.beginTransaction(Read.class);
			if (StringUtils.isNotEmpty(wi.getOpinion())) {
				read.setOpinion(wi.getOpinion());
			}
			emc.check(read, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionEdit$Wi")
	public static class Wi extends ActionEditWi {

		private static final long serialVersionUID = -4081879316283847032L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionEdit$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 7170600975986555915L;
	}

}
