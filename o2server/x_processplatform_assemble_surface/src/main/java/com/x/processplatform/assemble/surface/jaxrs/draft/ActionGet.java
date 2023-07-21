package com.x.processplatform.assemble.surface.jaxrs.draft;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Draft draft = emc.find(id, Draft.class);
			if (null == draft) {
				throw new ExceptionEntityNotExist(id, Draft.class);
			}
			if ((!effectivePerson.isPerson(draft.getPerson())) && (!business
					.ifPersonCanManageApplicationOrProcess(effectivePerson, draft.getApplication(), draft.getProcess()))) {
				throw new ExceptionAccessDenied(effectivePerson, draft);
			}
			Application application = business.application().pick(draft.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(draft.getApplication(), Application.class);
			}
			Process process = business.process().pick(draft.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(draft.getProcess(), Process.class);
			}
			Wo wo = new Wo();

			wo.setData(draft.getProperties().getData());
			Work work = this.mockWork(application, process, draft.getPerson(), draft.getIdentity(), draft.getUnit(),
					draft.getTitle());
			// 设置id值与workid相同.save可以判断
			work.setId(draft.getId());
			String form = this.findForm(business, process);
			if (StringUtils.isEmpty(form)) {
				throw new ExceptionNoneForm();
			}
			work.setForm(form);
			wo.setWork(WoWork.copier.copy(work));

			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionGet$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -3079311013584307428L;

		@FieldDescribe("业务数据.")
		@Schema(description = "业务数据.")
		private Data data;

		@FieldDescribe("工作.")
		@Schema(description = "工作.")
		private WoWork work;

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public WoWork getWork() {
			return work;
		}

		public void setWork(WoWork work) {
			this.work = work;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionGet$WoWork")
	public static class WoWork extends Work {
		
		private static final long serialVersionUID = 1573047112378070272L;
		
		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));
	
	}

}
