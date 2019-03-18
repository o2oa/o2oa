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
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;

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
			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob());
			WorkLog currentWorkLog = workLogs.stream()
					.filter(o -> StringUtils.equals(o.getFromActivityToken(), work.getActivityToken())).findFirst()
					.orElse(null);
			if (null == currentWorkLog) {
				throw new ExceptionCurrentWorkLogNotFound(work.getId());
			}
			WorkLogTree tree = new WorkLogTree(workLogs);
			WorkLogTree.Node currentNode = tree.find(currentWorkLog);

			WorkLogTree.Nodes upManualNodes = currentNode.upTo(ActivityType.manual, ActivityType.agent,
					ActivityType.choice, ActivityType.delay, ActivityType.delay, ActivityType.embed,
					ActivityType.invoke, ActivityType.message, ActivityType.parallel, ActivityType.service,
					ActivityType.split);

			if (upManualNodes.isEmpty()) {
				throw new ExceptionUpManualNotFound(work.getId());
			}

			for (WorkLogTree.Node o : upManualNodes) {

				if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
						effectivePerson.getDistinguishedName(), TaskCompleted.activityToken_FIELDNAME,
						o.getWorkLog().getFromActivityToken()) > 0) {
					break;
				}

				throw new ExceptionAccessDenied(effectivePerson);
			}

			Wo wo = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("work", work.getId(), "add", "split"), wi).getData(Wo.class);

			result.setData(wo);
			return result;

		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("添加的拆分值.")
		private String splitValue;

		public String getSplitValue() {
			return splitValue;
		}

		public void setSplitValue(String splitValue) {
			this.splitValue = splitValue;
		}

	}

	public static class Wo extends WoId {
	}

}