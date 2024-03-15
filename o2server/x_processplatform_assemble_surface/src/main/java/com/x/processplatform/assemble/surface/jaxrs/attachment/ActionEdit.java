package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
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
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> id, () -> workId);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Attachment attachment = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			// 判断work是否存在
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}

			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPerson(effectivePerson);
			boolean canControl = this.control(attachment, effectivePerson, identities, units, business);
			if (!canControl) {
				throw new ExceptionAccessDenied(effectivePerson, attachment);
			}

			emc.beginTransaction(Attachment.class);
			Wi.copier.copy(wi, attachment);
			emc.check(attachment, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
		}

		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionEdit$Wi")
	public static class Wi extends Attachment {

		private static final long serialVersionUID = 4243967432624425952L;

		static WrapCopier<Wi, Attachment> copier = WrapCopierFactory.wi(Wi.class, Attachment.class,
				Arrays.asList(Attachment.readIdentityList_FIELDNAME, Attachment.readUnitList_FIELDNAME,
						Attachment.editIdentityList_FIELDNAME, Attachment.editUnitList_FIELDNAME,
						Attachment.controllerIdentityList_FIELDNAME, Attachment.controllerUnitList_FIELDNAME,
						Attachment.divisionList_FIELDNAME, Attachment.OBJECTSECURITYCLEARANCE_FIELDNAME,
						Attachment.stringValue01_FIELDNAME, Attachment.stringValue02_FIELDNAME,
						Attachment.stringValue03_FIELDNAME),
				null);

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionEdit$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 566443882004256284L;

	}

}
