package com.x.processplatform.service.processing.processor.end;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.server.Config;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.WorkDataHelper;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class EndProcessor extends AbstractProcessor {

	private static Logger logger = LoggerFactory.getLogger(EndProcessor.class);

	public EndProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		List<String> workIds = this.business().work().listWithJob(work.getJob());
		/* 如果还有多个副本没有到达end节点那么不能路由 */
		if (!this.checkAllRelativeArrivedEndActivity(workIds)) {
			logger.debug("work title:{}, id:{}, has {} split work with same job not all arrvie end activity.",
					work.getTitle(), work.getId(), workIds.size());
			return results;
		} else {
			logger.debug(
					"work title:{}, id:{}, has {} split work with same job all arrvie end activity, will convert to workCompleted.",
					work.getTitle(), work.getId(), workIds.size());
			this.entityManagerContainer().beginTransaction(Task.class);
			this.entityManagerContainer().beginTransaction(TaskCompleted.class);
			this.entityManagerContainer().beginTransaction(Read.class);
			this.entityManagerContainer().beginTransaction(ReadCompleted.class);
			this.entityManagerContainer().beginTransaction(Review.class);
			this.entityManagerContainer().beginTransaction(Attachment.class);
			this.entityManagerContainer().beginTransaction(WorkLog.class);
			this.entityManagerContainer().beginTransaction(Work.class);
			this.entityManagerContainer().beginTransaction(WorkCompleted.class);
			/* 删除所有待办 */
			this.clearTaskWithJob(work.getJob());
			WorkCompleted workCompleted = createWorkCompleted(work);
			this.entityManagerContainer().persist(workCompleted, CheckPersistType.all);
			this.updateTaskCompleted(work.getJob(), workCompleted.getId());
			this.updateRead(work.getJob(), workCompleted.getId());
			this.updateReadCompleted(work.getJob(), workCompleted.getId());
			this.updateReview(work.getJob(), workCompleted.getId());
			this.updateWorkLog(work.getJob(), workCompleted.getId());
			this.updateAttachment(work.getJob(), workCompleted.getId());
			/** 标记数据已经完成,使用workDataHelper的地方不需要在重新开启事务 */
			WorkDataHelper workDataHelper = new WorkDataHelper(this.entityManagerContainer(), work);
			workDataHelper.completed(workCompleted.getCompletedTime());
			this.entityManagerContainer().delete(Work.class, workIds);
			this.entityManagerContainer().commit();
			return results;
		}
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		logger.debug("work title:{}, id:{}, inquire route return empty.", work.getTitle(), work.getId());
		return new ArrayList<Route>();
	}

	private boolean checkAllRelativeArrivedEndActivity(List<String> ids) throws Exception {
		for (Work o : this.entityManagerContainer().fetchAttribute(ids, Work.class, "activityType")) {
			if (!o.getActivityType().equals(ActivityType.end)) {
				return false;
			}
		}
		return true;
	}

	private void clearTaskWithJob(String job) throws Exception {
		/* 删除可能的待办 */
		this.entityManagerContainer().delete(Task.class, this.business().task().listWithJob(job));
	}

	private WorkCompleted createWorkCompleted(Work work) throws Exception {
		WorkCompleted workCompleted = new WorkCompleted();
		work.copyTo(workCompleted, JpaObject.ID_DISTRIBUTEFACTOR);
		workCompleted.setCompletedTime(new Date());
		workCompleted.setDuration(
				Config.workTime().betweenMinutes(workCompleted.getStartTime(), workCompleted.getCompletedTime()));
		if ((null != workCompleted.getExpireTime())
				&& (workCompleted.getExpireTime().before(workCompleted.getCompletedTime()))) {
			workCompleted.setExpired(true);
		} else {
			workCompleted.setExpired(false);
		}
		workCompleted.setWork(work.getId());
		/* 将Form内容进行保存 */
		if (StringUtils.isNotEmpty(work.getForm())) {
			Form form = this.entityManagerContainer().fetchAttribute(work.getForm(), Form.class, "data", "mobileData");
			workCompleted.setForm(work.getForm());
			workCompleted.setFormData(StringUtils.isNotEmpty(form.getData()) ? form.getData() : form.getMobileData());
			workCompleted.setFormMobileData(
					StringUtils.isNotEmpty(form.getMobileData()) ? form.getMobileData() : form.getData());
		}
		return workCompleted;
	}

	private void updateTaskCompleted(String job, String workCompletedId) throws Exception {
		/* 标记TaskCompleted指向WorkCompleted */
		for (TaskCompleted o : this.entityManagerContainer().list(TaskCompleted.class,
				this.business().taskCompleted().listWithJob(job))) {
			o.setCompleted(true);
			o.setWorkCompleted(workCompletedId);
		}
	}

	private void updateRead(String job, String workCompletedId) throws Exception {
		for (Read o : this.entityManagerContainer().list(Read.class, this.business().read().listWithJob(job))) {
			o.setCompleted(true);
			o.setWorkCompleted(workCompletedId);
		}
	}

	private void updateReadCompleted(String job, String workCompletedId) throws Exception {
		for (ReadCompleted o : this.entityManagerContainer().list(ReadCompleted.class,
				this.business().readCompleted().listWithJob(job))) {
			o.setCompleted(true);
			o.setWorkCompleted(workCompletedId);
		}
	}

	private void updateReview(String job, String workCompletedId) throws Exception {
		for (Review o : this.entityManagerContainer().list(Review.class, this.business().review().listWithJob(job))) {
			o.setCompleted(true);
			o.setWorkCompleted(workCompletedId);
		}
	}

	private void updateWorkLog(String job, String workCompletedId) throws Exception {
		for (WorkLog o : this.entityManagerContainer().list(WorkLog.class,
				this.business().workLog().listWithJob(job))) {
			/* 清空split值，不需要保存 */
			o.setSplitting(false);
			o.setSplitToken("");
			o.setSplitTokenList(new ArrayList<String>());
			o.setSplitValue("");
			o.setCompleted(true);
			o.setWorkCompleted(workCompletedId);
		}
	}

	private void updateAttachment(String job, String workCompletedId) throws Exception {
		for (Attachment o : this.entityManagerContainer().list(Attachment.class,
				this.business().attachment().listWithJob(job))) {
			o.setCompleted(true);
		}
	}
}