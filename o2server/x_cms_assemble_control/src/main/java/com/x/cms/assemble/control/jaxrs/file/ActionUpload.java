package com.x.cms.assemble.control.jaxrs.file;

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
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.File;

class ActionUpload extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String fileName, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			FileTools.verifyConstraint(bytes.length, fileName, null);
			File file = emc.find(id, File.class);
			if (null == file) {
				throw new ExceptionEntityNotExist(id, File.class);
			}
			AppInfo appInfo = emc.find(file.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionEntityNotExist(file.getAppId(), AppInfo.class);
			}
			if ((!business.isAppInfoManager(effectivePerson, appInfo))) {
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
