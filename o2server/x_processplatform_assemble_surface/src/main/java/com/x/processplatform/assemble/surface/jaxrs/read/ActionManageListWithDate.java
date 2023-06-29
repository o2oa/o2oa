package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageListWithDate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListWithDate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String date) throws Exception {

		LOGGER.debug("execute:{}, date:{}.", effectivePerson::getDistinguishedName, () -> date);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				if (DateTools.isDateTimeOrDate(date)) {
					Date startTime = DateTools.floorDate(DateTools.parse(date), 0);
					Date endTime = DateTools.getAdjustTimeDay(startTime, 1, 0, 0, 0);
					List<Read> os = this.list(business, startTime, endTime);
					List<Wo> wos = Wo.copier.copy(os);
					result.setData(wos);
					result.setCount((long) wos.size());
				}
			}
			return result;
		}
	}

	private List<Read> list(Business business, Date startTime, Date endTime) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Read> cq = cb.createQuery(Read.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.conjunction();

		if (startTime != null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Read_.createTime), startTime));
		}
		if (endTime != null) {
			p = cb.and(p, cb.lessThan(root.get(Read_.createTime), endTime));
		}

		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageListWithDate$Wo")
	public static class Wo extends Read {

		private static final long serialVersionUID = 8776369434319911308L;

		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class,
				JpaObject.singularAttributeField(Read.class, true, true), null);

	}

}
