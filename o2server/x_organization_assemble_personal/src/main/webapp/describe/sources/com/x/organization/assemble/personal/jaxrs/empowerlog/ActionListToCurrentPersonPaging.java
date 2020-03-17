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
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.entity.accredit.EmpowerLog;
import com.x.organization.core.entity.accredit.EmpowerLog_;

class ActionListToCurrentPersonPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(EmpowerLog.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EmpowerLog> root = cq.from(EmpowerLog.class);
			Predicate p = cb.equal(root.get(EmpowerLog_.toPerson), effectivePerson.getDistinguishedName());
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

		static WrapCopier<EmpowerLog, Wo> copier = WrapCopierFactory.wo(EmpowerLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("匹配关键字")
		private String key;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

}
