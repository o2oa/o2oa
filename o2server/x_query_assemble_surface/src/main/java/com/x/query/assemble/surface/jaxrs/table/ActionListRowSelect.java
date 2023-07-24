package com.x.query.assemble.surface.jaxrs.table;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Table;

class ActionListRowSelect extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListRowSelect.class);

	ActionResult<List<?>> execute(EffectivePerson effectivePerson, String tableFlag, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, table:{}.", effectivePerson::getDistinguishedName, () -> tableFlag);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<?>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Table table = emc.flag(tableFlag, Table.class);
			Business business = new Business(emc);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.readable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson, table);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			EntityManager em = emc.get(cls);
			String sql = "SELECT o FROM " + cls.getName() + " o";
			if (StringUtils.isNotBlank(wi.getWhere())) {
				sql += " where " + wi.getWhere();
			}
			if (StringUtils.isNotBlank(wi.getOrderBy())) {
				sql += " order by " + wi.getOrderBy();
			}
			List<?> list;
			if (wi.getSize() != null && wi.getSize() > 0) {
				list = em.createQuery(sql).setMaxResults(wi.getSize()).getResultList();
			} else {
				list = em.createQuery(sql).getResultList();
			}
			result.setData(list);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 5494319398614663331L;

		@FieldDescribe("查询条件，格式为jpql语法,o.name='zhangsan'，允许为空")
		private String where;
		@FieldDescribe("排序条件，格式为：o.updateTime desc，允许为空")
		private String orderBy;
		@FieldDescribe("返回结果集数量,允许为空")
		private Integer size;

		public String getWhere() {
			return where;
		}

		public void setWhere(String where) {
			this.where = where;
		}

		public String getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}
	}

}
