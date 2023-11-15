package com.x.processplatform.assemble.surface.jaxrs.record;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Record rec = null;
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			rec = emc.find(id, Record.class);
			if (null == rec) {
				throw new ExceptionEntityNotExist(id, Record.class);
			}
			Control control = new JobControlBuilder(effectivePerson, business, rec.getJob()).enableAllowManage()
					.build();
			if (BooleanUtils.isNotTrue(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson, rec.getJob());
			}
		}
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("record", rec.getId()), wi, rec.getJob()).getData(WoId.class);
		Wo wo = new Wo();
		wo.setId(resp.getId());
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.record.ActionManageEdit$Wi")
	public static class Wi extends Record {

		private static final long serialVersionUID = 4179509440650818001L;

		static WrapCopier<Wi, Record> copier = WrapCopierFactory.wi(Wi.class, Record.class, null,
				JpaObject.FieldsUnmodify);

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.record.ActionManageEdit$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -3506278526439574670L;

	}

}