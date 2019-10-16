package com.x.file.assemble.control.jaxrs.recycle;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Recycle;
import com.x.file.core.entity.personal.Share;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ActionResume extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Recycle recycle = emc.find(id, Recycle.class);
			if (null == recycle) {
				throw new ExceptionAttachmentNotExist(id);
			}
			/* 判断当前用户是否有权限访问该文件 */
			if(!effectivePerson.isManager() && !StringUtils.equals(effectivePerson.getDistinguishedName(), recycle.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			if("attachment".equals(recycle.getFileType())){
				Attachment2 att = emc.find(recycle.getFileId(), Attachment2.class);
				if(att!=null){
					EntityManager aem = emc.beginTransaction(Attachment2.class);
					att.setStatus("正常");
					aem.getTransaction().commit();
				}
			}else{
				Folder2 folder = emc.find(recycle.getFileId(), Folder2.class);
				if(folder!=null) {
					List<Folder2> folderList = new ArrayList<>();
					folderList.add(folder);
					folderList.addAll(business.folder2().listSubNested1(folder.getId(), null));
					for(Folder2 fo : folderList){
						EntityManager fem = emc.beginTransaction(Folder2.class);
						if(business.folder2().exist(effectivePerson.getDistinguishedName(), fo.getName(), fo.getSuperior(), fo.getId())){
							fo.setName(new Date().getTime() + fo.getName());
						}
						fo.setStatus("正常");
						fem.getTransaction().commit();
						List<Attachment2> attachments = business.attachment2().listWithFolder2(fo.getId(),null);
						for (Attachment2 att : attachments) {
							EntityManager aem = emc.beginTransaction(Attachment2.class);
							att.setStatus("正常");
							aem.getTransaction().commit();
						}
					}
				}
			}
			EntityManager em = emc.beginTransaction(Recycle.class);
			emc.delete(Recycle.class, recycle.getId());
			em.getTransaction().commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}