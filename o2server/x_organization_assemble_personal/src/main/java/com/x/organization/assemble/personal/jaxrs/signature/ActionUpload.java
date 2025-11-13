package com.x.organization.assemble.personal.jaxrs.signature;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.organization.core.entity.Custom;
import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionUpload extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			String signature = Base64.encodeBase64String(bytes);
			emc.beginTransaction(Custom.class);
			Custom custom = new Custom();
			custom.setPerson(effectivePerson.getDistinguishedName());
			custom.setName(CUSTOM_SIGNATURE_NAME);
			custom.setData(signature);
			emc.persist(custom, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
