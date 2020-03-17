package com.x.file.assemble.control.jaxrs.recycle;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
import java.util.List;

class ActionDelete extends BaseAction {

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
				EntityManager aem = emc.beginTransaction(Attachment2.class);
				emc.delete(Attachment2.class, recycle.getFileId());
				aem.getTransaction().commit();
			}else{
				Folder2 folder = emc.find(recycle.getFileId(), Folder2.class);
				if(folder!=null) {
					List<String> ids = new ArrayList<>();
					ids.add(folder.getId());
					ids.addAll(business.folder2().listSubNested(folder.getId(), null));
					for (int i = ids.size() - 1; i >= 0; i--) {
						List<Attachment2> attachments = business.attachment2().listWithFolder2(ids.get(i),null);
						for (Attachment2 att : attachments) {
							EntityManager aem = emc.beginTransaction(Attachment2.class);
							aem.remove(att);
							aem.getTransaction().commit();
						}
						EntityManager fem = emc.beginTransaction(Folder2.class);
						emc.delete(Folder2.class, ids.get(i));
						fem.getTransaction().commit();
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