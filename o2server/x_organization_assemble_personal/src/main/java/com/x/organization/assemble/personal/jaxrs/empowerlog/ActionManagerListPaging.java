package com.x.organization.assemble.personal.jaxrs.empowerlog;

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
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.accredit.EmpowerLog;
import com.x.organization.core.entity.accredit.EmpowerLog_;

class ActionManagerListPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(EmpowerLog.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EmpowerLog> root = cq.from(EmpowerLog.class);
			Predicate p = cb.conjunction();
			if(effectivePerson.isManager()) {
				if (StringUtils.isNotEmpty(wi.getFromPerson())) {
					String key = "%" + StringTools.escapeSqlLikeKey(wi.getFromPerson()) + "%";
					p = cb.and(p, cb.like(root.get(EmpowerLog_.fromPerson), key, StringTools.SQL_ESCAPE_CHAR));
				}
			}else{
				p = cb.and(p, cb.equal(root.get(EmpowerLog_.fromPerson), effectivePerson.getDistinguishedName()));
			}
			if(DateTools.isDateTimeOrDate(wi.getStartTime())){
				p = cb.and(p, cb.greaterThan(root.get(EmpowerLog_.createTime), DateTools.parse(wi.getStartTime())));
			}
			if(DateTools.isDateTimeOrDate(wi.getEndTime())){
				p = cb.and(p, cb.lessThan(root.get(EmpowerLog_.createTime), DateTools.parse(wi.getEndTime())));
			}
			if (StringUtils.isNotEmpty(wi.getKey())) {
				String key = "%" + StringTools.escapeSqlLikeKey(wi.getKey()) + "%";
				p = cb.and(p, cb.like(root.get(EmpowerLog_.title), key, StringTools.SQL_ESCAPE_CHAR));
			}
			List<Wo> wos = emc.fetchDescPaging(EmpowerLog.class, Wo.copier, p, page, size,
					EmpowerLog.createTime_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(EmpowerLog.class, p));
			return result;
		}
	}

	public static class Wo extends EmpowerLog {

		private static final long serialVersionUID = 4279205128463146835L;

		static WrapCopier<EmpowerLog, Wo> copier = WrapCopierFactory.wi(EmpowerLog.class, Wo.class,
				JpaObject.singularAttributeField(EmpowerLog.class, true, true), null);

	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("授权人")
		private String fromPerson;

		@FieldDescribe("匹配关键字")
		private String key;

		@FieldDescribe("(授权创建时间)开始时间yyyy-MM-dd HH:mm:ss")
		private String startTime;

		@FieldDescribe("(授权创建时间)结束时间yyyy-MM-dd HH:mm:ss")
		private String endTime;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getFromPerson() {
			return fromPerson;
		}

		public void setFromPerson(String fromPerson) {
			this.fromPerson = fromPerson;
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

}
