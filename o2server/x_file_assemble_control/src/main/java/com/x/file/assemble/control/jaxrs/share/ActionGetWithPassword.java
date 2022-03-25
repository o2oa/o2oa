package com.x.file.assemble.control.jaxrs.share;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.personal.Share;

class ActionGetWithPassword extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String password) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Share share = emc.find(id, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if(Share.SHARE_TYPE_PASSWORD.equals(share.getShareType())){
				if (StringUtils.isEmpty(password)) {
					throw new ExceptionFieldEmpty(Share.password_FIELDNAME);
				}
				if(!password.equalsIgnoreCase(share.getPassword())){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}else{
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Wo wo = Wo.copier.copy(share);
			wo.setContentType(this.contentType(false, wo.getName()));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Share {

		private static final long serialVersionUID = -531053101150157872L;

		public static final List<String> FieldsInvisible2 = ListUtils.unmodifiableList(
				Arrays.asList(distributeFactor_FIELDNAME, sequence_FIELDNAME, scratchString_FIELDNAME,
						scratchBoolean_FIELDNAME, scratchDate_FIELDNAME, scratchInteger_FIELDNAME));

		static final WrapCopier<Share, Wo> copier = WrapCopierFactory.wo(Share.class, Wo.class, null,
				FieldsInvisible2);

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
