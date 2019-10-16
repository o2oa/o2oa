package com.x.processplatform.service.processing.schedule;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WorkDataHelper;

public class Reorganize extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Reorganize.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TimeStamp stamp = new TimeStamp();
			Business business = new Business(emc);
			Integer clearDraft = this.clearDraft(business);
			Integer clearOrphanWorkCount = this.clearOrphanWork(business);
			Integer rerouteWorkActivityNotExistedCount = this.rerouteWorkActivityNotExisted(business);
			Integer clearOrphanTaskCount = this.clearOrphanTask(business);
			Integer trigerWorkCount = this.triggerDetainedWork(business);
			logger.print("删除未保存数据的草稿工作 {}, 删除孤立工作 {}, 删除活动错误工作 {},  删除孤立待办 {}, 触发滞留工作 {}, 耗时: {}.", clearDraft,
					clearOrphanWorkCount, rerouteWorkActivityNotExistedCount, clearOrphanTaskCount, trigerWorkCount,
					stamp.consumingMilliseconds());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	/*
	 * 删除草稿状态下的文件,对于草稿的判断是没有人工处理过的work(dataChanged ==false),同时流程中有人工处理环节的.
	 */

	private Integer clearDraft(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids_process = emc.select(Manual.class, Manual.process_FIELDNAME, String.class);
		ids_process = ListTools.trim(ids_process, true, true);
		List<String> ids = emc.idsEqualAndIn(Work.class, Work.dataChanged_FIELDNAME, false, Work.process_FIELDNAME,
				ids_process);
		Date cutoff = DateTools.fromNowMinutes(-60);
		Integer count = 0;
		for (String id : ids) {
			Work work = emc.find(id, Work.class);
			if (null != work && work.getUpdateTime().before(cutoff)) {
				WorkDataHelper workDataHelper = new WorkDataHelper(emc, work);
				/* 数据也为空 */
				if (workDataHelper.get().emptySet()) {
					logger.print("删除未保存数据的草稿工作, 标题: {},  流程: {}, 应用: {}, id: {}.", work.getTitle(),
							work.getProcessName(), work.getApplicationName(), work.getId());
					ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", work.getId()));
					count++;
				}
			}
		}
		logger.print("共删除 {} 个未保存数据的草稿工作, 耗时: {}.", count, stamp.consumingMilliseconds());
		return count;
	}

	private Integer clearOrphanTask(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids_none_application = emc.idsNotIn(Task.class, Task.application_FIELDNAME,
				emc.ids(Application.class));
		List<String> ids_none_process = emc.idsNotIn(Task.class, Task.process_FIELDNAME, emc.ids(Process.class));
		List<String> ids_none_agent = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Agent.class),
				Task.activityType_FIELDNAME, ActivityType.agent);
		List<String> ids_none_begin = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Begin.class),
				Task.activityType_FIELDNAME, ActivityType.begin);
		List<String> ids_none_cancel = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Cancel.class),
				Task.activityType_FIELDNAME, ActivityType.cancel);
		List<String> ids_none_choice = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Choice.class),
				Task.activityType_FIELDNAME, ActivityType.choice);
		List<String> ids_none_delay = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Delay.class),
				Task.activityType_FIELDNAME, ActivityType.delay);
		List<String> ids_none_embed = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Embed.class),
				Task.activityType_FIELDNAME, ActivityType.embed);
		List<String> ids_none_end = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(End.class),
				Task.activityType_FIELDNAME, ActivityType.end);
		List<String> ids_none_invoke = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Invoke.class),
				Task.activityType_FIELDNAME, ActivityType.invoke);
		List<String> ids_none_manual = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Manual.class),
				Task.activityType_FIELDNAME, ActivityType.manual);
		List<String> ids_none_merge = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Merge.class),
				Task.activityType_FIELDNAME, ActivityType.merge);
		List<String> ids_none_message = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME,
				emc.ids(Message.class), Task.activityType_FIELDNAME, ActivityType.message);
		List<String> ids_none_parallel = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME,
				emc.ids(Parallel.class), Task.activityType_FIELDNAME, ActivityType.parallel);
		List<String> ids_none_service = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME,
				emc.ids(Service.class), Task.activityType_FIELDNAME, ActivityType.service);
		List<String> ids_none_split = emc.idsEqualAndNotIn(Task.class, Task.activity_FIELDNAME, emc.ids(Split.class),
				Task.activityType_FIELDNAME, ActivityType.split);
		List<String> ids = ListUtils
				.sum(ListUtils
						.sum(ListUtils
								.sum(ListUtils
										.sum(ListUtils.sum(
												ListUtils.sum(
														ListUtils.sum(
																ListUtils.sum(ListUtils.sum(
																		ListUtils.sum(ListUtils.sum(ListUtils.sum(
																				ListUtils.sum(ListUtils.sum(
																						ListUtils.sum(ids_none_process,
																								ids_none_application),
																						ids_none_agent),
																						ids_none_begin),
																				ids_none_cancel), ids_none_choice),
																				ids_none_delay),
																		ids_none_embed), ids_none_end),
																ids_none_invoke),
														ids_none_manual),
												ids_none_merge), ids_none_message),
										ids_none_parallel),
								ids_none_service),
						ids_none_split);
		for (String id : ids) {
			Task task = emc.find(id, Task.class);
			if (null != task) {
				logger.print("删除孤立待办, 用户: {}, 标题: {}, id: {}.", task.getPerson(), task.getTitle(), task.getId());
				emc.beginTransaction(Task.class);
				emc.remove(task);
				emc.commit();
				MessageFactory.task_delete(task);
			}
		}
		logger.print("共删除 {} 个孤立待办, 耗时: {}.", ids.size(), stamp.consumingMilliseconds());
		return ids.size();
	}

	private Integer clearOrphanWork(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids_none_application = emc.idsNotIn(Work.class, Work.application_FIELDNAME,
				emc.ids(Application.class));
		List<String> ids_none_process = emc.idsNotIn(Work.class, Work.process_FIELDNAME, emc.ids(Process.class));
		List<String> ids = ListUtils.sum(ids_none_application, ids_none_process);
		for (String id : ids) {
			Work work = emc.find(id, Work.class);
			if (null != work) {
				logger.print("删除孤立工作, 标题: {},  流程: {}, 应用: {}, id: {}.", work.getTitle(), work.getProcessName(),
						work.getApplicationName(), work.getId());
				ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", work.getId()));
			}
		}
		logger.print("共删除 {} 个孤立工作, 耗时: {}.", ids.size(), stamp.consumingMilliseconds());
		return ids.size();
	}

	private Integer rerouteWorkActivityNotExisted(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids_none_agent = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Agent.class),
				Work.activityType_FIELDNAME, ActivityType.agent);
		List<String> ids_none_begin = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Begin.class),
				Work.activityType_FIELDNAME, ActivityType.begin);
		List<String> ids_none_cancel = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Cancel.class),
				Work.activityType_FIELDNAME, ActivityType.cancel);
		List<String> ids_none_choice = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Choice.class),
				Work.activityType_FIELDNAME, ActivityType.choice);
		List<String> ids_none_delay = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Delay.class),
				Work.activityType_FIELDNAME, ActivityType.delay);
		List<String> ids_none_embed = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Embed.class),
				Work.activityType_FIELDNAME, ActivityType.embed);
		List<String> ids_none_end = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(End.class),
				Work.activityType_FIELDNAME, ActivityType.end);
		List<String> ids_none_invoke = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Invoke.class),
				Work.activityType_FIELDNAME, ActivityType.invoke);
		List<String> ids_none_manual = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Manual.class),
				Work.activityType_FIELDNAME, ActivityType.manual);
		List<String> ids_none_merge = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Merge.class),
				Work.activityType_FIELDNAME, ActivityType.merge);
		List<String> ids_none_message = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME,
				emc.ids(Message.class), Work.activityType_FIELDNAME, ActivityType.message);
		List<String> ids_none_parallel = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME,
				emc.ids(Parallel.class), Work.activityType_FIELDNAME, ActivityType.parallel);
		List<String> ids_none_service = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME,
				emc.ids(Service.class), Work.activityType_FIELDNAME, ActivityType.service);
		List<String> ids_none_split = emc.idsEqualAndNotIn(Work.class, Work.activity_FIELDNAME, emc.ids(Split.class),
				Work.activityType_FIELDNAME, ActivityType.split);
		List<String> ids = ListUtils
				.sum(ListUtils.sum(
						ListUtils
								.sum(ListUtils
										.sum(ListUtils
												.sum(ListUtils.sum(
														ListUtils
																.sum(ListUtils.sum(ListUtils.sum(
																		ListUtils.sum(
																				ListUtils.sum(ListUtils.sum(
																						ListUtils.sum(ids_none_agent,
																								ids_none_begin),
																						ids_none_cancel),
																						ids_none_choice),
																				ids_none_delay),
																		ids_none_embed), ids_none_end),
																		ids_none_invoke),
														ids_none_manual), ids_none_merge),
												ids_none_message),
										ids_none_parallel),
						ids_none_service), ids_none_split);
		Integer count = 0;
		for (String id : ids) {
			Work work = emc.find(id, Work.class);
			if (null != work) {
				Process process = business.element().get(work.getProcess(), Process.class);
				if (null != process) {
					Begin begin = business.element().getBeginWithProcess(process.getId());
					if (null != begin) {
						logger.print("调度活动错误工作, 标题: {}, 活动: {}, id: {}.", work.getTitle(), work.getActivityName(),
								work.getId());
						emc.beginTransaction(Work.class);
						work.setActivity(begin.getId());
						work.setActivityAlias(begin.getAlias());
						work.setActivityDescription(begin.getDescription());
						work.setActivityName(begin.getName());
						work.setActivityType(begin.getActivityType());
						emc.commit();
						ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
								Applications.joinQueryUri("work", work.getId(), "reroute", "activity", begin.getId()),
								null).getData(WoId.class);
						count++;
					} else {
						logger.print("调度活动错误工作失败, 无法找到开始节点, 标题: {}, 活动: {}, id: {}.", work.getTitle(),
								work.getActivityName(), work.getId());
					}
				}
			}
		}
		logger.print("共 {} 个活动错误工作, 调度 {} 个, 耗时:{}.", ids.size(), count, stamp.consumingMilliseconds());
		return ids.size();
	}

	private Integer triggerDetainedWork(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = this.triggerDetainedWork_list(business);
		for (String id : ids) {
			Work work = emc.find(id, Work.class);
			if (null != work) {
				try {
					logger.debug("触发滞留工作, 标题: {}, id: {}.", work.getTitle(), work.getId());
					ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", work.getId(), "processing"), new ProcessingAttributes());
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		List<String> ids_after = this.triggerDetainedWork_list(business);
		logger.print("共发现 {} 个滞留工作, 触发后剩余 {} 个, 滞留阈值: {} 分钟, 耗时: {}.", ids.size(), ids_after.size(),
				Config.processPlatform().getReorganize().getTriggerAfterMinutes(), stamp.consumingMilliseconds());
		return ids.size();
	}

	private List<String> triggerDetainedWork_list(Business business) throws Exception {
		Date date = new Date();
		date = DateUtils.addMinutes(date, -Config.processPlatform().getReorganize().getTriggerAfterMinutes());
		return business.entityManagerContainer().idsLessThan(Work.class, Work.activityArrivedTime_FIELDNAME, date);
	}

}
