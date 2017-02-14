package com.x.file.assemble.control.jaxrs.folder;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutFolder;
import com.x.file.core.entity.Folder;

public class ActionListWithFolder extends ActionBase {

	public List<WrapOutFolder> execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Folder folder = emc.find(id, Folder.class, ExceptionWhen.not_found);
		if (!StringUtils.equals(folder.getPerson(), effectivePerson.getName())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} access folder{id:" + id + "} denied.");
		}
		List<String> ids = business.folder().listSubDirect(folder.getId());
		List<WrapOutFolder> wraps = copier.copy(emc.list(Folder.class, ids));
		for (WrapOutFolder o : wraps) {
			setCount(business, o);
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
