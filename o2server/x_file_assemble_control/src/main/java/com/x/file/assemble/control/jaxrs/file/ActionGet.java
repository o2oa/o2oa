package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.open.File;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			File file = emc.find(id, File.class);
			if (null == file) {
				throw new ExceptionFileNotExisted(id);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(file.getPerson())) {
				throw new ExceptionFileAccessDenied(effectivePerson.getDistinguishedName(), file.getName(),
						file.getId());
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
