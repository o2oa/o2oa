package com.x.program.center.jaxrs.market;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.enums.CommonStatus;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.Application_;
import com.x.program.center.core.entity.InstallLog;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ActionListPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			String orderBy = Application.createTime_FIELDNAME;
			if(StringUtils.isNotEmpty(wi.getOrderBy())){
				orderBy = wi.getOrderBy();
			}
			List<Wo> wos = new ArrayList<>();
			if(BooleanUtils.isTrue(wi.getAsc())){
				wos = emc.fetchAscPaging(Application.class, Wo.copier, p, page, size, orderBy);
			}else {
				wos = emc.fetchDescPaging(Application.class, Wo.copier, p, page, size, orderBy);
			}
			for(Wo wo : wos){
				InstallLog installLog = emc.find(wo.getId(), InstallLog.class);
				if(installLog!=null && CommonStatus.VALID.getValue().equals(installLog.getStatus())){
					wo.setInstalledVersion(installLog.getVersion());
				}else{
					wo.setInstalledVersion("");
				}
			}
			result.setData(wos);
			result.setCount(emc.count(Application.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.conjunction();

		if(StringUtils.isNotEmpty(wi.getName())){
			String key = StringTools.escapeSqlLikeKey(wi.getName());
			if (StringUtils.isNotEmpty(key)) {
				p = cb.and(p,cb.like(root.get(Application_.name), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
			}
		}

		if(StringUtils.isNotEmpty(wi.getCategory())){
			p = cb.and(p, cb.equal(root.get(Application_.category), wi.getCategory()));
		}

		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Application_.createTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Application_.createTime), DateTools.parse(wi.getEndTime())));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("名称")
		private String name;

		@FieldDescribe("分类")
		private String category;

		@FieldDescribe("排序字段：createTime（创建时间，默认）|orderNumber（排序）|recommend（推荐指数）")
		private String orderBy;

		@FieldDescribe("是否是升序排序：true|false")
		private Boolean isAsc;

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

		public String getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}

		public Boolean getAsc() {
			return isAsc;
		}

		public void setAsc(Boolean asc) {
			isAsc = asc;
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

	public static class Wo extends Application {

		private static final long serialVersionUID = 9206739553467260926L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class,
				JpaObject.singularAttributeField(Application.class, true, false), Arrays.asList("abort", "installSteps", "describe"));

		@FieldDescribe("已安装的版本，空表示未安装")
		private String installedVersion;

		public String getInstalledVersion() {
			return installedVersion;
		}

		public void setInstalledVersion(String installedVersion) {
			this.installedVersion = installedVersion;
		}


	}

}
