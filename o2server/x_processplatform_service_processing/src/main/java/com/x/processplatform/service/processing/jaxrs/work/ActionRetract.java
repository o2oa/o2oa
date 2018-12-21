package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

/**
 * 根据WorkLogId进行召回
 * 
 * @author Rui
 *
 */
class ActionRetract extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRetract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workLogId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = null;
			WorkLog workLog = null;
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			workLog = emc.find(workLogId, WorkLog.class);
			if (null == workLog) {
				throw new ExceptionWorkLogNotExist(workLogId);
			}
			if (!StringUtils.equals(work.getJob(), workLog.getJob())) {
				throw new ExceptionWorkNotMatchWithWorkLog(work.getTitle(), work.getId(), work.getJob(),
						workLog.getId(), workLog.getJob());
			}
			List<Work> works = this.listForwardWork(business, workLog);
			emc.beginTransaction(Work.class);
			emc.beginTransaction(Task.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);
			emc.beginTransaction(WorkLog.class);
			for (Work o : works) {
				this.cleanComplex(business, o);
				if (!StringUtils.equals(work.getId(), o.getId())) {
					business.entityManagerContainer().remove(o);
				}
			}
			this.retractAsWorkLog(work, workLog);
			emc.delete(WorkLog.class,
					business.workLog().listWithFromActivityTokenForward(workLog.getFromActivityToken()));
			emc.commit();
			Processing processing = new Processing(0, new ProcessingAttributes(), emc);
			ProcessingConfigurator processingConfigurator = new ProcessingConfigurator();
			processingConfigurator.setContinueLoop(false);
			processingConfigurator.setActivityCreateRead(false);
			processingConfigurator.setActivityCreateReview(false);
			processingConfigurator.setChangeActivityToken(false);
			processingConfigurator.setJoinAtExecute(false);
			processingConfigurator.setActivityStampArrivedWorkLog(false);
			processing.processing(work.getId(), processingConfigurator);
			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	private void cleanComplex(Business business, Work work) throws Exception {
		business.entityManagerContainer().delete(Task.class,
				business.task().listWithActivityToken(work.getActivityToken()));
		business.entityManagerContainer().delete(TaskCompleted.class,
				business.taskCompleted().listWithActivityToken(work.getActivityToken()));
		business.entityManagerContainer().delete(Read.class,
				business.read().listWithActivityToken(work.getActivityToken()));
		business.entityManagerContainer().delete(ReadCompleted.class,
				business.readCompleted().listWithActivityToken(work.getActivityToken()));
		// business.entityManagerContainer().delete(Review.class,
		// business.review().listWithActivityToken(work.getActivityToken()));
	}

	private List<Work> listForwardWork(Business business, WorkLog workLog) throws Exception {
		List<String> ids = business.workLog()
				.listWithFromActivityTokenForwardNotConnected(workLog.getFromActivityToken());
		List<String> activityTokens = SetUniqueList.setUniqueList(new ArrayList<String>());
		for (WorkLog o : business.entityManagerContainer().fetch(ids, WorkLog.class,
				ListTools.toList(WorkLog.fromActivityToken_FIELDNAME))) {
			activityTokens.add(o.getFromActivityToken());
		}
		List<String> workIds = business.work().listWithActivityToken(activityTokens);
		return business.entityManagerContainer().list(Work.class, workIds);
	}

	private void retractAsWorkLog(Work work, WorkLog workLog) throws Exception {
		work.setDestinationActivity(workLog.getFromActivity());
		work.setDestinationActivityType(workLog.getFromActivityType());
		work.setActivityToken(workLog.getFromActivityToken());
		work.setSplitting(workLog.getSplitting());
		work.setSplitValue(workLog.getSplitValue());
		work.setSplitToken(workLog.getSplitToken());
		work.setSplitTokenList(workLog.getSplitTokenList());
	}

}