package com.x.processplatform.assemble.surface.jaxrs.draft;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
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
import com.x.cms.core.entity.content.Data;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.draft.ActionDrawWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDraw extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDraw.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String processFlag, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, processFlag:{}.", effectivePerson::getDistinguishedName, () -> processFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			String identity = this.decideCreatorIdentity(business, effectivePerson, wi.getIdentity());
			String person = business.organization().person().getWithIdentity(identity);
			String unit = business.organization().unit().getWithIdentity(identity);
			Process process = business.process().pick(processFlag);
			if (null == process) {
				throw new ExceptionEntityNotExist(processFlag);
			}
			Application application = business.application().pick(process.getApplication());
			List<String> roles = business.organization().role().listWithPerson(effectivePerson);
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
				throw new ExceptionAccessDenied(effectivePerson, application);
			}
			Work work = this.mockWork(application, process, person, identity, unit, wi.getTitle());
			// 设置id值与workid相同.save可以判断
			work.setId("");
			String form = this.findForm(business, process);
			if (StringUtils.isEmpty(form)) {
				throw new ExceptionNoneForm();
			}
			work.setForm(form);
			Wo wo = new Wo();
			wo.setWork(WoWork.copier.copy(work));
			wo.setData(wi.getData());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionDraw$Wi")
	public static class Wi extends ActionDrawWi {

		private static final long serialVersionUID = -5147265323027256791L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionDraw$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 8693897765699829238L;

		@FieldDescribe("工作.")
		@Schema(description = "工作.")
		private WoWork work;

		@FieldDescribe("业务数据.")
		@Schema(description = "业务数据.")
		private Data data = new Data();

		public WoWork getWork() {
			return work;
		}

		public void setWork(WoWork work) {
			this.work = work;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionDraw$WoWork")
	public static class WoWork extends Work {

		private static final long serialVersionUID = 1573047112378070272L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

}
