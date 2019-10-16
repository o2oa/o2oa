package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.organization.Unit;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

class ActionSaveToFolder extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String shareId, String fileId, String folderId, String password) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Share share = emc.find(shareId, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(shareId);
			}
			if ((!StringUtils.isEmpty(folderId)) && (!StringUtils.equalsIgnoreCase(folderId, EMPTY_SYMBOL))) {
				Folder2 folder = emc.find(folderId, Folder2.class);
				if (folder == null) {
					throw new ExceptionFolderNotExist(folderId);
				}
				/* 判断目录的所有者是否是当前用户 */
				if (!StringUtils.equals(effectivePerson.getDistinguishedName(), folder.getPerson())) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName(), folderId);
				}
			}else{
				folderId = null;
			}
			/* 判断文件的分享用户是否包含当前用户 */
			if(!"password".equals(share.getShareType())) {
				if(!hasPermission(business,effectivePerson,share)){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}else{
				if(StringUtils.isEmpty(password)){
					throw new Exception("password can not be empty.");
				}
				if(!password.equalsIgnoreCase(share.getPassword())){
					throw new Exception("invalid password.");
				}
			}
			if(StringUtils.isEmpty(fileId)){
				throw new Exception("fileId can not be empty.");
			}
			/* 转存文件或目录到指定的目录下 */
			if("attachment".equals(share.getFileType())){
				Attachment2 att = emc.find(fileId, Attachment2.class);
				Attachment2 newAtt = new Attachment2(att.getName(), effectivePerson.getDistinguishedName(),
						folderId, att.getOriginFile(), att.getLength(), att.getType());
				emc.check(newAtt, CheckPersistType.all);
				emc.beginTransaction(Attachment2.class);
				emc.persist(newAtt);
				emc.commit();
			}else{
				Folder2 folder = emc.find(fileId, Folder2.class);
				Folder2 newFolder = new Folder2(folder.getName(),effectivePerson.getDistinguishedName(),folderId,folder.getStatus());
				EntityManager em = emc.beginTransaction(Folder2.class);
				emc.check(newFolder, CheckPersistType.all);
				em.persist(newFolder);
				em.getTransaction().commit();
				List<String> subIds = business.folder2().listSubNested(folder.getId(),"正常");
				for(String subFold : subIds){
					folder = emc.find(subFold, Folder2.class);
					Folder2 newSubFolder = new Folder2(folder.getName(),effectivePerson.getDistinguishedName(),folderId,folder.getStatus());
					EntityManager em1 = emc.beginTransaction(Folder2.class);
					em1.persist(newSubFolder);
					em1.getTransaction().commit();
					List<Attachment2> attachments = business.attachment2().listWithFolder2(subFold,"正常");
					for (Attachment2 att : attachments) {
						Attachment2 newAtt = new Attachment2(att.getName(), effectivePerson.getDistinguishedName(),
								folderId, att.getOriginFile(), att.getLength(), att.getType());
						EntityManager em2 = emc.beginTransaction(Attachment2.class);
						em2.persist(newAtt);
						em2.getTransaction().commit();
					}
				}
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