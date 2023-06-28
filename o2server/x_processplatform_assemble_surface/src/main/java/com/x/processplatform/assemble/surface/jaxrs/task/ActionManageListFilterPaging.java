package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
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
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionManageListFilterPagingWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageListFilterPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListFilterPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
				Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
				List<Wo> wos = emc.fetchDescPaging(Task.class, Wo.copier, p, page, size, Task.startTime_FIELDNAME);
				result.setData(wos);
				result.setCount(emc.count(Task.class, p));
			} else {
				result.setData(new ArrayList<>());
				result.setCount(0L);
			}
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		List<String> personIds = business.organization().person().list(wi.getCredentialList());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.conjunction();
		p = predicateApplication(wi, cb, root, p);
		p = predicateProcess(business, wi, cb, root, p);
		p = predicateStringValue(wi, cb, root, p);
		p = predicateStartTime(wi, cb, root, p);
		p = predicateEndTime(wi, cb, root, p);
		p = predicatePerson(personIds, cb, root, p);
		p = predicateCreatorUnit(wi, cb, root, p);
		p = predicateWork(wi, cb, root, p);
		p = predicateJob(wi, cb, root, p);
		p = predicateStartTimeMonth(wi, cb, root, p);
		p = predicateActivityName(wi, cb, root, p);
		p = predicateExpireTime(wi, cb, root, p);
		p = predicateUrgeTime(wi, cb, root, p);
		p = predicateExcludeDraft(wi, cb, root, p);
		p = predicateKey(wi, cb, root, p);
		return p;
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

	private Predicate predicateStartTimeMonth(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Task_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		return p;
	}

	private Predicate predicateJob(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getJobList())) {
			p = cb.and(p, root.get(Task_.job).in(wi.getJobList()));
		}
		return p;
	}

	private Predicate predicateWork(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getWorkList())) {
			p = cb.and(p, root.get(Task_.work).in(wi.getWorkList()));
		}
		return p;
	}

	private Predicate predicateCreatorUnit(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Task_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		return p;
	}

	private Predicate predicatePerson(List<String> personIds, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (ListTools.isNotEmpty(personIds)) {
			p = cb.and(p, root.get(Task_.person).in(personIds));
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

	private Predicate predicateStringValue(Wi wi, CriteriaBuilder cb, Root<Task> root, Predicate p) {
		if (StringUtils.isNotBlank(wi.getStringValue01())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue01), wi.getStringValue01()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue02())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue02), wi.getStringValue02()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue03())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue03), wi.getStringValue03()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue04())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue04), wi.getStringValue04()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue05())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue05), wi.getStringValue05()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue06())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue06), wi.getStringValue06()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue07())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue07), wi.getStringValue07()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue08())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue08), wi.getStringValue08()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue09())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue09), wi.getStringValue09()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue10())) {
			p = cb.and(p, cb.equal(root.get(Task_.stringValue10), wi.getStringValue10()));
		}
		return p;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageListFilterPaging.Wi")
	public class Wi extends ActionManageListFilterPagingWi {

		private static final long serialVersionUID = -1353659669041503749L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageListFilterPaging.Wo")
	public static class Wo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, false), null);

	}

}
