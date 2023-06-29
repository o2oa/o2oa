package com.x.processplatform.assemble.surface.jaxrs.task;

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
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageListWithDateHour extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListWithDateHour.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String date, Integer hour, Boolean isExcludeDraft)
			throws Exception {
		LOGGER.debug("execute:{}, date:{}, hour:{}, isExcludeDraft:{}.", effectivePerson::getDistinguishedName,
				() -> date, () -> hour, () -> isExcludeDraft);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))
					&& (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(date)) && hour >= 0 && hour < 24)) {
				Date startTime = DateTools.getAdjustTimeDay(DateTools.floorDate(DateTools.parse(date), 0), 0, hour, 0,
						0);
				Date endTime = DateTools.getAdjustTimeDay(startTime, 0, 1, 0, 0);
				List<Task> os = this.list(business, startTime, endTime, isExcludeDraft);
				List<Wo> wos = Wo.copier.copy(os);
				result.setData(wos);
				result.setCount((long) wos.size());
			}
			return result;
		}
	}

	private List<Task> list(Business business, Date startTime, Date endTime, Boolean isExcludeDraft) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.conjunction();

		if (startTime != null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(JpaObject_.createTime), startTime));
		}
		if (endTime != null) {
			p = cb.and(p, cb.lessThan(root.get(JpaObject_.createTime), endTime));
		}
		if (BooleanUtils.isTrue(isExcludeDraft)) {
			p = cb.and(p, cb.or(cb.isFalse(root.get(Task_.first)), cb.isNull(root.get(Task_.first)),
					cb.equal(root.get(Task_.workCreateType), Work.WORKCREATETYPE_ASSIGN)));
		}

		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageListWithDateHour.Wo")
	public static class Wo extends Task {

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, true), null);

	}

}
