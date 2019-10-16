package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Share;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String password) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Share share = emc.find(id, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(id);
			}
			/* 判断当前用户是否有权限访问该文件 */
			if(!effectivePerson.isManager() && !StringUtils.equals(effectivePerson.getDistinguishedName(), share.getPerson())) {
				if (!"password".equals(share.getShareType())) {
					if (!hasPermission(business, effectivePerson, share)) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
				} else {
					if (StringUtils.isEmpty(password)) {
						throw new Exception("password can not be empty.");
					}
					if (!password.equalsIgnoreCase(share.getPassword())) {
						throw new Exception("invalid password.");
					}
				}
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

		static WrapCopier<Share, Wo> copier = WrapCopierFactory.wo(Share.class, Wo.class, null,
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
