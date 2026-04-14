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
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.Recycle3;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ActionResume extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Recycle3 recycle = emc.find(id, Recycle3.class);
			if (null == recycle) {
				throw new ExceptionAttachmentNotExist(id);
			}
			/* 判断当前用户是否有权限访问该文件 */
			if(!business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), recycle.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			if(Share.FILE_TYPE_ATTACHMENT.equals(recycle.getFileType())){
				this.resumeAtt(business, recycle, effectivePerson.getDistinguishedName());
			}else{
				this.resumeFolder(business, recycle, effectivePerson.getDistinguishedName());
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

	private void resumeAtt(Business business, Recycle3 recycle, String person) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		if(StringUtils.isBlank(recycle.getZoneId())) {
			Attachment2 att = emc.find(recycle.getFileId(), Attachment2.class);
			if (att != null) {
				long usedSize = business.attachment2().getUseCapacity(person);
				usedSize = usedSize + att.getLength();
				int vResult = business.verifyConstraint(person, usedSize);
				if (vResult > 0) {
					long usedCapacity = usedSize / (1024 * 1024);
					throw new ExceptionCapacityOut(usedCapacity, vResult);
				}
				emc.beginTransaction(Attachment2.class);
				att.setStatus(FileStatusEnum.VALID.getName());
			}
		}else{
			Attachment3 att = emc.find(recycle.getFileId(), Attachment3.class);
			if(att!=null){
				emc.beginTransaction(Attachment3.class);
				att.setStatus(FileStatusEnum.VALID.getName());
				att.setLastUpdatePerson(person);
				att.setLastUpdateTime(new Date());
			}
		}
	}

	private void resumeFolder(Business business, Recycle3 recycle, String person) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		if(StringUtils.isBlank(recycle.getZoneId())) {
			Folder2 folder = emc.find(recycle.getFileId(), Folder2.class);
			if (folder != null) {
				long usedSize = business.attachment2().getUseCapacity(person);
				emc.beginTransaction(Folder2.class);
				emc.beginTransaction(Attachment2.class);
				List<Folder2> folderList = new ArrayList<>();
				folderList.add(folder);
				folderList.addAll(business.folder2().listSubNested1(folder.getId(), null));
				for (Folder2 fo : folderList) {
					if (business.folder2().exist(person, fo.getName(), fo.getSuperior(), fo.getId())) {
						fo.setName(System.currentTimeMillis() + fo.getName());
					}
					fo.setStatus(FileStatusEnum.VALID.getName());
					List<Attachment2> attachments = business.attachment2().listWithFolder2(fo.getId(), null);
					for (Attachment2 att : attachments) {
						usedSize = usedSize + att.getLength();
						int vResult = business.verifyConstraint(person, usedSize);
						if (vResult > 0) {
							long usedCapacity = usedSize / (1024 * 1024);
							throw new ExceptionCapacityOut(usedCapacity, vResult);
						}
						att.setStatus(FileStatusEnum.VALID.getName());
					}
				}
			}
		} else{
			Folder3 folder = emc.find(recycle.getFileId(), Folder3.class);
			if (folder != null) {
				emc.beginTransaction(Folder3.class);
				emc.beginTransaction(Attachment3.class);
				List<Folder3> folderList = new ArrayList<>();
				folderList.add(folder);
				folderList.addAll(business.folder3().listSubNested1(folder.getId(), null));
				for (Folder3 fo : folderList) {
					if (business.folder3().exist(fo.getName(), fo.getSuperior(), fo.getZoneId(), null, fo.getId())) {
						fo.setName(System.currentTimeMillis() + fo.getName());
					}
					fo.setStatus(FileStatusEnum.VALID.getName());
					fo.setLastUpdateTime(new Date());
					fo.setLastUpdatePerson(person);
					List<Attachment3> attachments = business.attachment3().listWithFolder2(fo.getId(), null);
					for (Attachment3 att : attachments) {
						att.setStatus(FileStatusEnum.VALID.getName());
						att.setLastUpdateTime(new Date());
						att.setLastUpdatePerson(person);
					}
				}
			}
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
