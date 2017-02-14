package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.utils.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.content.tools.DataHelper;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.service.processing.Business;

/**
 * 创建处于start状态的work
 * 
 * @author Rui
 *
 */
public class ActionCreate {

	WrapOutId execute(Business business, String processId, JsonElement jsonElement) throws Exception {

		EntityManagerContainer emc = business.entityManagerContainer();
		Process process = business.element().get(processId, Process.class);
		Application application = business.element().get(process.getApplication(), Application.class);
		Begin begin = business.element().getBeginWithProcess(process.getId());
		Work work = this.create(application, process, begin);
		emc.beginTransaction(Work.class);
		emc.persist(work, CheckPersistType.all);
		if ((null != jsonElement) && jsonElement.isJsonObject()) {
			DataHelper dataHelper = new DataHelper(emc, work);
			dataHelper.update(jsonElement);
		}
		emc.commit();
		return new WrapOutId(work.getId());
	}

	private Work create(Application application, Process process, Begin begin) throws Exception {
		Date now = new Date();
		Work work = new Work();
		work.setApplication(application.getId());
		work.setApplicationName(application.getName());
		work.setProcess(process.getId());
		work.setProcessName(process.getName());
		work.setJob(StringTools.uniqueToken());
		work.setStartTime(now);
		work.setExecuted(false);
		work.setInquired(false);
		work.setErrorRetry(0);
		work.setWorkStatus(WorkStatus.start);
		work.setDestinationActivity(begin.getId());
		work.setDestinationActivityType(ActivityType.begin);
		work.setDestinationRoute(null);
		work.setSplitting(false);
		work.setActivityToken(StringTools.uniqueToken());
		return work;
	}

}
