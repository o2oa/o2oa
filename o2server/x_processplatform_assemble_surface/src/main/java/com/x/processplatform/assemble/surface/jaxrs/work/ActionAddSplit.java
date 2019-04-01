package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;

class ActionAddSplit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/* 校验work是否存在 */
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			if (StringUtils.isEmpty(wi.getSplitValue())) {
				throw new ExceptionEmptySplitValue(work.getId());
			}
			Manual manual = business.manual().pick(work.getActivity());
			if (null == manual || BooleanUtils.isFalse(manual.getAllowAddSplit())
					|| (!BooleanUtils.isTrue(work.getSplitting()))) {
				throw new ExceptionCannotAddSplit(work.getId());
			}

			List<WorkLog> workLogs = this.listWorkLog(business, work);

			WorkLogTree tree = new WorkLogTree(workLogs);

			WorkLog workLog = workLogs.stream().filter(o -> StringUtils.equals(o.getId(), wi.getWorkLog())).findFirst()
					.orElse(null);

			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}


			Wo wo = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("work", work.getId(), "add", "split"), wi).getData(Wo.class);

			result.setData(wo);
			return result;

		}
	}

	private List<WorkLog> listWorkLog(Business business, Work work) throws Exception {
		return business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob());
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("添加的拆分值.")
		private String splitValue;

		@FieldDescribe("拆分日志.")
		private String workLog;

		public String getSplitValue() {
			return splitValue;
		}

		public void setSplitValue(String splitValue) {
			this.splitValue = splitValue;
		}

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

	}

	public static class Wo extends WoId {
	}

}