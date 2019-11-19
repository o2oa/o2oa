package com.x.processplatform.assemble.surface.jaxrs.review;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;

class ActionFilterAttribute extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Wo wo = new Wo();
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			wo.getApplicationList().addAll(this.listApplicationPair(business, effectivePerson, p));
			wo.getProcessList().addAll(this.listProcessPair(business, effectivePerson, p));
			wo.getCreatorUnitList().addAll(this.listCreatorUnitPair(business, effectivePerson, p));
			wo.getStartTimeMonthList().addAll(this.listStartTimeMonthPair(business, effectivePerson, p));
			wo.getCompletedTimeMonthList().addAll(this.listCompletedTimeMonthPair(business, effectivePerson, p));
			result.setData(wo);
			return result;
		}
	}

	public class Wi extends FilterWi {
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("可选择的应用")
		private List<NameValueCountPair> applicationList = new ArrayList<>();

		@FieldDescribe("可选择的流程")
		private List<NameValueCountPair> processList = new ArrayList<>();

		@FieldDescribe("可选择的组织")
		private List<NameValueCountPair> creatorUnitList = new ArrayList<>();

		@FieldDescribe("可选择的开始月份")
		private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();

		@FieldDescribe("限制结束月份范围")
		private List<NameValueCountPair> completedTimeMonthList = new ArrayList<>();

		public List<NameValueCountPair> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<NameValueCountPair> applicationList) {
			this.applicationList = applicationList;
		}

		public List<NameValueCountPair> getProcessList() {
			return processList;
		}

		public void setProcessList(List<NameValueCountPair> processList) {
			this.processList = processList;
		}

		public List<NameValueCountPair> getCreatorUnitList() {
			return creatorUnitList;
		}

		public void setCreatorUnitList(List<NameValueCountPair> creatorUnitList) {
			this.creatorUnitList = creatorUnitList;
		}

		public List<NameValueCountPair> getStartTimeMonthList() {
			return startTimeMonthList;
		}

		public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
			this.startTimeMonthList = startTimeMonthList;
		}

		public List<NameValueCountPair> getCompletedTimeMonthList() {
			return completedTimeMonthList;
		}

		public void setCompletedTimeMonthList(List<NameValueCountPair> completedTimeMonthList) {
			this.completedTimeMonthList = completedTimeMonthList;
		}

	}

	private List<NameValueCountPair> listApplicationPair(Business business, EffectivePerson effectivePerson,
			Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Review> root = cq.from(Review.class);
		Path<String> pathApplication = root.get(Review_.application);
		Path<String> pathApplicationName = root.get(Review_.applicationName);
		cq.multiselect(pathApplication, pathApplicationName, cb.count(root)).where(predicate).groupBy(pathApplication);
		List<Tuple> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> list = new ArrayList<>();
		NameValueCountPair pair = null;
		for (Tuple o : os) {
			pair = new NameValueCountPair();
			pair.setName(o.get(pathApplicationName));
			pair.setValue(o.get(pathApplication));
			pair.setCount(o.get(2, Long.class));
			list.add(pair);
		}
		list = list.stream().sorted((o1, o2) -> Objects.toString(o1.getName()).compareTo(o2.getName().toString()))
				.collect(Collectors.toList());
		return list;
	}

	private List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson,
			Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Review> root = cq.from(Review.class);
		Path<String> pathProcess = root.get(Review_.process);
		Path<String> pathProcessName = root.get(Review_.processName);
		cq.multiselect(pathProcess, pathProcessName, cb.count(root)).where(predicate).groupBy(pathProcess);
		List<Tuple> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> list = new ArrayList<>();
		NameValueCountPair pair = null;
		for (Tuple o : os) {
			pair = new NameValueCountPair();
			pair.setName(o.get(pathProcessName));
			pair.setValue(o.get(pathProcess));
			pair.setCount(o.get(2, Long.class));
			list.add(pair);
		}
		list = list.stream().sorted((o1, o2) -> Objects.toString(o1.getName()).compareTo(o2.getName().toString()))
				.collect(Collectors.toList());
		return list;
	}

	private List<NameValueCountPair> listCreatorUnitPair(Business business, EffectivePerson effectivePerson,
			Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Review> root = cq.from(Review.class);
		Path<String> pathCreatorUnit = root.get(Review_.creatorUnit);
		cq.multiselect(pathCreatorUnit, cb.count(root)).where(predicate).groupBy(pathCreatorUnit);
		List<Tuple> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> list = new ArrayList<>();
		NameValueCountPair pair = null;
		for (Tuple o : os) {
			pair = new NameValueCountPair();
			pair.setValue(o.get(pathCreatorUnit));
			pair.setName(OrganizationDefinition.name(o.get(pathCreatorUnit)));
			pair.setCount(o.get(1, Long.class));
			list.add(pair);
		}
		list = list.stream().sorted((o1, o2) -> Objects.toString(o1.getName()).compareTo(o2.getName().toString()))
				.collect(Collectors.toList());
		return list;
	}

	private List<NameValueCountPair> listStartTimeMonthPair(Business business, EffectivePerson effectivePerson,
			Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Review> root = cq.from(Review.class);
		Path<String> pathStartTimeMonth = root.get(Review_.startTimeMonth);
		cq.multiselect(pathStartTimeMonth, cb.count(root)).where(predicate).groupBy(pathStartTimeMonth);
		List<Tuple> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> list = new ArrayList<>();
		NameValueCountPair pair = null;
		for (Tuple o : os) {
			pair = new NameValueCountPair();
			pair.setValue(o.get(pathStartTimeMonth));
			pair.setName(pair.getValue());
			pair.setCount(o.get(1, Long.class));
			list.add(pair);
		}
		list = list.stream().sorted((o1, o2) -> Objects.toString(o1.getName()).compareTo(o2.getName().toString()))
				.collect(Collectors.toList());
		return list;
	}

	private List<NameValueCountPair> listCompletedTimeMonthPair(Business business, EffectivePerson effectivePerson,
			Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Review> root = cq.from(Review.class);
		Path<String> pathCompletedTimeMonth = root.get(Review_.completedTimeMonth);
		cq.multiselect(pathCompletedTimeMonth, cb.count(root))
				.where(cb.and(cb.equal(root.get(Review_.completed), true), predicate)).groupBy(pathCompletedTimeMonth);
		List<Tuple> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> list = new ArrayList<>();
		NameValueCountPair pair = null;
		for (Tuple o : os) {
			pair = new NameValueCountPair();
			pair.setValue(o.get(pathCompletedTimeMonth));
			pair.setName(pair.getValue());
			pair.setCount(o.get(1, Long.class));
			list.add(pair);
		}
		list = list.stream().sorted((o1, o2) -> Objects.toString(o1.getName()).compareTo(o2.getName().toString()))
				.collect(Collectors.toList());
		return list;
	}
}