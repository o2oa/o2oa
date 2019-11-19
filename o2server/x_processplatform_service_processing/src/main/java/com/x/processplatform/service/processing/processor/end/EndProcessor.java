package com.x.processplatform.service.processing.processor.end;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class EndProcessor extends AbstractEndProcessor {

	private static Logger logger = LoggerFactory.getLogger(EndProcessor.class);

	public EndProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, End end) throws Exception {
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, End end) throws Exception {
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, End end) throws Exception {

		List<Work> results = new ArrayList<>();

		Work other = aeiObjects.getWorks().stream().filter(o -> {
			return o != aeiObjects.getWork();
		}).sorted(Comparator.comparing(Work::getCreateTime)).findFirst().orElse(null);

		if (null != other) {
			aeiObjects.getUpdateWorks().add(other);
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			this.mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeRead(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReview(aeiObjects, aeiObjects.getWork(), other);
			this.mergeHint(aeiObjects, aeiObjects.getWork(), other);
			this.mergeAttachment(aeiObjects, aeiObjects.getWork(), other);
			this.mergeWorkLog(aeiObjects, aeiObjects.getWork(), other);
			aeiObjects.getWorkLogs().stream()
					.filter(p -> StringUtils.equals(p.getFromActivityToken(), aeiObjects.getWork().getActivityToken()))
					.forEach(obj -> {
						aeiObjects.getDeleteWorkLogs().add(obj);
					});
		} else {
			WorkCompleted workCompleted = this.createWorkCompleted(aeiObjects.getWork());
			workCompleted.setAllowRollback(end.getAllowRollback());
			aeiObjects.getCreateWorkCompleteds().add(workCompleted);
			aeiObjects.getTasks().stream().forEach(o -> aeiObjects.getDeleteTasks().add(o));
			aeiObjects.getHints().stream().forEach(o -> aeiObjects.getDeleteHints().add(o));
			aeiObjects.getTaskCompleteds().stream().forEach(o -> {
				/* 已办的完成时间是不需要更新的 */
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				/* 重新赋值映射字段 */
				o.copyProjectionFields(workCompleted);
				/* 加入到更新队列保证事务开启 */
				aeiObjects.getUpdateTaskCompleteds().add(o);
			});
			aeiObjects.getReads().stream().forEach(o -> {
				/* 待阅的完成时间是不需要更新的 */
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				/* 重新赋值映射字段 */
				o.copyProjectionFields(workCompleted);
				/* 加入到更新队列保证事务开启 */
				aeiObjects.getUpdateReads().add(o);
			});
			aeiObjects.getReadCompleteds().stream().forEach(o -> {
				/* 已阅的完成时间是不需要更新的 */
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				/* 重新赋值映射字段 */
				o.copyProjectionFields(workCompleted);
				/* 加入到更新队列保证事务开启 */
				aeiObjects.getUpdateReadCompleteds().add(o);
			});
			aeiObjects.getReviews().stream().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				o.setCompletedTime(workCompleted.getCompletedTime());
				o.setCompletedTimeMonth(workCompleted.getCompletedTimeMonth());
				/* 重新赋值映射字段 */
				o.copyProjectionFields(workCompleted);
				/* 加入到更新队列保证事务开启 */
				aeiObjects.getUpdateReviews().add(o);
			});
			aeiObjects.getWorkLogs().stream().forEach(o -> {
				o.setSplitting(false);
				o.setSplitToken("");
				o.setSplitTokenList(new ArrayList<String>());
				o.setSplitValue("");
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				/* 加入到更新队列保证事务开启 */
				aeiObjects.getUpdateWorkLogs().add(o);
				/* 删除未连接的WorkLog */
				if (BooleanUtils.isNotTrue(o.getConnected())) {
					aeiObjects.getDeleteWorkLogs().add(o);
				}
			});
			aeiObjects.getAttachments().stream().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				/* 加入到更新队列保证事务开启 */
				aeiObjects.getUpdateAttachments().add(o);
			});
			/* 已workCompleted数据为准进行更新 */
			aeiObjects.getData().setWork(workCompleted);
			aeiObjects.getData().setAttachmentList(aeiObjects.getAttachments());
			aeiObjects.getDeleteWorks().addAll(aeiObjects.getWorks());
		}

		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, End end) throws Exception {
		if (StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScript())
				|| StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScriptText())) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
			scriptHelper.eval(aeiObjects.getWork().getApplication(),
					Objects.toString(aeiObjects.getProcess().getAfterEndScript()),
					Objects.toString(aeiObjects.getProcess().getAfterEndScriptText()));
		}
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, End end) throws Exception {
		return new ArrayList<Route>();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, End end) throws Exception {
	}

	/* 根据work和data创建最终保存的workCompleted */
	private WorkCompleted createWorkCompleted(Work work) throws Exception {
		Date completedTime = new Date();
		Long duration = Config.workTime().betweenMinutes(work.getStartTime(), completedTime);
		String formString = "";
		String formMobileString = "";
		if (StringUtils.isNotEmpty(work.getForm())) {
			Form form = this.entityManagerContainer().fetch(work.getForm(), Form.class,
					ListTools.toList(Form.data_FIELDNAME, Form.mobileData_FIELDNAME));
			if (null != form) {
				formString = form.getData();
				formMobileString = form.getMobileData();
			}
		}
		WorkCompleted workCompleted = new WorkCompleted(work, completedTime, duration, formString, formMobileString);
		return workCompleted;
	}
}