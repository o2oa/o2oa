package com.x.cms.assemble.control.jaxrs.commend;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.DocumentCommend_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class ActionListPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(DocumentCommend.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DocumentCommend> cq = cb.createQuery(DocumentCommend.class);
			Root<DocumentCommend> root = cq.from(DocumentCommend.class);
			Predicate p = cb.conjunction();
			if (StringUtils.isNotBlank(wi.getDocumentId())){
				p = cb.and(p, root.get(DocumentCommend_.documentId).in(wi.getDocumentId()));
			}
			if (StringUtils.isNotBlank(wi.getCommendPerson())){
				p = cb.and(p, root.get(DocumentCommend_.commendPerson).in(wi.getCommendPerson()));
			}
			List<Wo> wos = emc.fetchDescPaging(DocumentCommend.class, Wo.copier, p, page, size, DocumentCommend.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(DocumentCommend.class, p));
			return result;
		}
	}

	public static class Wi extends DocumentCommend{

		private static final long serialVersionUID = 8042740393049682505L;

		static WrapCopier<Wi, DocumentCommend> copier = WrapCopierFactory.wi(Wi.class, DocumentCommend.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends DocumentCommend {

		private static final long serialVersionUID = -1828627584254370972L;

		static WrapCopier<DocumentCommend, Wo> copier = WrapCopierFactory.wo(DocumentCommend.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
