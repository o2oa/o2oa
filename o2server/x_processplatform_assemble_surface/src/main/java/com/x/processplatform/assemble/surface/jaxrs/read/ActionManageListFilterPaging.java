package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.express.assemble.surface.jaxrs.read.ActionManageListFilterPagingWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageListFilterPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListFilterPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
				Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
				Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
				List<Wo> wos = emc.fetchDescPaging(Read.class, Wo.copier, p, page, size, Read.startTime_FIELDNAME);
				result.setData(wos);
				result.setCount(emc.count(Read.class, p));
			} else {
				result.setData(new ArrayList<Wo>());
				result.setCount(0L);
			}
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		List<String> person_ids = business.organization().person().list(wi.getCredentialList());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Read> cq = cb.createQuery(Read.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Read_.application).in(wi.getApplicationList()));
		}
		if (StringUtils.isNotBlank(wi.getPerson())) {
			p = cb.and(p, cb.equal(root.get(Read_.person), wi.getPerson()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue01())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue01), wi.getStringValue01()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue02())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue02), wi.getStringValue02()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue03())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue03), wi.getStringValue03()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue04())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue04), wi.getStringValue04()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue05())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue05), wi.getStringValue05()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue06())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue06), wi.getStringValue06()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue07())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue07), wi.getStringValue07()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue08())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue08), wi.getStringValue08()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue09())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue09), wi.getStringValue09()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue10())) {
			p = cb.and(p, cb.equal(root.get(Read_.stringValue10), wi.getStringValue10()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(Read_.process).in(wi.getProcessList()));
			} else {
				p = cb.and(p, root.get(Read_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Read_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Read_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(person_ids)) {
			p = cb.and(p, root.get(Read_.person).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Read_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getWorkList())) {
			p = cb.and(p, root.get(Read_.work).in(wi.getWorkList()));
		}
		if (ListTools.isNotEmpty(wi.getJobList())) {
			p = cb.and(p, root.get(Read_.job).in(wi.getJobList()));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Read_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Read_.activityName).in(wi.getActivityNameList()));
		}
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				key = StringUtils.replaceEach(key, new String[] { "?", "%" }, new String[] { "", "" });
				p = cb.and(p,
						cb.or(cb.like(root.get(Read_.title), "%" + key + "%"),
								cb.like(root.get(Read_.opinion), "%" + key + "%"),
								cb.like(root.get(Read_.serial), "%" + key + "%"),
								cb.like(root.get(Read_.creatorPerson), "%" + key + "%"),
								cb.like(root.get(Read_.creatorUnit), "%" + key + "%")));
			}
		}

		return p;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageListFilterPaging$Wi")
	public class Wi extends ActionManageListFilterPagingWi {

		private static final long serialVersionUID = -3825407993922560607L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageListFilterPaging$Wo")
	public static class Wo extends Read {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class,
				JpaObject.singularAttributeField(Read.class, true, true), null);

	}

}
