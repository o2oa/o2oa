package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			File file = emc.find(flag, File.class);
			if (null == file) {
				file = emc.flag(flag, File.class);
				if(file == null) {
					throw new ExceptionEntityNotExist(flag);
				}
			}
			Wo wo = Wo.copier.copy(file);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends File {

		private static final long serialVersionUID = 100904116457932549L;

		static WrapCopier<File, Wo> copier = WrapCopierFactory.wo(File.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
