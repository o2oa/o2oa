package com.x.pan.assemble.control.jaxrs.recycle;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.Recycle3;
import com.x.pan.core.entity.ZonePermission;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Recycle3 recycle = emc.find(id, Recycle3.class);
			if (null == recycle) {
				throw new ExceptionAttachmentNotExist(id);
			}
			/* 判断当前用户是否有权限访问该文件 */
			boolean flag = !business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), recycle.getPerson());
			if(flag) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

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
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
