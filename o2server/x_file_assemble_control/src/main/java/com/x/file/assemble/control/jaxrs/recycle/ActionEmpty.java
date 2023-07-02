package com.x.file.assemble.control.jaxrs.recycle;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Recycle;
import com.x.file.core.entity.personal.Share;

class ActionEmpty extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			List<Recycle> recycleList = business.recycle().listWithPerson(effectivePerson.getDistinguishedName());
			if(ListTools.isEmpty(recycleList)){
				return result;
			}
			for(Recycle recycle : recycleList){
				if(Share.FILE_TYPE_ATTACHMENT.equals(recycle.getFileType())){
					Attachment2 att = emc.find(recycle.getFileId(), Attachment2.class);
					this.deleteFile(business, att);
				}else{
					Folder2 folder = emc.find(recycle.getFileId(), Folder2.class);
					if(folder!=null) {
						List<String> ids = new ArrayList<>();
						ids.add(folder.getId());
						ids.addAll(business.folder2().listSubNested(folder.getId(), null));
						for (int i = ids.size() - 1; i >= 0; i--) {
							List<Attachment2> attachments = business.attachment2().listWithFolder2(ids.get(i),null);
							for (Attachment2 att : attachments) {
								this.deleteFile(business, att);
							}
							emc.beginTransaction(Folder2.class);
							emc.delete(Folder2.class, ids.get(i));
							emc.commit();
						}
					}
				}
				emc.beginTransaction(Recycle.class);
				emc.delete(Recycle.class, recycle.getId());
				emc.commit();
			}

			return result;
		}
	}

	private void deleteFile(Business business, Attachment2 att) throws Exception{
		if(att==null){
			return;
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		Long count = emc.countEqual(Attachment2.class, Attachment2.originFile_FIELDNAME, att.getOriginFile());
		if(count.equals(1L)){
			OriginFile originFile = emc.find(att.getOriginFile(), OriginFile.class);
			if(originFile!=null){
				StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
						originFile.getStorage());
				if(mapping!=null){
					originFile.deleteContent(mapping);
				}
				emc.beginTransaction(Attachment2.class);
				emc.beginTransaction(OriginFile.class);
				emc.remove(att);
				emc.remove(originFile);
				emc.commit();
			}
		}else{
			emc.beginTransaction(Attachment2.class);
			emc.remove(att);
			emc.commit();
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
