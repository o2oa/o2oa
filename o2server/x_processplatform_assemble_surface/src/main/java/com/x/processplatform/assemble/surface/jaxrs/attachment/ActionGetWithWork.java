package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGetWithWork extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetWithWork.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {

		LOGGER.debug("execute:{}, id:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> id, () -> workId);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}

			Wo wo = Wo.copier.copy(attachment);

			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);

			List<String> units = business.organization().unit().listWithPerson(effectivePerson);

			boolean canControl = this.control(attachment, effectivePerson, identities, units, business);
			boolean canEdit = this.edit(attachment, effectivePerson, identities, units, business);
			boolean canRead = this.read(attachment, effectivePerson, identities, units, business);
			if (canRead) {
				wo.getControl().setAllowRead(true);
				wo.getControl().setAllowEdit(canEdit);
				wo.getControl().setAllowControl(canControl);
			}

			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionGetWithWork$Wo")
	public static class Wo extends Attachment {

		private static final long serialVersionUID = 1954637399762611493L;

		static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private WoControl control = new WoControl();

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionGetWithWork$WoControl")
	public static class WoControl extends GsonPropertyObject {

		private static final long serialVersionUID = -2289010323449796825L;

		private Boolean allowRead = false;
		private Boolean allowEdit = false;
		private Boolean allowControl = false;

		public Boolean getAllowRead() {
			return allowRead;
		}

		public void setAllowRead(Boolean allowRead) {
			this.allowRead = allowRead;
		}

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}

	}

}