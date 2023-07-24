package com.x.processplatform.assemble.bam.jaxrs.state;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;

class ActionSummary extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionSummary.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(ThisApplication.state.getSummary());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("应用数量")
		private Long applicationCount = 0L;

		@FieldDescribe("流程数量")
		private Long processCount = 0L;

		@FieldDescribe("待办数量")
		private Long taskCount = 0L;

		@FieldDescribe("已办数量")
		private Long taskCompletedCount = 0L;

		@FieldDescribe("待阅数量")
		private Long readCount = 0L;

		@FieldDescribe("已阅数量")
		private Long readCompletedCount = 0L;

		@FieldDescribe("工作实例数量")
		private Long workCount = 0L;

		@FieldDescribe("已完成工作数量")
		private Long workCompletedCount = 0L;

		@FieldDescribe("超时待办数量")
		private Long expiredTaskCount = 0L;

		@FieldDescribe("超时已办数量")
		private Long expiredTaskCompletedCount = 0L;

		@FieldDescribe("超时工作数量")
		private Long expiredWorkCount = 0L;

		@FieldDescribe("身份对象")
		private Long expiredWorkCompletedCount = 0L;

		public Long getApplicationCount() {
			return applicationCount;
		}

		public void setApplicationCount(Long applicationCount) {
			this.applicationCount = applicationCount;
		}

		public Long getProcessCount() {
			return processCount;
		}

		public void setProcessCount(Long processCount) {
			this.processCount = processCount;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Long getTaskCompletedCount() {
			return taskCompletedCount;
		}

		public void setTaskCompletedCount(Long taskCompletedCount) {
			this.taskCompletedCount = taskCompletedCount;
		}

		public Long getReadCount() {
			return readCount;
		}

		public void setReadCount(Long readCount) {
			this.readCount = readCount;
		}

		public Long getReadCompletedCount() {
			return readCompletedCount;
		}

		public void setReadCompletedCount(Long readCompletedCount) {
			this.readCompletedCount = readCompletedCount;
		}

		public Long getWorkCount() {
			return workCount;
		}

		public void setWorkCount(Long workCount) {
			this.workCount = workCount;
		}

		public Long getWorkCompletedCount() {
			return workCompletedCount;
		}

		public void setWorkCompletedCount(Long workCompletedCount) {
			this.workCompletedCount = workCompletedCount;
		}

		public Long getExpiredTaskCount() {
			return expiredTaskCount;
		}

		public void setExpiredTaskCount(Long expiredTaskCount) {
			this.expiredTaskCount = expiredTaskCount;
		}

		public Long getExpiredTaskCompletedCount() {
			return expiredTaskCompletedCount;
		}

		public void setExpiredTaskCompletedCount(Long expiredTaskCompletedCount) {
			this.expiredTaskCompletedCount = expiredTaskCompletedCount;
		}

		public Long getExpiredWorkCount() {
			return expiredWorkCount;
		}

		public void setExpiredWorkCount(Long expiredWorkCount) {
			this.expiredWorkCount = expiredWorkCount;
		}

		public Long getExpiredWorkCompletedCount() {
			return expiredWorkCompletedCount;
		}

		public void setExpiredWorkCompletedCount(Long expiredWorkCompletedCount) {
			this.expiredWorkCompletedCount = expiredWorkCompletedCount;
		}
	}

}
