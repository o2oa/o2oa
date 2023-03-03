package com.x.query.assemble.surface.jaxrs.table;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

class ActionListRowPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListRowPaging.class);

	ActionResult<List<?>> execute(EffectivePerson effectivePerson, String tableFlag, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<?>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Table table = emc.flag(tableFlag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.readable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			EntityManager em = emc.get(cls);

			List<String> fields = JpaObject.singularAttributeField(cls, true, false);
			if(!fields.contains(JpaObject.sequence_FIELDNAME)) {
				fields.add(JpaObject.sequence_FIELDNAME);
			}

			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			String orderBy = Table.sequence_FIELDNAME;
			if(StringUtils.isNotBlank(wi.getOrderBy()) && fields.contains(wi.getOrderBy())){
				orderBy = wi.getOrderBy();
			}

			List<?> wos;
			if(wi.getDescOrder()) {
				wos = emc.fetchDescPaging(cls, fields, p, page, size, orderBy);
			}else{
				wos = emc.fetchAscPaging(cls, fields, p, page, size, orderBy);
			}

			result.setData(wos);
			result.setCount(emc.count(cls, p));
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -297439043438152109L;

		@FieldDescribe("排序字段，默认根据创建时间排序")
		private String orderBy;

		@FieldDescribe("是否倒叙排序,默认倒叙")
		private Boolean descOrder;

		public String getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}

		public Boolean getDescOrder() {
			return descOrder == null ? Boolean.TRUE : descOrder;
		}

		public void setDescOrder(Boolean descOrder) {
			this.descOrder = descOrder;
		}
	}

}
