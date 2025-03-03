package com.x.general.assemble.control.jaxrs.invoice;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.TernaryManagement;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.general.assemble.control.Business;
import com.x.general.core.entity.Invoice;
import com.x.general.core.entity.Invoice_;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

class ActionListPaging extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		logger.debug("execute, person:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if(!effectivePerson.isManager() && !effectivePerson.isTernaryManager()){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(business, wi, effectivePerson);
			List<Wo> wos = emc.fetchDescPaging(Invoice.class, Wo.copier, p, page, size, Invoice.lastUpdateTime_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Invoice.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(Business business, Wi wi, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Invoice.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Invoice> cq = cb.createQuery(Invoice.class);
		Root<Invoice> root = cq.from(Invoice.class);
		Predicate p = cb.equal(root.get(Invoice_.person), effectivePerson.getDistinguishedName());
		if (StringUtils.isNoneBlank(wi.getNumber())) {
			p = cb.and(p, cb.equal(root.get(Invoice_.number), wi.getNumber()));
		}
		if (StringUtils.isNoneBlank(wi.getName())) {
			String key = StringTools.escapeSqlLikeKey(wi.getName());
			p = cb.and(p, cb.like(root.get(Invoice_.name), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("发票号码")
		private String number;

		@FieldDescribe("文件名称")
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}
	}

	public static class Wo extends Invoice {

		private static final long serialVersionUID = -4635222902589827154L;

		static WrapCopier<Invoice, Wo> copier = WrapCopierFactory.wo(Invoice.class, Wo.class,
				JpaObject.singularAttributeField(Invoice.class, true, true), null);

	}
}
