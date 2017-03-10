package com.x.file.assemble.control.jaxrs.folder;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder;

public class ActionUpdate {

	BeanCopyTools<WrapInFolder, Folder> copier = BeanCopyToolsBuilder.create(WrapInFolder.class, Folder.class,
			WrapInFolder.Includes);

	public WrapOutId execute(Business business, EffectivePerson effectivePerson, String id, WrapInFolder wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Folder folder = emc.find(id, Folder.class, ExceptionWhen.not_found);
		if (!StringUtils.equalsIgnoreCase(effectivePerson.getName(), folder.getPerson())) {
			throw new Exception(
					"person{name:" + effectivePerson.getName() + "} can not update folder{id:" + folder.getId() + "}");
		}
		emc.beginTransaction(Folder.class);
		copier.copy(wrapIn, folder);
		emc.check(folder, CheckPersistType.all);
		emc.commit();
		WrapOutId wrap = new WrapOutId(folder.getId());
		return wrap;

	}

}
