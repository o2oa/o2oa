package com.x.query.assemble.designer.jaxrs.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.View;
import com.x.query.core.entity.View_;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import com.x.query.core.express.plan.Runtime;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean idleName(Business business, View view) throws Exception {
		EntityManager em = business.entityManagerContainer().get(View.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<View> root = cq.from(View.class);
		Predicate p = cb.equal(root.get(View_.query), view.getQuery());
		p = cb.and(p, cb.equal(root.get(View_.name), view.getName()));
		p = cb.and(p, cb.notEqual(root.get(View_.id), view.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}

	protected boolean idleAlias(Business business, View view) throws Exception {
		EntityManager em = business.entityManagerContainer().get(View.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<View> root = cq.from(View.class);
		Predicate p = cb.equal(root.get(View_.query), view.getQuery());
		p = cb.and(p, cb.equal(root.get(View_.alias), view.getAlias()));
		p = cb.and(p, cb.notEqual(root.get(View_.id), view.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}

	protected Runtime runtime(EffectivePerson effectivePerson, Business business, View view,
			List<FilterEntry> filterList, Map<String, String> parameter, Integer count, boolean isBundle)
			throws Exception {
		Runtime runtime = new Runtime();
		runtime.person = effectivePerson.getDistinguishedName();
		runtime.identityList = business.organization().identity().listWithPerson(effectivePerson);
		List<String> list = new ArrayList<>();
		if (runtime.identityList != null) {
			for (String identity : runtime.identityList) {
				if (identity.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(identity, "@"));
				}
			}
			runtime.identityList.addAll(list);
			list.clear();
		}
		runtime.unitList = business.organization().unit().listWithPerson(effectivePerson);
		if (runtime.unitList != null) {
			for (String item : runtime.unitList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.unitList.addAll(list);
			list.clear();
		}
		runtime.unitAllList = business.organization().unit().listWithPersonSupNested(effectivePerson);
		if (runtime.unitAllList != null) {
			for (String item : runtime.unitAllList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.unitAllList.addAll(list);
			list.clear();
		}
		runtime.groupList = business.organization().group()
				.listWithPersonReference(ListTools.toList(effectivePerson.getDistinguishedName()), true, true, true);
		if (runtime.groupList != null) {
			for (String item : runtime.groupList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.groupList.addAll(list);
			list.clear();
		}
		runtime.roleList = business.organization().role().listWithPerson(effectivePerson);
		if (runtime.roleList != null) {
			for (String item : runtime.roleList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.roleList.addAll(list);
			list.clear();
		}
		runtime.parameter = parameter;
		runtime.filterList = filterList;
		runtime.count = this.getCount(view, count, isBundle);
		return runtime;
	}

	protected Integer getCount(View view, Integer count, boolean isBundle) {
		Integer viewCount = view.getCount();
		if (isBundle) {
			if (viewCount == null || viewCount < 1) {
				viewCount = View.MAX_COUNT;
			}
			return ((count == null) || (count < 1)) ? viewCount : count;
		} else {
			Integer wiCount = ((count == null) || (count < 1) || (count > View.MAX_COUNT)) ? View.MAX_COUNT : count;
			return NumberUtils.min(viewCount, wiCount);
		}
	}

	protected void setProcessEdition(Business business, ProcessPlatformPlan processPlatformPlan) throws Exception {
		if (!processPlatformPlan.where.processList.isEmpty()) {
			List<String> processIds = ListTools.extractField(processPlatformPlan.where.processList,
					Process.id_FIELDNAME, String.class, true, true);
			List<Process> processList = business.process().listObjectWithProcess(processIds, true);
			List<ProcessPlatformPlan.WhereEntry.ProcessEntry> listProcessEntry = gson.fromJson(gson.toJson(processList),
					new TypeToken<List<ProcessPlatformPlan.WhereEntry.ProcessEntry>>() {
					}.getType());
			if (!listProcessEntry.isEmpty()) {
				processPlatformPlan.where.processList = listProcessEntry;
			}
		}
	}

}
