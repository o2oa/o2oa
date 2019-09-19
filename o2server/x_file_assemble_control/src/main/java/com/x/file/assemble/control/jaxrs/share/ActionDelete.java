package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Share;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Share share = emc.find(id, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(id);
			}
			/* 判断文件的所有者是否是当前用户 */
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), share.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			EntityManager em = emc.beginTransaction(Share.class);
			emc.delete(Share.class, share.getId());
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