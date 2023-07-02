package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;

/**
 * 创建处于start状态的work
 * 
 * @author Rui
 * 
 *         此方法不需要推入线程池运行
 */
class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String processId, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, processId:{}.", effectivePerson::getDistinguishedName, () -> processId);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Work work = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.element().get(processId, Process.class);
			Application application = business.element().get(process.getApplication(), Application.class);
			Begin begin = business.element().getBeginWithProcess(process.getId());
			work = create(application, process, begin);
			emc.beginTransaction(Work.class);
			if ((null != jsonElement) && jsonElement.isJsonObject()) {
				WorkDataHelper workDataHelper = new WorkDataHelper(emc, work);
				workDataHelper.update(jsonElement);
			}
			emc.persist(work, CheckPersistType.all);
			emc.commit();
			wo.setId(work.getId());
		}
		MessageFactory.work_create(work);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 3599562746351153636L;

	}

	private Work create(Application application, Process process, Begin begin) throws Exception {
		Date now = new Date();
		Work work = new Work();
		/* 标识工作数据未修改 */
		work.setDataChanged(false);
		work.setWorkThroughManual(false);
		work.setWorkCreateType(Work.WORKCREATETYPE_SURFACE);
		work.setApplication(application.getId());
		work.setApplicationName(application.getName());
		work.setApplicationAlias(application.getAlias());
		work.setProcess(process.getId());
		work.setProcessName(process.getName());
		work.setProcessAlias(process.getAlias());
		work.setJob(StringTools.uniqueToken());
		work.setStartTime(now);
		// work.setErrorRetry(0);
		work.setWorkStatus(WorkStatus.start);
		work.setDestinationActivity(begin.getId());
		work.setDestinationActivityType(ActivityType.begin);
		work.setDestinationRoute(null);
		work.setSplitting(false);
		work.setActivityToken(StringTools.uniqueToken());
		return work;
	}

}
