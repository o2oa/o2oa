package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCloseCheck extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCloseCheck.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		WoDraft woDraft = new WoDraft();
		woDraft.setValue(false);
		wo.setDraft(woDraft);
		Work work = null;
		Process process = null;
		boolean check = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null != work) {
				process = business.process().pick(work.getProcess());
				check = this.checkDraft(effectivePerson, business, work, process);
			}
		}
		if ((null != work) && (null != process) && check) {
			ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("work", work.getId()), work.getJob()).getData(Wo.class);
			wo.getDraft().setValue(true);
		} else {
			wo.getDraft().setValue(false);
		}
		result.setData(wo);
		return result;
	}

	private boolean checkDraft(EffectivePerson effectivePerson, Business business, Work work, Process process)
			throws Exception {
		return ((null != work) && (BooleanUtils.isFalse(work.getDataChanged()))
				&& (Objects.equals(ActivityType.manual, work.getActivityType())) && (null != process)
				&& (BooleanUtils.isTrue(process.getCheckDraft())) && effectivePerson.isPerson(work.getCreatorPerson())
				&& (business.entityManagerContainer().countEqual(Attachment.class, Attachment.job_FIELDNAME,
						work.getJob()) == 0));

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCloseCheck$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 837722785808492603L;

		@FieldDescribe("检查删除草稿结果.")
		@Schema(description = "检查删除草稿结果.")
		private WoDraft draft;

		public WoDraft getDraft() {
			return draft;
		}

		public void setDraft(WoDraft draft) {
			this.draft = draft;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCloseCheck$WoDraft")
	public static class WoDraft extends WrapBoolean {

		private static final long serialVersionUID = 376594708278621837L;

	}

}