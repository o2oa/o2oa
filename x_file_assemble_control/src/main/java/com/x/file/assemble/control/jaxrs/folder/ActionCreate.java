package com.x.file.assemble.control.jaxrs.folder;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.Folder;

public class ActionCreate {

	public WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInFolder wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Folder.class);
		Folder folder = new Folder();
		folder.setPerson(effectivePerson.getName());
		folder.setName(wrapIn.getName());
		folder.setSuperior(StringUtils.trimToEmpty(wrapIn.getSuperior()));
		emc.persist(folder, CheckPersistType.all);
		emc.commit();
		WrapOutId wrap = new WrapOutId(folder.getId());
		return wrap;

	}

}
