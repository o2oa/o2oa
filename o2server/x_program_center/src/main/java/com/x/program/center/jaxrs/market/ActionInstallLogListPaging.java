package com.x.program.center.jaxrs.market;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.InstallLog;
import com.x.program.center.core.entity.InstallLog_;

class ActionInstallLogListPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = emc.fetchDescPaging(InstallLog.class, Wo.copier, p, page, size, InstallLog.createTime_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(InstallLog.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(InstallLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<InstallLog> root = cq.from(InstallLog.class);
		Predicate p = cb.conjunction();

		if(StringUtils.isNotEmpty(wi.getName())){
			String key = StringTools.escapeSqlLikeKey(wi.getName());
			if (StringUtils.isNotEmpty(key)) {
				p = cb.and(p,cb.like(root.get(InstallLog_.name), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
			}
		}

		if(StringUtils.isNotEmpty(wi.getStatus())){
			p = cb.and(p, cb.equal(root.get(InstallLog_.status), wi.getStatus()));
		}

		if(StringUtils.isNotEmpty(wi.getCategory())){
			p = cb.and(p, cb.equal(root.get(InstallLog_.category), wi.getCategory()));
		}

		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(InstallLog_.createTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(InstallLog_.createTime), DateTools.parse(wi.getEndTime())));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("名称")
		private String name;

		@FieldDescribe("分类")
		private String category;

		@FieldDescribe("状态:1(正常)|0(失效)")
		private String status;

		@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss")
		private String startTime;

		@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss")
		private String endTime;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
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
	}

	public static class Wo extends InstallLog {

		private static final long serialVersionUID = -2553925573532406246L;

		static WrapCopier<InstallLog, Wo> copier = WrapCopierFactory.wo(InstallLog.class, Wo.class,
				JpaObject.singularAttributeField(InstallLog.class, true, true), null);


	}

}
