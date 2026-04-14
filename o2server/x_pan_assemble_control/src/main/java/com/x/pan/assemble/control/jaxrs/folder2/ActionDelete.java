package com.x.pan.assemble.control.jaxrs.folder2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Recycle3;

import java.util.ArrayList;
import java.util.List;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder2 folder = emc.find(id, Folder2.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!effectivePerson.getDistinguishedName().equals(folder.getPerson()) && !business.controlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			if(FileStatusEnum.VALID.getName().equals(folder.getStatus())){
				List<Folder2> folderList = new ArrayList<>();
				folderList.add(folder);
				folderList.addAll(business.folder2().listSubNested1(folder.getId(), null));
				emc.beginTransaction(Folder2.class);
				emc.beginTransaction(Attachment2.class);
				for(Folder2 fo : folderList){
					fo.setStatus(FileStatusEnum.INVALID.getName());
					List<Attachment2> attachments = business.attachment2().listWithFolder2(fo.getId(),null);
					for (Attachment2 att : attachments) {
						att.setStatus(FileStatusEnum.INVALID.getName());
					}
				}

				Recycle3 recycle = new Recycle3(folder.getPerson(), folder.getName(), folder.getId(), Share.FILE_TYPE_FOLDER, "");
				emc.beginTransaction(Recycle3.class);
				emc.persist(recycle, CheckPersistType.all);
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
