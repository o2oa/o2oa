package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionReference extends ActionBase {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ReadNotExistedException(id);
			}
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("read", readOutCopier.copy(read));
			/** 装载所有的workLog */
			wrap.put("workLogList", this.listWorkLog(business, read));
			/** 装载后续的work */
			wrap.put("workList", this.listWork(business, read));
			/** 装载可能的workCompleted */
			wrap.put("workCompletedList", this.listWorkCompleted(business, read));
			result.setData(wrap);
		}
		return result;
	}

	private List<WrapOutWorkLog> listWorkLog(Business business, Read read) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.workLog().listWithJob(read.getJob());
		List<WorkLog> list = emc.list(WorkLog.class, ids);
		List<WrapOutWorkLog> os = WorkLogBuilder.complex(business, list);
		return os;
	}

	private List<WrapOutWork> listWork(Business business, Read read) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityTokenForward(read.getActivityToken());
		List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
			workIds.add(o.getWork());
		}
		List<WrapOutWork> works = workOutCopier.copy(business.entityManagerContainer().list(Work.class, workIds));
		return works;
	}

	private List<WrapOutWorkCompleted> listWorkCompleted(Business business, Read read) throws Exception {
		List<WrapOutWorkCompleted> list = new ArrayList<>();
		if (BooleanUtils.isTrue(read.getCompleted())) {
			WorkCompleted o = business.entityManagerContainer().find(read.getWorkCompleted(), WorkCompleted.class);
			if (null != o) {
				list.add(workCompletedOutCopier.copy(o));
			}
		}
		return list;
	}
}
