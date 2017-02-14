package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionReference extends ActionBase {
	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class, ExceptionWhen.not_found);
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("taskCompleted", taskCompletedOutCopier.copy(taskCompleted));
			wrap.put("workLogList", this.listWorkLog(business, taskCompleted));
			List<WrapOutWork> works = new ArrayList<WrapOutWork>();
			wrap.put("workList", works);
			List<WrapOutWorkCompleted> workCompleteds = new ArrayList<WrapOutWorkCompleted>();
			wrap.put("workCompletedList", workCompleteds);
			List<String> ids = business.workLog().listWithFromActivityTokenForward(taskCompleted.getActivityToken());
			List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
			List<String> workCompletedIds = SetUniqueList.setUniqueList(new ArrayList<String>());
			for (WorkLog o : emc.list(WorkLog.class, ids)) {
				if (o.getCompleted()) {
					workCompletedIds.add(o.getWorkCompleted());
				} else {
					workIds.add(o.getWork());
				}
			}
			for (String str : workCompletedIds) {
				WorkCompleted o = emc.find(str, WorkCompleted.class);
				if (null != o) {
					workCompleteds.add(workCompletedOutCopier.copy(o));
				}
			}
			for (String str : workIds) {
				Work o = emc.find(str, Work.class);
				if (null != o) {
					works.add(workOutCopier.copy(o));
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<WrapOutWorkLog> listWorkLog(Business business, TaskCompleted taskCompleted) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.workLog().listWithJob(taskCompleted.getJob());
		List<WorkLog> os = emc.list(WorkLog.class, ids);
		return WorkLogBuilder.complex(business, os);
	}

}