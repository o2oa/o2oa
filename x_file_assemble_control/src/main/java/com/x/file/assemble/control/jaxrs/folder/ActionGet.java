package com.x.file.assemble.control.jaxrs.folder;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutFolder;
import com.x.file.core.entity.Folder;

public class ActionGet extends ActionBase {

	public WrapOutFolder execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Folder folder = emc.find(id, Folder.class, ExceptionWhen.not_found);
		if (!StringUtils.equals(folder.getPerson(), effectivePerson.getName())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} access folder{id:" + id + "} denied.");
		}
		WrapOutFolder wrap = copier.copy(folder);
		setCount(business, wrap);
		return wrap;
	}

}
