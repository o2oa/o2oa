package com.x.query.assemble.surface.jaxrs.importmodel;

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
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.ImportRecordItem;
import com.x.query.core.entity.ImportRecordItem_;

class ActionRecordItemListPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(ImportRecordItem.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<ImportRecordItem> root = cq.from(ImportRecordItem.class);
			Predicate p = cb.conjunction();
			if(StringUtils.isNotBlank(wi.getRecordId())){
				p = cb.and(p, cb.equal(root.get(ImportRecordItem_.recordId), wi.getRecordId()));
			}
			if(StringUtils.isNotBlank(wi.getStatus())){
				p = cb.and(p, cb.equal(root.get(ImportRecordItem_.status), wi.getStatus()));
			}
			List<Wo> wos = emc.fetchDescPaging(ImportRecordItem.class, Wo.copier, p, page, size, ImportRecordItem.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(ImportRecordItem.class, p));
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject{

		private static final long serialVersionUID = -907555682909071665L;

		@FieldDescribe("导入记录ID.")
		private String recordId;

		@FieldDescribe("状态：导入成功|导入失败.")
		private String status;

		public String getRecordId() {
			return recordId;
		}

		public void setRecordId(String recordId) {
			this.recordId = recordId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public static class Wo extends ImportRecordItem {

		private static final long serialVersionUID = -7382962683340732228L;

		static WrapCopier<ImportRecordItem, Wo> copier = WrapCopierFactory.wo(ImportRecordItem.class, Wo.class,
				null, ListTools.toList(JpaObject.FieldsInvisible, ImportRecordItem.data_FIELDNAME));

	}
}
