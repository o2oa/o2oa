package com.x.portal.assemble.surface.jaxrs.portal;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;

class ActionIcon extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Portal o = business.portal().pick(id);
			if (null == o) {
				throw new ExceptionPortalNotExist(id);
			}
			// if (!business.portal().visible(effectivePerson, o)) {
			// throw new
			// ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(),
			// o.getName(), o.getId());
			// }
			byte[] bs = Base64
					.decodeBase64(StringUtils.isEmpty(o.getIcon()) ? DEFAULT_PORTAL_ICON_BASE64 : o.getIcon());
			Wo wo = new Wo(bs, this.contentType(false, "icon.png"), this.contentDisposition(false, "icon.png"));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
