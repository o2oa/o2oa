package com.x.query.assemble.surface.jaxrs.importmodel;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.ImportRecord;
import com.x.query.core.entity.ImportRecordItem;

class ActionGetRecordStatus extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetRecordStatus.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String recordId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			ImportRecord record = emc.find(recordId, ImportRecord.class);
			if(record == null){
				throw new ExceptionEntityNotExist(recordId, ImportRecord.class);
			}
			Wo wo = new Wo();
			wo.setCount(record.getCount());
			wo.setStatus(record.getStatus());
			wo.setDistribution(record.getDistribution());
			Long executeCount = emc.countEqual(ImportRecordItem.class, ImportRecordItem.recordId_FIELDNAME, record.getId());
			Long failCount = emc.countEqualAndEqual(ImportRecordItem.class, ImportRecordItem.recordId_FIELDNAME, record.getId(),
					ImportRecordItem.status_FIELDNAME, ImportRecordItem.STATUS_FAILED);
			wo.setExecuteCount(executeCount);
			wo.setFailCount(failCount);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("当前状态.")
		private String status;

		@FieldDescribe("总数量.")
		private Integer count;

		@FieldDescribe("已执行数量.")
		private Long executeCount;

		@FieldDescribe("失败数量.")
		private Long failCount;

		@FieldDescribe("导入结果描述.")
		private String distribution;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Long getExecuteCount() {
			return executeCount;
		}

		public void setExecuteCount(Long executeCount) {
			this.executeCount = executeCount;
		}

		public Long getFailCount() {
			return failCount;
		}

		public void setFailCount(Long failCount) {
			this.failCount = failCount;
		}

		public String getDistribution() {
			return distribution;
		}

		public void setDistribution(String distribution) {
			this.distribution = distribution;
		}
	}
}
