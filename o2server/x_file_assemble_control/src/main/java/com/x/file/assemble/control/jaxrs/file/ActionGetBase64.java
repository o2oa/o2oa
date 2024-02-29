package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;

class ActionGetBase64 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			File file = emc.find(id, File.class, ExceptionWhen.not_found);
			if (null == file) {
				throw new ExceptionFileNotExisted(id);
			}

			StorageMapping mapping = ThisApplication.context().storageMappings().get(File.class,
					file.getStorage());
			try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
				file.readContent(mapping, output);
				String value = Base64.encodeBase64String(output.toByteArray());
				Wo wo = new Wo();
				wo.setValue(value);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WrapString {

	}

}
