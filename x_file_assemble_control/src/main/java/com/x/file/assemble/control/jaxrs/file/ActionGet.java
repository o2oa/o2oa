package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.file.assemble.control.wrapout.WrapOutFile;
import com.x.file.core.entity.open.File;

class ActionGet extends ActionBase {
	ActionResult<WrapOutFile> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutFile> result = new ActionResult<>();
		WrapOutFile wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			File file = emc.find(id, File.class);
			if (null == file) {
				throw new FileNotExistedException(id);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotUser(file.getPerson())) {
				throw new FileAccessDeniedException(effectivePerson.getName(), file.getName(), file.getId());
			}
			wrap = copier.copy(file);
			result.setData(wrap);
			return result;
		}
	}
}
