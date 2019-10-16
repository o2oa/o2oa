package com.x.processplatform.assemble.surface.jaxrs.review;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class ActionListMyFilterPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);
			List<Review> os = this.list(effectivePerson, business, adjustPage, adjustPageSize, wi);
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			result.setCount(this.count(effectivePerson, business, wi));
			return result;
		}
	}

	private List<Review> list(EffectivePerson effectivePerson, Business business, Integer adjustPage,
			Integer adjustPageSize, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		List<String> person_ids = business.organization().person().list(wi.getCredentialList());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Review_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Review_.process).in(wi.getProcessList()));
		}
		if(DateTools.isDateTimeOrDate(wi.getStartTime())){
			p = cb.and(p, cb.greaterThan(root.get(Review_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if(DateTools.isDateTimeOrDate(wi.getEndTime())){
			p = cb.and(p, cb.lessThan(root.get(Review_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(person_ids)) {
			p = cb.and(p, root.get(Review_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Review_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Review_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Review_.currentActivityName).in(wi.getActivityNameList()));
		}
		if (ListTools.isNotEmpty(wi.getCompletedList())) {
			p = cb.and(p, root.get(Review_.completed).in(wi.getCompletedList()));
		}
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				key = StringUtils.replaceEach(key, new String[] { "?", "%" }, new String[] { "", "" });
				p = cb.and(p,
						cb.or(cb.like(root.get(Review_.title), "%" + key + "%"),
								cb.like(root.get(Review_.serial), "%" + key + "%"),
								cb.like(root.get(Review_.creatorPerson), "%" + key + "%"),
								cb.like(root.get(Review_.creatorUnit), "%" + key + "%")));
			}
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get(Review_.startTime)));
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	private Long count(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		List<String> person_ids = business.organization().person().list(wi.getCredentialList());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Review_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Review_.process).in(wi.getProcessList()));
		}
		if(DateTools.isDateTimeOrDate(wi.getStartTime())){
			p = cb.and(p, cb.greaterThan(root.get(Review_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if(DateTools.isDateTimeOrDate(wi.getEndTime())){
			p = cb.and(p, cb.lessThan(root.get(Review_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(person_ids)) {
			p = cb.and(p, root.get(Review_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Review_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Review_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Review_.currentActivityName).in(wi.getActivityNameList()));
		}
		if (ListTools.isNotEmpty(wi.getCompletedList())) {
			p = cb.and(p, root.get(Review_.completed).in(wi.getCompletedList()));
		}
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				key = StringUtils.replaceEach(key, new String[] { "?", "%" }, new String[] { "", "" });
				p = cb.and(p,
						cb.or(cb.like(root.get(Review_.title), "%" + key + "%"),
								cb.like(root.get(Review_.serial), "%" + key + "%"),
								cb.like(root.get(Review_.creatorPerson), "%" + key + "%"),
								cb.like(root.get(Review_.creatorUnit), "%" + key + "%")));
			}
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("应用")
		private List<String> applicationList;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss")
		private String startTime;

		@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss")
		private String endTime;

		@FieldDescribe("创建用户")
		private List<String> credentialList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("到达时间")
		private List<String> startTimeMonthList;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

		@FieldDescribe("工单完成状态true|false")
		private List<Boolean> completedList;

		@FieldDescribe("关键字")
		private String key;

		public List<String> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<String> applicationList) {
			this.applicationList = applicationList;
		}

		public List<String> getProcessList() {
			return processList;
		}

		public void setProcessList(List<String> processList) {
			this.processList = processList;
		}

		public List<String> getStartTimeMonthList() {
			return startTimeMonthList;
		}

		public void setStartTimeMonthList(List<String> startTimeMonthList) {
			this.startTimeMonthList = startTimeMonthList;
		}

		public List<String> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<String> activityNameList) {
			this.activityNameList = activityNameList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<String> getCreatorUnitList() {
			return creatorUnitList;
		}

		public void setCreatorUnitList(List<String> creatorUnitList) {
			this.creatorUnitList = creatorUnitList;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public List<String> getCredentialList() {
			return credentialList;
		}

		public void setCredentialList(List<String> credentialList) {
			this.credentialList = credentialList;
		}

		public List<Boolean> getCompletedList() {
			return completedList;
		}

		public void setCompletedList(List<Boolean> completedList) {
			this.completedList = completedList;
		}
	}

	public static class Wo extends Review {

		private static final long serialVersionUID = 4412958037130830411L;

		static WrapCopier<Review, Wo> copier = WrapCopierFactory.wo(Review.class, Wo.class,
				JpaObject.singularAttributeField(Review.class, true, true), null);

	}

}
