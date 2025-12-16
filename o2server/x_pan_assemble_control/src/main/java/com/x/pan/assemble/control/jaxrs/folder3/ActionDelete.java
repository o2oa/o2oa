package com.x.pan.assemble.control.jaxrs.folder3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DateTools;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.Recycle3;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if(Business.TOP_FOLD.equals(folder.getSuperior())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			if(!business.zoneEditable(effectivePerson, folder.getId(), folder.getPerson())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			if(FileStatusEnum.VALID.getName().equals(folder.getStatus())){
				String name = folder.getName();
				if (business.folder3().exist(name, folder.getSuperior(),null, FileStatusEnum.INVALID.getName(), null)) {
					name = business.adjustDate(name);
				}
				folder.setName(name);
				List<Folder3> folderList = new ArrayList<>();
				folderList.add(folder);
				folderList.addAll(business.folder3().listSubNested1(folder.getId(), null));
				emc.beginTransaction(Folder3.class);
				emc.beginTransaction(Attachment3.class);
				for(Folder3 fo : folderList){
					fo.setStatus(FileStatusEnum.INVALID.getName());
					fo.setLastUpdatePerson(effectivePerson.getDistinguishedName());
					fo.setLastUpdateTime(new Date());
					List<Attachment3> attachments = business.attachment3().listWithFolder2(fo.getId(),null);
					for (Attachment3 att : attachments) {
						att.setStatus(FileStatusEnum.INVALID.getName());
						att.setLastUpdatePerson(effectivePerson.getDistinguishedName());
						att.setLastUpdateTime(new Date());
					}
				}

				Recycle3 recycle = new Recycle3(effectivePerson.getDistinguishedName(), name, folder.getId(), Share.FILE_TYPE_FOLDER, folder.getZoneId());
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
