package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionReference extends ActionBase {
	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new TaskCompletedNotExistedException(id);
			}
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("taskCompleted", taskCompletedOutCopier.copy(taskCompleted));
			wrap.put("workCompletedList", this.listWorkCompleted(business, taskCompleted));
			wrap.put("workLogList", this.listWorkLog(business, taskCompleted));
			wrap.put("workList", this.listWork(business, taskCompleted));
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

	private List<WrapOutWork> listWork(Business business, TaskCompleted taskCompleted) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityTokenForward(taskCompleted.getActivityToken());
		List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
			workIds.add(o.getWork());
		}
		List<WrapOutWork> works = workOutCopier.copy(business.entityManagerContainer().list(Work.class, workIds));
		return works;
	}

	private List<WrapOutWorkCompleted> listWorkCompleted(Business business, TaskCompleted taskCompleted)
			throws Exception {
		List<WrapOutWorkCompleted> list = workCompletedOutCopier
				.copy(business.workCompleted().listWithJobObject(taskCompleted.getJob()));
		SortTools.asc(list, "createTime");
		return list;
	}

}