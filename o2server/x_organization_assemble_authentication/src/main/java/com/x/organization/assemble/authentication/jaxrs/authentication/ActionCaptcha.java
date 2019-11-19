package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCaptcha;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;

class ActionCaptcha extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCaptcha.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Integer width, Integer height) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//Audit audit = logger.audit(effectivePerson);
			Business business = new Business(emc);
			WoCaptcha wrap = business.instrument().captcha().create(width, height);
			Wo wo = new Wo();
			wo.setId(wrap.getId());
			wo.setImage(wrap.getImage());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("id.")
		private String id;
		@FieldDescribe("base64图片值.")
		private String image;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

	}
}
