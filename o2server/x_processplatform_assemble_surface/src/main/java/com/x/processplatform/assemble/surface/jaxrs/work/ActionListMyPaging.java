package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
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
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionListMyPagingWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListMyPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListMyPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = emc.fetchDescPaging(Work.class, Wo.copier, p, this.adjustPage(page), this.adjustSize(size),
					JpaObject.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Work.class, p));
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionListMyPaging$Wi")
	public static class Wi extends ActionListMyPagingWi {

		private static final long serialVersionUID = -1849285234970846023L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionListMyPaging$Wo")
	public static class Wo extends Work {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class,
				JpaObject.singularAttributeField(Work.class, true, true), null);

	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Work_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(Work_.process).in(wi.getProcessList()));
			} else {
				p = cb.and(p, root.get(Work_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Work_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Work_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Work_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Work_.activityName).in(wi.getActivityNameList()));
		}
		if (null != wi.getWorkThroughManual()) {
			p = cb.and(p, cb.equal(root.get(Work_.workThroughManual), wi.getWorkThroughManual()));
		}
		if (null != wi.getDataChanged()) {
			p = cb.and(p, cb.equal(root.get(Work_.dataChanged), wi.getDataChanged()));
		}
		String key = StringTools.escapeSqlLikeKey(wi.getKey());
		if (StringUtils.isNotEmpty(key)) {
			key = "%" + key + "%";
			p = cb.and(p, cb.or(cb.like(root.get(Work_.title), key), cb.like(root.get(Work_.serial), key)));
		}
		return p;
	}

}
