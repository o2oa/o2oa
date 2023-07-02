package com.x.query.assemble.surface.jaxrs.importmodel;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.ImportRecord;
import com.x.query.core.entity.Query;
import org.apache.commons.lang3.StringUtils;

class ActionExecute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ImportModel model = business.pick(id, ImportModel.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(id, ImportModel.class);
			}
			Query query = business.pick(model.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(model.getQuery(), Query.class);
			}
			if (!business.readable(effectivePerson, model)) {
				throw new ExceptionAccessDenied(effectivePerson, model);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(StringUtils.isBlank(wi.getRecordId())){
				throw new ExceptionEntityFieldEmpty(ImportRecord.class, "id");
			}
			ImportRecord record = business.pick(wi.getRecordId(), ImportRecord.class);
			if(record != null){
				throw new ExceptionEntityExist(wi.getRecordId(), record);
			}
			emc.beginTransaction(ImportRecord.class);
			record = new ImportRecord();
			record.setId(wi.getRecordId());
			record.setName(model.getName());
			record.setModelId(model.getId());
			record.setQuery(model.getQuery());
			record.setCount(wi.getData().getAsJsonArray().size());
			record.setData(wi.getData().toString());
			record.setStatus(ImportRecord.STATUS_WAIT);
			record.setCreatorPerson(effectivePerson.getDistinguishedName());
			emc.persist(record, CheckPersistType.all);
			emc.commit();
			wo.setId(record.getId());
			logger.info(record.getId()+"=="+wi.getRecordId());
		}
		try {
			ThisApplication.queueImportData.send(wo.getId());
		} catch (Exception e) {
			logger.warn("{}-数据导入处理放入队列异常:{}", wo.getId(), e.getMessage());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("导入记录ID.")
		private String recordId;

		@FieldDescribe("数据.")
		private JsonElement data;

		public String getRecordId() {
			return recordId;
		}

		public void setRecordId(String recordId) {
			this.recordId = recordId;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

	}
}
