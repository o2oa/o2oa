package com.x.processplatform.assemble.surface.jaxrs.correlation;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionUpdateWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.SiteTargetWi;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;

class ActionUpdateWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithJob.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		ActionUpdateWi req = new ActionUpdateWi();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(
					new JobControlBuilder(effectivePerson, business, job).enableAllowSave().build().getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}
			req.setSiteTargetList(wi.getSiteTargetList());
			req.setPerson(effectivePerson.getDistinguishedName());
		}

		if (ListTools.isNotEmpty(wi.getSiteTargetList())) {
			Wo wo = ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_correlation_service_processing.class,
							Applications.joinQueryUri("correlation", "update", "type", "processplatform", "job", job), req, job)
					.getData(Wo.class);
			result.setData(wo);
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 8167538341492974963L;

		@FieldDescribe("关联文档列表，替换文档指定site的关联文档数据，为空则不操作.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "SiteTargetWi", fieldValue = "{'site':'关联内容框标识','targetList':[{'type':'关联目标类型(cms或processPlatform)','bundle':'关联目标标识','view':'来源视图'}]}")
		private List<SiteTargetWi> siteTargetList;

		public List<SiteTargetWi> getSiteTargetList() {
			return siteTargetList;
		}

		public void setSiteTargetList(
				List<SiteTargetWi> siteTargetList) {
			this.siteTargetList = siteTargetList;
		}

	}

	public static class Wo extends ActionCreateTypeProcessPlatformWo {

		private static final long serialVersionUID = -6712098733513365965L;

	}

}
