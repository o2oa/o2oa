package com.x.file.assemble.control.jaxrs.folder;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutFolder;
import com.x.file.core.entity.Folder;

public class ActionListTop extends ActionBase {

	public List<WrapOutFolder> execute(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.folder().listTopWithPerson(effectivePerson.getName());
		List<WrapOutFolder> wraps = copier.copy(emc.list(Folder.class, ids));
		for (WrapOutFolder o : wraps) {
			setCount(business, o);
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
