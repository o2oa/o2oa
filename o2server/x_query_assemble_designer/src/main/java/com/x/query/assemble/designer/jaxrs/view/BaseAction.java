package com.x.query.assemble.designer.jaxrs.view;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.View;
import com.x.query.core.entity.View_;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import com.x.query.core.express.plan.Runtime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

abstract class BaseAction extends StandardJaxrsAction {

	private static final CacheCategory userCache = new CacheCategory(View.class, Person.class, Unit.class,
			Group.class, Role.class);

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
		CacheKey cacheKey = new CacheKey("runtime_person", effectivePerson.getDistinguishedName());
		Optional<?> optional = CacheManager.get(userCache, cacheKey);
		if (optional.isPresent()) {
			Runtime cacheRuntime = (Runtime) optional.get();
			runtime.identityList = cacheRuntime.identityList;
			runtime.unitList = cacheRuntime.unitList;
			runtime.unitAllList = cacheRuntime.unitAllList;
			runtime.groupList = cacheRuntime.groupList;
			runtime.roleList = cacheRuntime.roleList;
		}else {
			runtime.identityList = business.organization().identity()
					.listWithPerson(effectivePerson);
			List<String> list = new ArrayList<>();
			if (runtime.identityList != null && View.TYPE_CMS.equals(view.getType())) {
				for (String identity : runtime.identityList) {
					if (identity.contains("@")) {
						list.add(StringUtils.substringAfter(identity, "@"));
					}
				}
				runtime.identityList.addAll(list);
				list.clear();
			}
			runtime.unitList = business.organization().unit().listWithPerson(effectivePerson);
			if (runtime.unitList != null && View.TYPE_CMS.equals(view.getType())) {
				for (String item : runtime.unitList) {
					if (item.contains("@")) {
						list.add(StringUtils.substringAfter(item, "@"));
					}
				}
				runtime.unitList.addAll(list);
				list.clear();
			}
			runtime.unitAllList = business.organization().unit()
					.listWithPersonSupNested(effectivePerson);
			if (runtime.unitAllList != null && View.TYPE_CMS.equals(view.getType())) {
				for (String item : runtime.unitAllList) {
					if (item.contains("@")) {
						list.add(StringUtils.substringAfter(item, "@"));
					}
				}
				runtime.unitAllList.addAll(list);
				list.clear();
			}
			runtime.groupList = business.organization().group()
					.listWithPersonReference(
							ListTools.toList(effectivePerson.getDistinguishedName()), true, true,
							true);
			if (runtime.groupList != null && View.TYPE_CMS.equals(view.getType())) {
				for (String item : runtime.groupList) {
					if (item.contains("@")) {
						list.add(StringUtils.substringAfter(item, "@"));
					}
				}
				runtime.groupList.addAll(list);
				list.clear();
			}
			runtime.roleList = business.organization().role().listWithPerson(effectivePerson);
			if (runtime.roleList != null && View.TYPE_CMS.equals(view.getType())) {
				for (String item : runtime.roleList) {
					if (item.contains("@")) {
						list.add(StringUtils.substringAfter(item, "@"));
					}
				}
				runtime.roleList.addAll(list);
				list.clear();
			}

			CacheManager.put(userCache, cacheKey, runtime);
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

	protected void setProcessEdition(ProcessPlatformPlan processPlatformPlan) throws Exception {
		List<Process> processList = new ArrayList<>();
		if (!processPlatformPlan.where.processList.isEmpty()) {
			List<String> processIds = ListTools.extractField(processPlatformPlan.where.processList,
					JpaObject.id_FIELDNAME, String.class, true, true);

			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				processList.addAll(business.process().listObjectWithProcess(processIds, true));
			}
		}
		if(!processPlatformPlan.where.applicationList.isEmpty()){
			List<String> appIds = ListTools.extractField(processPlatformPlan.where.applicationList,
					JpaObject.id_FIELDNAME, String.class, true, true);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				processList.addAll(business.process().listObjectWithApp(appIds));
			}
		}
		processList = processList.stream().distinct().collect(Collectors.toList());
		processPlatformPlan.where.processList = gson.fromJson(gson.toJson(processList),
				new TypeToken<List<ProcessPlatformPlan.WhereEntry.ProcessEntry>>() {
				}.getType());

	}

}
