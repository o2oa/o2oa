package com.x.pan.assemble.control.jaxrs.recycle;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Recycle3;
import org.apache.commons.lang3.StringUtils;

class ActionGet extends BaseAction {

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

			Wo wo = Wo.copier.copy(recycle);
			wo.setContentType(this.contentType(false, wo.getName()));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Recycle3 {

		private static final long serialVersionUID = -3276772472130111535L;

		static WrapCopier<Recycle3, Wo> copier = WrapCopierFactory.wo(Recycle3.class, Wo.class, null,
				FieldsInvisible);

		@FieldDescribe("文件类型")
		private String contentType;

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

	}
}
