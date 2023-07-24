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
import com.x.query.core.entity.ImportRecord;
import com.x.query.core.entity.ImportRecord_;

class ActionRecordListPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(ImportRecord.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<ImportRecord> root = cq.from(ImportRecord.class);
			Predicate p = cb.conjunction();
			if(StringUtils.isNotBlank(wi.getModelId())){
				p = cb.and(p, cb.equal(root.get(ImportRecord_.modelId), wi.getModelId()));
			}
			if(StringUtils.isNotBlank(wi.getStatus())){
				p = cb.and(p, cb.equal(root.get(ImportRecord_.status), wi.getStatus()));
			}
			List<Wo> wos = emc.fetchDescPaging(ImportRecord.class, Wo.copier, p, page, size, ImportRecord.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(ImportRecord.class, p));
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject{

		private static final long serialVersionUID = -5861772273940394221L;

		@FieldDescribe("导入模型ID.")
		private String modelId;

		@FieldDescribe("状态：待导入|导入中|导入成功|部分成功|导入失败.")
		private String status;

		public String getModelId() {
			return modelId;
		}

		public void setModelId(String modelId) {
			this.modelId = modelId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public static class Wo extends ImportRecord {

		private static final long serialVersionUID = 56729158952414491L;

		static WrapCopier<ImportRecord, Wo> copier = WrapCopierFactory.wo(ImportRecord.class, Wo.class,
				null, ListTools.toList(JpaObject.FieldsInvisible, ImportRecord.data_FIELDNAME));

	}
}
