package com.x.query.assemble.surface.jaxrs.table;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;

class ActionListPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson, new Table())) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			EntityManager em = emc.get(Table.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			List<Wo> wos = emc.fetchDescPaging(Table.class, Wo.copier, p, page, size, Table.sequence_FIELDNAME);
			wos.stream().forEach(wo -> {
				try {
					Query query = emc.find(wo.getQuery(), Query.class);
					if (query != null) {
						wo.setQueryName(query.getName());
						wo.setQueryAlias(query.getAlias());
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			result.setData(wos);
			result.setCount(emc.count(Table.class, p));
			return result;
		}
	}

	public static class Wo extends Table {

		private static final long serialVersionUID = -2529024915990955519L;

		static WrapCopier<Table, Wo> copier = WrapCopierFactory.wo(Table.class, Wo.class,
				JpaObject.singularAttributeField(Table.class, true, true), null);

		@FieldDescribe("查询应用名称.")
		private String queryName;

		@FieldDescribe("查询应用别名.")
		private String queryAlias;

		public String getQueryName() {
			return queryName;
		}

		public void setQueryName(String queryName) {
			this.queryName = queryName;
		}

		public String getQueryAlias() {
			return queryAlias;
		}

		public void setQueryAlias(String queryAlias) {
			this.queryAlias = queryAlias;
		}
	}
}
