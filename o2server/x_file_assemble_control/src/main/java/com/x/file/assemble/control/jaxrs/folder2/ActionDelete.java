package com.x.file.assemble.control.jaxrs.folder2;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Recycle;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder2 folder = emc.find(id, Folder2.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!effectivePerson.isManager() && !StringUtils.equalsIgnoreCase(effectivePerson.getDistinguishedName(), folder.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			if(FileStatus.VALID.getName().equals(folder.getStatus())){
				List<Folder2> folderList = new ArrayList<>();
				folderList.add(folder);
				folderList.addAll(business.folder2().listSubNested1(folder.getId(), null));
				for(Folder2 fo : folderList){
					EntityManager fem = emc.beginTransaction(Folder2.class);
					fo.setStatus(FileStatus.INVALID.getName());
					fem.getTransaction().commit();
					List<Attachment2> attachments = business.attachment2().listWithFolder2(fo.getId(),null);
					for (Attachment2 att : attachments) {
						EntityManager aem = emc.beginTransaction(Attachment2.class);
						att.setStatus(FileStatus.INVALID.getName());
						aem.getTransaction().commit();
					}
				}

				Recycle recycle = new Recycle(folder.getPerson(), folder.getName(), folder.getId(), "folder");
				EntityManager rem = emc.beginTransaction(Recycle.class);
				rem.persist(recycle);
				rem.getTransaction().commit();
				emc.commit();
			}

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}