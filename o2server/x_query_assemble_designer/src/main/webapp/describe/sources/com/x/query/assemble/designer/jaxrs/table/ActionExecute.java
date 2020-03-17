package com.x.query.assemble.designer.jaxrs.table;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionExecute extends BaseAction {

	ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement)
			throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			this.check(effectivePerson, business, table);
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<JpaObject>) Class.forName(dynamicEntity.className());
			EntityManager em = emc.get(cls);
			Query query = em.createQuery(wi.getData());
			Object data = null;
			if (StringUtils.equalsIgnoreCase(wi.getType(), Statement.TYPE_SELECT)) {
				if ((null != wi.getMaxResults()) && (wi.getMaxResults() > 0)) {
					query.setMaxResults(wi.getMaxResults());
				}
				if ((null != wi.getFirstResult()) && (wi.getFirstResult() > 0)) {
					query.setFirstResult(wi.getFirstResult());
				}
				data = query.getResultList();
			} else {
				emc.beginTransaction(cls);
				data = query.executeUpdate();
				emc.commit();
			}
			result.setData(data);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("类型")
		private String type;

		@FieldDescribe("jpql语句.")
		private String data;

		@FieldDescribe("返回最大结果集.")
		private Integer maxResults;

		@FieldDescribe("返回结果开始于.")
		private Integer firstResult;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public Integer getFirstResult() {
			return firstResult;
		}

		public void setFirstResult(Integer firstResult) {
			this.firstResult = firstResult;
		}

		public Integer getMaxResults() {
			return maxResults;
		}

		public void setMaxResults(Integer maxResults) {
			this.maxResults = maxResults;
		}

	}

}