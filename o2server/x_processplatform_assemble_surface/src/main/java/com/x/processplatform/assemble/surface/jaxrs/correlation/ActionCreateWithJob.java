package com.x.processplatform.assemble.surface.jaxrs.correlation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWi;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionCreateWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithJob.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		ActionCreateTypeProcessPlatformWi req = new ActionCreateTypeProcessPlatformWi();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(
					new JobControlBuilder(effectivePerson, business, job).enableAllowSave().build().getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}
			req.setTargetList(readTarget(effectivePerson, business, wi.getTargetList()));
			req.setPerson(effectivePerson.getDistinguishedName());
		}

		if (ListTools.isNotEmpty(wi.getTargetList())) {
			Wo wo = ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_correlation_service_processing.class,
							Applications.joinQueryUri("correlation", "type", "processplatform", "job", job), req, job)
					.getData(Wo.class);
			result.setData(wo);
		}
		return result;
	}

	private List<TargetWi> readTarget(EffectivePerson effectivePerson, Business business, List<TargetWi> targets)
			throws Exception {
		List<TargetWi> list = new ArrayList<>();
		if (ListTools.isNotEmpty(targets)) {
			for (TargetWi targetWi : targets) {
				if (StringUtils.equalsIgnoreCase(targetWi.getType(), Correlation.TYPE_PROCESSPLATFORM)) {
					list.add(readTargetProcessPlatform(effectivePerson, business, targetWi.getBundle(),
							targetWi.getSite(), targetWi.getView()));
				} else if (StringUtils.equalsIgnoreCase(targetWi.getType(), Correlation.TYPE_CMS)) {
					list.add(readTargetCms(effectivePerson, business, targetWi.getBundle(), targetWi.getSite(),
							targetWi.getView()));
				} else {
					throw new ExceptionAccessDenied(effectivePerson);
				}
			}
		}
		return list;
	}

	private TargetWi readTargetProcessPlatform(EffectivePerson effectivePerson, Business business, String bundle,
			String site, String view) throws Exception {
		Work work = business.entityManagerContainer().firstEqual(Work.class, Work.job_FIELDNAME, bundle);
		if (null == work) {
			WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
					WorkCompleted.job_FIELDNAME, bundle);
			if (null == workCompleted) {
				throw new ExceptionEntityExist(bundle);
			}
		}
		TargetWi targetWi = new TargetWi();
		targetWi.setType(Correlation.TYPE_PROCESSPLATFORM);
		targetWi.setBundle(bundle);
		targetWi.setSite(site);
		targetWi.setView(view);
		return targetWi;
	}

	private TargetWi readTargetCms(EffectivePerson effectivePerson, Business business, String bundle, String site,
			String view) throws Exception {
		Document document = business.entityManagerContainer().firstEqual(Document.class, JpaObject.id_FIELDNAME,
				bundle);
		if (null == document) {
			throw new ExceptionEntityExist(bundle);
		}
		TargetWi targetWi = new TargetWi();
		targetWi.setType(Correlation.TYPE_CMS);
		targetWi.setBundle(bundle);
		targetWi.setSite(site);
		targetWi.setView(view);
		return targetWi;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 8167538341492974963L;

		@FieldDescribe("关联目标.")
		private List<TargetWi> targetList;

		public List<TargetWi> getTargetList() {
			return targetList;
		}

		public void setTargetList(List<TargetWi> targetList) {
			this.targetList = targetList;
		}

	}

	public static class Wo extends ActionCreateTypeProcessPlatformWo {

		private static final long serialVersionUID = -6712098733513365965L;

	}

}