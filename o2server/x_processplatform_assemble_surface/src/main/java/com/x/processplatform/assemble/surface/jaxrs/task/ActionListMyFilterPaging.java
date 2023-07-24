package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import javax.persistence.EntityManager;
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
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionListMyFilterPagingWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListMyFilterPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListMyFilterPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);
			List<Task> os = this.list(effectivePerson, business, adjustPage, adjustPageSize, wi);
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			result.setCount(this.count(effectivePerson, business, wi));
			return result;
		}
	}

	private List<Task> list(EffectivePerson effectivePerson, Business business, Integer adjustPage,
			Integer adjustPageSize, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
		p = predicateApplication(wi, cb, root, p);
		p = predicateProcess(business, wi, cb, root, p);
		p = predicateStartTime(wi, cb, root, p);
		p = predicateEndTime(wi, cb, root, p);
		p = predicateCreatorUnit(wi, cb, root, p);
		p = predicateTimeMonth(wi, cb, root, p);
		p = predicateActivityName(wi, cb, root, p);
		p = predicateExpireTime(wi, cb, root, p);
		p = predicateUrgeTime(wi, cb, root, p);
		p = predicateExcludeDraft(wi, cb, root, p);
		p = predicateKey(wi, cb, root, p);
		cq.select(root).where(p).orderBy(cb.desc(root.get(Task_.startTime)));
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	private Predicate predicateKey(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				key = StringUtils.replaceEach(key, new String[] { "?", "%" }, new String[] { "", "" });
				p = cb.and(p,
						cb.or(cb.like(root.get(Task_.title), "%" + key + "%"),
								cb.like(root.get(Task_.opinion), "%" + key + "%"),
								cb.like(root.get(Task_.serial), "%" + key + "%"),
								cb.like(root.get(Task_.creatorPerson), "%" + key + "%"),
								cb.like(root.get(Task_.creatorUnit), "%" + key + "%")));
			}
		}
		return p;
	}

	private Predicate predicateExcludeDraft(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (BooleanUtils.isTrue(wi.getExcludeDraft())) {
			p = cb.and(p, cb.or(cb.isFalse(root.get(Task_.first)), cb.isNull(root.get(Task_.first)),
					cb.equal(root.get(Task_.workCreateType), Work.WORKCREATETYPE_ASSIGN)));
		}
		return p;
	}

	private Predicate predicateUrgeTime(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (StringUtils.isNotBlank(wi.getUrgeTime())) {
			int urgeTime = 0;
			try {
				urgeTime = Integer.parseInt(wi.getUrgeTime());
			} catch (NumberFormatException e) {
				LOGGER.error(e);
			}
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(Task_.urgeTime),
					DateTools.getAdjustTimeDay(null, 0, -urgeTime, 0, 0)));
		}
		return p;
	}

	private Predicate predicateExpireTime(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (StringUtils.isNotBlank(wi.getExpireTime())) {
			int expireTime = 0;
			try {
				expireTime = Integer.parseInt(wi.getExpireTime());
			} catch (NumberFormatException e) {
				LOGGER.error(e);
			}
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(Task_.expireTime),
					DateTools.getAdjustTimeDay(null, 0, -expireTime, 0, 0)));
		}
		return p;
	}

	private Predicate predicateActivityName(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Task_.activityName).in(wi.getActivityNameList()));
		}
		return p;
	}

	private Predicate predicateTimeMonth(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Task_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		return p;
	}

	private Predicate predicateCreatorUnit(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Task_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		return p;
	}

	private Predicate predicateEndTime(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) throws Exception {
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getEndTime()))) {
			p = cb.and(p, cb.lessThan(root.get(Task_.startTime), DateTools.parse(wi.getEndTime())));
		}
		return p;
	}

	private Predicate predicateStartTime(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) throws Exception {
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getStartTime()))) {
			p = cb.and(p, cb.greaterThan(root.get(Task_.startTime), DateTools.parse(wi.getStartTime())));
		}
		return p;
	}

	private Predicate predicateProcess(Business business, Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p)
			throws Exception {
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(Task_.process).in(wi.getProcessList()));
			} else {
				p = cb.and(p, root.get(Task_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		return p;
	}

	private Predicate predicateApplication(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Task_.application).in(wi.getApplicationList()));
		}
		return p;
	}

	private Long count(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
		p = predicateApplication(wi, cb, root, p);
		p = predicateProcess(business, wi, cb, root, p);
		p = predicateStartTime(wi, cb, root, p);
		p = predicateEndTime(wi, cb, root, p);
		p = predicateCreatorUnit(wi, cb, root, p);
		p = predicateTimeMonth(wi, cb, root, p);
		p = predicateActivityName(wi, cb, root, p);
		p = predicateExpireTime(wi, cb, root, p);
		p = predicateUrgeTime(wi, cb, root, p);
		p = predicateExcludeDraft(wi, cb, root, p);
		p = predicateKey(wi, cb, root, p);
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionListMyFilterPaging$Wi")
	public class Wi extends ActionListMyFilterPagingWi {

		private static final long serialVersionUID = 2367955871006172175L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionListMyFilterPaging$Wo")
	public static class Wo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, false), null);

	}

}