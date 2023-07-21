package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

class ActionManageListWithDateHour extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String date, Integer hour)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "",""))) {
				if(DateTools.isDateTimeOrDate(date) && hour>=0 && hour<24){
					Date startTime = DateTools.getAdjustTimeDay(DateTools.floorDate(DateTools.parse(date), 0),
							0, hour, 0, 0);
					Date endTime = DateTools.getAdjustTimeDay(startTime, 0, 1, 0, 0);
					List<TaskCompleted> os = this.list(business, startTime, endTime);
					List<Wo> wos = Wo.copier.copy(os);
					result.setData(wos);
					result.setCount((long)wos.size());
				}
			}
			return result;
		}
	}

	private List<TaskCompleted> list(Business business, Date startTime, Date endTime) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.conjunction();

		if (startTime != null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(TaskCompleted_.createTime), startTime));
		}
		if (endTime != null) {
			p = cb.and(p, cb.lessThan(root.get(TaskCompleted_.createTime), endTime));
		}

		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wo extends TaskCompleted {

		static WrapCopier<TaskCompleted, Wo> copier = WrapCopierFactory.wo(TaskCompleted.class, Wo.class,
				JpaObject.singularAttributeField(TaskCompleted.class, true, true), null);

	}

}
