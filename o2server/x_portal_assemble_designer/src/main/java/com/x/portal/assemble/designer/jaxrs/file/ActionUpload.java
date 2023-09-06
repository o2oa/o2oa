package com.x.portal.assemble.designer.jaxrs.file;

import com.x.base.core.project.tools.FileTools;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.File;
import com.x.portal.core.entity.Portal;

class ActionUpload extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String fileName,byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			FileTools.verifyConstraint(bytes.length, fileName, null);
			File file = emc.flag(id, File.class);
			if (null == file) {
				throw new ExceptionEntityNotExist(id, File.class);
			}
			Portal portal = emc.find(file.getPortal(), Portal.class);
			if (null == portal) {
				throw new ExceptionEntityNotExist(file.getPortal(), Portal.class);
			}
			if ((!business.editable(effectivePerson, portal))) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(File.class);
			file.setLength((long) bytes.length);
			file.setData(Base64.encodeBase64String(bytes));
			file.setFileName(fileName);
			emc.commit();
			CacheManager.notify(File.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
