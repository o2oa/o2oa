package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2TerminateWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

/**
 * 
 * @author Rui
 *
 */
class V2Terminate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Terminate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(new CallableImpl(param.id)).get(300,
				TimeUnit.SECONDS);
	}

	private Param init(String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.id = work.getId();
			param.job = work.getJob();
		}
		return param;
	}

	private class Param {

		private String id;
		private String job;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private CallableImpl(String workId) {
			this.workId = workId;
		}

		private String workId;

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Work work = emc.find(workId, Work.class);
				Business business = new Business(emc);
				Activity activity = business.element().getActivity(work.getActivity());
				AeiObjects aeiObjects = new AeiObjects(business, work, activity, new ProcessingAttributes());
				WorkCompleted workCompleted = createWorkCompleted(aeiObjects.getWork(), activity);
				workCompleted.setAllowRollback(false);
				aeiObjects.getCreateWorkCompleteds().add(workCompleted);
				aeiObjects.getTasks().stream().forEach(o -> aeiObjects.getDeleteTasks().add(o));
				aeiObjects.getDocumentVersions().stream().forEach(o -> aeiObjects.getDeleteDocumentVersions().add(o));
				aeiObjects.getTaskCompleteds().stream().forEach(o -> {
					// 已办的完成时间是不需要更新的
					o.setCompleted(true);
					o.setWorkCompleted(workCompleted.getId());
					// 重新赋值映射字段
					o.copyProjectionFields(workCompleted);
					// 加入到更新队列保证事务开启
					aeiObjects.getUpdateTaskCompleteds().add(o);
				});
				aeiObjects.getReads().stream().forEach(o -> {
					// 待阅的完成时间是不需要更新的
					o.setCompleted(true);
					o.setWorkCompleted(workCompleted.getId());
					// 重新赋值映射字段
					o.copyProjectionFields(workCompleted);
					// 加入到更新队列保证事务开启
					aeiObjects.getUpdateReads().add(o);
				});
				aeiObjects.getReadCompleteds().stream().forEach(o -> {
					// 已阅的完成时间是不需要更新的
					o.setCompleted(true);
					o.setWorkCompleted(workCompleted.getId());
					// 重新赋值映射字段
					o.copyProjectionFields(workCompleted);
					// 加入到更新队列保证事务开启
					aeiObjects.getUpdateReadCompleteds().add(o);
				});
				aeiObjects.getRecords().stream().forEach(o -> {
					o.setCompleted(true);
					o.setWorkCompleted(workCompleted.getId());
					aeiObjects.getUpdateRecords().add(o);
				});
				aeiObjects.getReviews().stream().forEach(o -> {
					o.setCompleted(true);
					o.setWorkCompleted(workCompleted.getId());
					o.setCompletedTime(workCompleted.getCompletedTime());
					o.setCompletedTimeMonth(workCompleted.getCompletedTimeMonth());
					// 重新赋值映射字段
					o.copyProjectionFields(workCompleted);
					// 加入到更新队列保证事务开启
					aeiObjects.getUpdateReviews().add(o);
				});
				aeiObjects.getWorkLogs().stream().forEach(o -> {
					o.setSplitting(false);
					o.setSplitToken("");
					o.getProperties().setSplitTokenList(new ArrayList<>());
					o.setSplitValue("");
					o.setCompleted(true);
					o.setWorkCompleted(workCompleted.getId());
					// 加入到更新队列保证事务开启
					aeiObjects.getUpdateWorkLogs().add(o);
					// 删除未连接的WorkLogd0431924-849f-4bf6-81b2-bfe997e62b7f
					if (BooleanUtils.isNotTrue(o.getConnected())) {
						aeiObjects.getDeleteWorkLogs().add(o);
					}
				});
				aeiObjects.getAttachments().stream().forEach(o -> {
					o.setCompleted(true);
					o.setWorkCompleted(workCompleted.getId());
					// 加入到更新队列保证事务开启
					aeiObjects.getUpdateAttachments().add(o);
				});
				// 已workCompleted数据为准进行更新
				aeiObjects.getData().setWork(workCompleted);
				aeiObjects.getData().setAttachmentList(aeiObjects.getAttachments());
				aeiObjects.getDeleteWorks().addAll(aeiObjects.getWorks());
				// 删除快照
				aeiObjects.getDeleteSnaps().addAll(aeiObjects.getSnaps());
				aeiObjects.getDeleteWorks().addAll(aeiObjects.getWorks());
				aeiObjects.commit();
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setId(workCompleted.getId());
				result.setData(wo);
				return result;
			}
		}

		private WorkCompleted createWorkCompleted(Work work, Activity activity) throws Exception {
			Date completedTime = new Date();
			Long duration = Config.workTime().betweenMinutes(work.getStartTime(), completedTime);
			WorkCompleted workCompleted = new WorkCompleted(work, completedTime, duration);
			workCompleted.setCompletedType(WorkCompleted.COMPLETEDTYPE_TERMINATE);
			if (null != activity) {
				workCompleted.setActivity(activity.getId());
				workCompleted.setActivityAlias(activity.getAlias());
				workCompleted.setActivityDescription(activity.getDescription());
				workCompleted.setActivityName(activity.getName());
			}
			return workCompleted;
		}
	}

	public static class Wo extends V2TerminateWo {

		private static final long serialVersionUID = 8964324166261918394L;

	}

}