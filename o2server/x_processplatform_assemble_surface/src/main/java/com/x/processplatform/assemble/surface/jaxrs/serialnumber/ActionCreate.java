package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import org.eclipse.jetty.util.StringUtil;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if(StringUtil.isBlank(wi.getProcess())){
				throw new ExceptionFieldEmpty(SerialNumber.process_FIELDNAME);
			}
			if(wi.getSerial() == null){
				throw new ExceptionFieldEmpty(SerialNumber.serial_FIELDNAME);
			}
			Process process = business.process().pick(wi.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(wi.getProcess());
			}
			Application application = business.application().pick(process.getApplication());
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			SerialNumber serialNumber = Wi.copier.copy(wi);
			serialNumber.setApplication(process.getApplication());
			emc.beginTransaction(SerialNumber.class);
			emc.persist(serialNumber, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(serialNumber.getId());
			result.setData(wo);
		}
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.serialnumber.ActionCreate$Wi")
	public static class Wi extends SerialNumber {

		private static final long serialVersionUID = 4466662676168979509L;
		static WrapCopier<Wi, SerialNumber> copier = WrapCopierFactory.wi(Wi.class, SerialNumber.class, null,
				JpaObject.FieldsUnmodify);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.serialnumber.ActionCreate$Wo")
	public static class Wo extends WoId {
	}
}
