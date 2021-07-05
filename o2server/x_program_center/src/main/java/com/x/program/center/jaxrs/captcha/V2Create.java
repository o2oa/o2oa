package com.x.program.center.jaxrs.captcha;

import java.awt.Font;
import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.wf.captcha.SpecCaptcha;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Captcha;

class V2Create extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Create.class);

	ActionResult<Wo> execute(Integer width, Integer height) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (width <= 80 && width > 800) {
				width = 80;
			}
			if (width <= 30 && width > 800) {
				width = 30;
			}

			SpecCaptcha specCaptcha = new SpecCaptcha(width, height, 4);
			specCaptcha.setCharType(com.wf.captcha.base.Captcha.TYPE_ONLY_NUMBER);

			if (StringUtils.isNotBlank(Config.person().getCaptchaFont())) {
				specCaptcha.setFont(new Font(Config.person().getCaptchaFont(), Font.PLAIN, 32));
			}

			emc.beginTransaction(Captcha.class);
			Captcha captcha = new Captcha();
			captcha.setAnswer(specCaptcha.text());
			emc.persist(captcha, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(captcha.getId());
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				wo.setImage(specCaptcha.toBase64(""));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo {

		@FieldDescribe("标识")
		private String id;

		@FieldDescribe("图像")
		private String image;

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}
}
