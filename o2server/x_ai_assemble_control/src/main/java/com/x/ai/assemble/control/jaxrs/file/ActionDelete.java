package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			File file = emc.find(flag, File.class);
			if (null == file) {
				file = emc.flag(flag, File.class);
				if(file == null) {
					throw new ExceptionEntityNotExist(flag);
				}
			}
			if(effectivePerson.isNotManager()){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(File.class,
					file.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageMappingNotExisted(file.getStorage());
			}

			file.deleteContent(mapping);
			emc.beginTransaction(File.class);
			emc.remove(file);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(file.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
