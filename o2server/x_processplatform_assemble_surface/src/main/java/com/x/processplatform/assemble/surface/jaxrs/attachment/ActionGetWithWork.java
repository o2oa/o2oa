package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

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
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.jaxrs.attachment.ActionListWithWorkOrWorkCompleted.Wo;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionGetWithWork extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}

			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, work.getId(),
					new ExceptionEntityNotExist(work.getId()))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			Wo wo = Wo.copier.copy(attachment);

			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);

			List<String> units = business.organization().unit().listWithPerson(effectivePerson);

			if (this.read(wo, effectivePerson, identities, units)) {
				wo.getControl().setAllowRead(true);
				wo.getControl().setAllowEdit(this.edit(wo, effectivePerson, identities, units));
				wo.getControl().setAllowControl(this.control(wo, effectivePerson, identities, units));
			}

			result.setData(wo);
			return result;
		}
	}

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

	public static class WoControl extends GsonPropertyObject {

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

	private boolean read(Wo wo, EffectivePerson effectivePerson, List<String> identities, List<String> units)
			throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(wo.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(wo.getReadIdentityList()) && ListTools.isEmpty(wo.getReadUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, wo.getReadIdentityList())
					|| ListTools.containsAny(identities, wo.getReadUnitList())) {
				value = true;
			}
		}
		wo.getControl().setAllowRead(value);
		return value;
	}

	private boolean edit(Wo wo, EffectivePerson effectivePerson, List<String> identities, List<String> units)
			throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(wo.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(wo.getEditIdentityList()) && ListTools.isEmpty(wo.getEditUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, wo.getEditIdentityList())
					|| ListTools.containsAny(identities, wo.getEditUnitList())) {
				value = true;
			}
		}
		return value;
	}

	private boolean control(Wo wo, EffectivePerson effectivePerson, List<String> identities, List<String> units)
			throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(wo.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(wo.getControllerUnitList()) && ListTools.isEmpty(wo.getControllerIdentityList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, wo.getControllerIdentityList())
					|| ListTools.containsAny(identities, wo.getControllerUnitList())) {
				value = true;
			}
		}
		return value;
	}
}