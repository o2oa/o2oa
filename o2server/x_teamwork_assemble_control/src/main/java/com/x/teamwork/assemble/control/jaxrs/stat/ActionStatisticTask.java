package com.x.teamwork.assemble.control.jaxrs.stat;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.jaxrs.task.WrapInQueryTask;
import com.x.teamwork.core.entity.ProjectStatusEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author sword
 */
public class ActionStatisticTask extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStatisticTask.class);

	private static final Integer STAT_MODE_1 = 1;
	private static final Integer STAT_MODE_2 = 2;

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		if(wi.getStartTime() == null || wi.getEndTime() == null){
			throw new StatisticQueryException("统计周期不能空");
		}

		Wo wo = this.statTask(effectivePerson, wi);
		result.setData(wo);
		return result;
	}

	protected Wo statTask(EffectivePerson effectivePerson, Wi wi) throws Exception{
		Wo wo = new Wo();
		String person = effectivePerson.getDistinguishedName();
		List<String> publishDataList = ListTools.toList(DateTools.format(wi.getStartTime()), DateTools.format(wi.getEndTime()));
		WrapInQueryTask query = new WrapInQueryTask();
		query.setProjectList(wi.getProjectList());
		query.setPublishDateList(publishDataList);
		if(STAT_MODE_2.equals(wi.getMode())){
			query.setExecutor(person);
			person = "";
		}else{
			query.setQueryManager(true);
			Business business = new Business(null);
			person = business.isManager(effectivePerson) ? "" : person;
		}
		Long allCount = taskQueryService.countWithCondition(person, query.getQueryFilter());
		wo.setAllCount(allCount);

		query.setWorkStatus(ProjectStatusEnum.CANCELED.getValue());
		Long deleteCount = taskQueryService.countWithCondition(person, query.getQueryFilter());
		wo.setDeleteCount(deleteCount);

		query.setWorkStatus(ProjectStatusEnum.DELAY.getValue());
		Long delayCount = taskQueryService.countWithCondition(person, query.getQueryFilter());
		wo.setDelayCount(delayCount);

		query.setWorkStatus(ProjectStatusEnum.COMPLETED.getValue());
		query.setOverTime(true);
		Long completedOverTimeCount = taskQueryService.countWithCondition(person, query.getQueryFilter());
		wo.setCompletedOverTimeCount(completedOverTimeCount);

		query.setOverTime(false);
		Long completedNormalCount = taskQueryService.countWithCondition(person, query.getQueryFilter());
		wo.setCompletedNormalCount(completedNormalCount);

		query.setWorkStatus(null);
		query.setOverTime(true);
		Long overTimeCount = taskQueryService.countWithCondition(person, query.getQueryFilter());
		wo.setOverTimeCount(overTimeCount);
		Long processingCount = allCount - completedOverTimeCount - completedNormalCount - deleteCount - delayCount;

		wo.setProcessingCount(processingCount);
		return wo;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("统计维度：1、我管理的(默认)|2、我负责的.")
		private Integer mode = STAT_MODE_1;

		@FieldDescribe("项目Id列表.")
		private List<String> projectList;

		@FieldDescribe("统计任务的开始时间.")
		private Date startTime;

		@FieldDescribe("统计任务的结束时间.")
		private Date endTime;

		public Integer getMode() {
			return mode;
		}

		public void setMode(Integer mode) {
			this.mode = mode;
		}

		public List<String> getProjectList() {
			return projectList;
		}

		public void setProjectList(List<String> projectList) {
			this.projectList = projectList;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}
	}

	public static class Wo{
		@FieldDescribe("所有任务数量")
		private Long allCount = 0L;

		@FieldDescribe("逾期完成任务数量")
		private Long completedOverTimeCount = 0L;

		@FieldDescribe("按时完成任务数量")
		private Long completedNormalCount = 0L;

		@FieldDescribe("进行中的任务数量")
		private Long processingCount = 0L;

		@FieldDescribe("已搁置的任务数量")
		private Long delayCount = 0L;

		@FieldDescribe("已取消的任务数量")
		private Long deleteCount = 0L;

		@FieldDescribe("逾期的任务数量")
		private Long overTimeCount = 0L;

		public Long getAllCount() {
			return allCount;
		}

		public void setAllCount(Long allCount) {
			this.allCount = allCount;
		}

		public Long getCompletedOverTimeCount() {
			return completedOverTimeCount;
		}

		public void setCompletedOverTimeCount(Long completedOverTimeCount) {
			this.completedOverTimeCount = completedOverTimeCount;
		}

		public Long getCompletedNormalCount() {
			return completedNormalCount;
		}

		public void setCompletedNormalCount(Long completedNormalCount) {
			this.completedNormalCount = completedNormalCount;
		}

		public Long getProcessingCount() {
			return processingCount;
		}

		public void setProcessingCount(Long processingCount) {
			this.processingCount = processingCount;
		}

		public Long getDelayCount() {
			return delayCount;
		}

		public void setDelayCount(Long delayCount) {
			this.delayCount = delayCount;
		}

		public Long getDeleteCount() {
			return deleteCount;
		}

		public void setDeleteCount(Long deleteCount) {
			this.deleteCount = deleteCount;
		}

		public Long getOverTimeCount() {
			return overTimeCount;
		}

		public void setOverTimeCount(Long overTimeCount) {
			this.overTimeCount = overTimeCount;
		}
	}

}
