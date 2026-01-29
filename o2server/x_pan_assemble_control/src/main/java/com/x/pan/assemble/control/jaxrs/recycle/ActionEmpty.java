package com.x.pan.assemble.control.jaxrs.recycle;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.Recycle3;
import com.x.pan.core.entity.ZonePermission;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ActionEmpty extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			List<Recycle3> recycleList = business.recycle().listWithPerson(effectivePerson.getDistinguishedName());
			if(ListTools.isEmpty(recycleList)){
				return result;
			}
			for(Recycle3 recycle : recycleList){
				if(Share.FILE_TYPE_ATTACHMENT.equals(recycle.getFileType())){
					if(StringUtils.isBlank(recycle.getZoneId())) {
						Attachment2 att = emc.find(recycle.getFileId(), Attachment2.class);
						emc.beginTransaction(Attachment2.class);
						this.deleteFile(business, att.getOriginFile());
						emc.remove(att);
					}else{
						Attachment3 att = emc.find(recycle.getFileId(), Attachment3.class);
						emc.beginTransaction(Attachment3.class);
						this.deleteFile(business, att.getOriginFile());
						emc.remove(att);
					}
				}else{
					if(StringUtils.isBlank(recycle.getZoneId())) {
						Folder2 folder = emc.find(recycle.getFileId(), Folder2.class);
						if (folder != null) {
							List<String> folderIds = new ArrayList<>();
							folderIds.add(folder.getId());
							folderIds.addAll(business.folder2().listSubNested(folder.getId(), null));
							Collections.reverse(folderIds);
							for (String folderId : folderIds){
								List<Attachment2> attachments = business.attachment2().listWithFolder2(folderId, null);
								for (Attachment2 att : attachments) {
									emc.beginTransaction(Attachment2.class);
									this.deleteFile(business, att.getOriginFile());
									emc.remove(att);
									emc.commit();
								}
								emc.beginTransaction(Folder2.class);
								emc.delete(Folder2.class, folderId);
								emc.commit();
							}
						}
					}else{
						Folder3 folder = emc.find(recycle.getFileId(), Folder3.class);
						if (folder != null) {
							List<String> folderIds = new ArrayList<>();
							folderIds.add(folder.getId());
							folderIds.addAll(business.folder3().listSubNested(folder.getId(), null));
							Collections.reverse(folderIds);
							for (String folderId : folderIds){
								List<Attachment3> attachments = business.attachment3().listWithFolder2(folderId, null);
								for (Attachment3 att : attachments) {
									emc.beginTransaction(Attachment3.class);
									this.deleteFile(business, att.getOriginFile());
									emc.remove(att);
									emc.commit();
								}
								emc.beginTransaction(Folder3.class);
								emc.beginTransaction(ZonePermission.class);
								emc.deleteEqual(ZonePermission.class, ZonePermission.zoneId_FIELDNAME, folder.getId());
								emc.delete(Folder3.class, folderId);
								emc.commit();
							}
						}
					}
				}
				emc.beginTransaction(Recycle3.class);
				emc.delete(Recycle3.class, recycle.getId());
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
