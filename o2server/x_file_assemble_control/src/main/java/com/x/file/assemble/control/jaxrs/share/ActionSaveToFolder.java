package com.x.file.assemble.control.jaxrs.share;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;

class ActionSaveToFolder extends BaseAction {

	private long usedSize;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String shareId, String fileId, String folderId, String password) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Share share = emc.find(shareId, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(shareId);
			}
			if ((!StringUtils.isEmpty(folderId)) && (!StringUtils.equalsIgnoreCase(folderId, EMPTY_SYMBOL))
					&& !Business.TOP_FOLD.equals(folderId)) {
				Folder2 folder = emc.find(folderId, Folder2.class);
				if (folder == null) {
					throw new ExceptionFolderNotExist(folderId);
				}
				/* 判断目录的所有者是否是当前用户 */
				if (!StringUtils.equals(effectivePerson.getDistinguishedName(), folder.getPerson())) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName(), folderId);
				}
			}else{
				folderId = Business.TOP_FOLD;
			}
			/* 判断文件的分享用户是否包含当前用户 */
			if(!Share.SHARE_TYPE_PASSWORD.equals(share.getShareType())) {
				if(!hasPermission(business,effectivePerson,share)){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}else{
				if(StringUtils.isEmpty(password)){
					throw new ExceptionFieldEmpty(Share.password_FIELDNAME);
				}
				if(!password.equalsIgnoreCase(share.getPassword())){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}
			if(StringUtils.isEmpty(fileId)){
				throw new ExceptionFieldEmpty(Share.fileId_FIELDNAME);
			}

			/* 转存文件或目录到指定的目录下 */
			saveToFolder(business, share, fileId, folderId, effectivePerson);

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void saveToFolder(Business business, Share share, String fileId, String folderId, EffectivePerson effectivePerson) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();

		this.usedSize = business.attachment2().getUseCapacity(effectivePerson.getDistinguishedName());
		if(Share.FILE_TYPE_ATTACHMENT.equals(share.getFileType())){
			Attachment2 att = emc.find(fileId, Attachment2.class);
			if(att!=null) {
				usedSize = usedSize + att.getLength();
				int vResult = business.verifyConstraint(effectivePerson.getDistinguishedName(), usedSize);
				if(vResult > 0){
					long usedCapacity = usedSize / (1024 * 1024);
					throw new ExceptionCapacityOut(usedCapacity, vResult);
				}
				Attachment2 newAtt = new Attachment2(att.getName(), effectivePerson.getDistinguishedName(),
						folderId, att.getOriginFile(), att.getLength(), att.getType());
				emc.check(newAtt, CheckPersistType.all);
				emc.beginTransaction(Attachment2.class);
				emc.persist(newAtt);
				emc.commit();
			}
		}else{
			Folder2 folder = emc.find(fileId, Folder2.class);
			Folder2 newFolder = new Folder2(folder.getName(),effectivePerson.getDistinguishedName(),folderId,folder.getStatus());
			EntityManager em = emc.beginTransaction(Folder2.class);
			emc.check(newFolder, CheckPersistType.all);
			em.persist(newFolder);
			em.getTransaction().commit();
			saveSubFolder(business, folder.getId(), newFolder.getId(), effectivePerson);
		}
	}

	private void saveSubFolder(Business business, String folderId, String newFolderId, EffectivePerson effectivePerson) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();

		List<Attachment2> attachments = business.attachment2().listWithFolder2(folderId,FileStatus.VALID.getName());
		for (Attachment2 att : attachments) {
			usedSize = usedSize + att.getLength();
			int vResult = business.verifyConstraint(effectivePerson.getDistinguishedName(), usedSize);
			if(vResult > 0){
				long usedCapacity = usedSize / (1024 * 1024);
				throw new ExceptionCapacityOut(usedCapacity, vResult);
			}
			Attachment2 newAtt = new Attachment2(att.getName(), effectivePerson.getDistinguishedName(),
					newFolderId, att.getOriginFile(), att.getLength(), att.getType());
			EntityManager em2 = emc.beginTransaction(Attachment2.class);
			em2.persist(newAtt);
			em2.getTransaction().commit();
		}

		List<Folder2> subFolders = business.folder2().listSubDirect1(folderId, FileStatus.VALID.getName());
		for(Folder2 folder : subFolders){
			Folder2 newSubFolder = new Folder2(folder.getName(),effectivePerson.getDistinguishedName(),newFolderId,folder.getStatus());
			EntityManager em1 = emc.beginTransaction(Folder2.class);
			em1.persist(newSubFolder);
			em1.getTransaction().commit();
			saveSubFolder(business, folder.getId(), newSubFolder.getId(), effectivePerson);
		}

	}

	public static class Wo extends WrapBoolean {
	}
}
