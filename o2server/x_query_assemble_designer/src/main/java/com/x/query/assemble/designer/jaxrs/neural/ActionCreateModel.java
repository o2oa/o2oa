package com.x.query.assemble.designer.jaxrs.neural;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.neural.Model;

class ActionCreateModel extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			emc.beginTransaction(Model.class);
			Model model = Wi.copier.copy(wi);
			emc.persist(model, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Model.class);
			Wo wo = new Wo();
			wo.setId(model.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Model {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Model> copier = WrapCopierFactory.wi(Wi.class, Model.class,
				ListTools.toList(Model.neuralNetworkType_FIELDNAME, Model.dataType_FIELDNAME, Model.name_FIELDNAME,
						Model.description_FIELDNAME, Model.alias_FIELDNAME, Model.inValueScriptText_FIELDNAME,
						Model.outValueScriptText_FIELDNAME, Model.attachmentScriptText_FIELDNAME,
						Model.processList_FIELDNAME, Model.applicationList_FIELDNAME, Model.inValueCount_FIELDNAME,
						Model.outValueCount_FIELDNAME, Model.maxResult_FIELDNAME, Model.propertyMap_FIELDNAME,
						Model.analyzeType_FIELDNAME),
				JpaObject.FieldsUnmodifyExcludeId);

	}

}