package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionReference extends ActionBase {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class, ExceptionWhen.not_found);
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("read", readOutCopier.copy(read));
			wrap.put("workLogList", this.listWorkLog(business, read));
			List<String> ids = business.workLog().listWithFromActivityTokenForward(read.getActivityToken());
			List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
			List<String> workCompletedIds = SetUniqueList.setUniqueList(new ArrayList<String>());
			for (WorkLog o : emc.list(WorkLog.class, ids)) {
				if (o.getCompleted()) {
					workCompletedIds.add(o.getWorkCompleted());
				} else {
					workIds.add(o.getWork());
				}
			}
			List<WrapOutWork> works = workOutCopier.copy(emc.list(Work.class, workIds));
			List<WrapOutWorkCompleted> workCompleteds = workCompletedOutCopier
					.copy(emc.list(WorkCompleted.class, workCompletedIds));
			wrap.put("workList", works);
			wrap.put("workCompletedList", workCompleteds);
			result.setData(wrap);
		}
		return result;
	}

	private List<WrapOutWorkLog> listWorkLog(Business business, Read read) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.workLog().listWithJob(read.getJob());
		return WorkLogBuilder.complex(business, emc.list(WorkLog.class, ids));
	}
}
