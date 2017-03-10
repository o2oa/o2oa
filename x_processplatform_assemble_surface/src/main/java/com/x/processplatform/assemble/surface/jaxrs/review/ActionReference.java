package com.x.processplatform.assemble.surface.jaxrs.review;

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
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionReference extends ActionBase {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Review review = emc.find(id, Review.class);
			if (null == review) {
				throw new ReviewNotExistedException(id);
			}
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("review", reviewOutCopier.copy(review));
			wrap.put("workLogList", this.listWorkLog(business, review));
			wrap.put("workList", this.listWork(business, review));
			wrap.put("workCompletedList", this.listWorkCompleted(business, review));
			result.setData(wrap);
		}
		return result;
	}

	private List<WrapOutWorkLog> listWorkLog(Business business, Review review) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.workLog().listWithJob(review.getJob());
		return WorkLogBuilder.complex(business, emc.list(WorkLog.class, ids));
	}

	private List<WrapOutWork> listWork(Business business, Review review) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityTokenForward(review.getActivityToken());
		List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
			workIds.add(o.getWork());
		}
		List<WrapOutWork> works = workOutCopier.copy(business.entityManagerContainer().list(Work.class, workIds));
		return works;
	}

	private List<WrapOutWorkCompleted> listWorkCompleted(Business business, Review review) throws Exception {
		List<WrapOutWorkCompleted> list = workCompletedOutCopier
				.copy(business.workCompleted().listWithJobObject(review.getJob()));
		SortTools.asc(list, "createTime");
		return list;
	}
}
