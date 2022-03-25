package com.x.program.center.jaxrs.invoke;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Invoke;

class ActionUpdate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/* 判断当前用户是否有权限访问 */
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Invoke invoke = emc.flag(flag, Invoke.class);
			if (null == invoke) {
				throw new ExceptionInvokeNotExist(flag);
			}
			String text = new String(bytes, DefaultCharset.name);
			emc.beginTransaction(Invoke.class);
			invoke.setText(text);
			// this.addComment(invoke);
			emc.commit();
			wo.setId(invoke.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
