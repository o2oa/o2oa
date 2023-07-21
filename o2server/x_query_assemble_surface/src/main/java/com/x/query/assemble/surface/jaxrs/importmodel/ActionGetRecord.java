package com.x.query.assemble.surface.jaxrs.importmodel;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.ImportRecord;

class ActionGetRecord extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetRecord.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String recordId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			ImportRecord record = emc.find(recordId, ImportRecord.class);
			if(record == null){
				throw new ExceptionEntityNotExist(recordId, ImportRecord.class);
			}
			Wo wo = Wo.copier.copy(record);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends ImportRecord {

		/** 不输出data数据,单独处理 */
		static WrapCopier<ImportRecord, Wo> copier = WrapCopierFactory.wo(ImportRecord.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));
	}
}
