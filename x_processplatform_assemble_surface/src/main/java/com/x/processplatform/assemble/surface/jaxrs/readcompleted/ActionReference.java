package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionReference extends ActionBase {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			ReadCompleted readCompleted = emc.find(id, ReadCompleted.class);
			if (null == readCompleted) {
				throw new ReadCompletedNotExistedException(id);
			}
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("readCompleted", readCompletedOutCopier.copy(readCompleted));
			wrap.put("workLogList", this.listWorkLog(business, readCompleted));
			wrap.put("workList", this.listWork(business, readCompleted));
			wrap.put("workCompletedList", this.listWorkCompleted(business, readCompleted));
			result.setData(wrap);
			return result;
		}
	}

	private List<WrapOutWorkLog> listWorkLog(Business business, ReadCompleted readCompleted) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.workLog().listWithJob(readCompleted.getJob());
		List<WorkLog> os = emc.list(WorkLog.class, ids);
		return WorkLogBuilder.complex(business, os);
	}

	private List<WrapOutWork> listWork(Business business, ReadCompleted readCompleted) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityTokenForward(readCompleted.getActivityToken());
		List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
			workIds.add(o.getWork());
		}
		List<WrapOutWork> works = workOutCopier.copy(business.entityManagerContainer().list(Work.class, workIds));
		return works;
	}

	private List<WrapOutWorkCompleted> listWorkCompleted(Business business, ReadCompleted readCompleted)
			throws Exception {
		List<WrapOutWorkCompleted> list = new ArrayList<>();
		if (BooleanUtils.isTrue(readCompleted.getCompleted())) {
			WorkCompleted o = business.entityManagerContainer().find(readCompleted.getWorkCompleted(),
					WorkCompleted.class);
			if (null != o) {
				list.add(workCompletedOutCopier.copy(o));
			}
		}
		return list;
	}

}