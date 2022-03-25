package com.x.cms.assemble.control.jaxrs.document;

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
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentViewRecord;
import com.x.cms.core.entity.DocumentViewRecord_;

class ActionQueryListViewRecordPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = wos = emc.fetchDescPaging(DocumentViewRecord.class, Wo.copier, p, page, size, DocumentViewRecord.createTime_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(DocumentViewRecord.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(DocumentViewRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<DocumentViewRecord> root = cq.from(DocumentViewRecord.class);
		Predicate p = cb.conjunction();

		if(StringUtils.isNotEmpty(wi.getDocId())){
			p = cb.and(p, cb.equal(root.get(DocumentViewRecord_.documentId), wi.getDocId()));
		}
		String person = wi.getViewerName();
		if(StringUtils.isNotEmpty(person)){
			person = business.organization().person().get(person);
			if(StringUtils.isBlank(person)){
				person = wi.getViewerName();
			}
			p = cb.and(p, cb.equal(root.get(DocumentViewRecord_.viewerName), person));
		}

		return p;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("信息文档ID")
		private String docId;

		@FieldDescribe("阅读人")
		private String viewerName;

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public String getViewerName() {
			return viewerName;
		}

		public void setViewerName(String viewerName) {
			this.viewerName = viewerName;
		}
	}

	public static class Wo extends DocumentViewRecord {

		private static final long serialVersionUID = 236759724445785414L;

		static WrapCopier<DocumentViewRecord, Wo> copier = WrapCopierFactory.wo(DocumentViewRecord.class, Wo.class,
				JpaObject.singularAttributeField(DocumentViewRecord.class, true, true), null);


	}

}
