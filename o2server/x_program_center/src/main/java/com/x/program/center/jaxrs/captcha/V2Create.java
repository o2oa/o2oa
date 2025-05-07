package com.x.program.center.jaxrs.captcha;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Quadruple;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Captcha;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

class V2Create extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Create.class);

	ActionResult<Wo> execute(Integer width, Integer height) throws Exception {
		LOGGER.debug("execute create captcha.");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			final int adjustWidth = (width <= 80 || width > 800) ? 80 : width;
			final int adjustHeight = (height <= 30 || height > 800) ? 30 : height;
			Quadruple<Integer, Integer, BufferedImage, Integer> quadruple = CaptchaGenerator.of(adjustWidth,
					adjustHeight).generator();
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
				ImageIO.write(quadruple.third(), "png", out);
				byte[] bytes = out.toByteArray();
				emc.beginTransaction(Captcha.class);
				Captcha captcha = new Captcha();
				captcha.setAnswer(String.valueOf(quadruple.fourth()));
				emc.persist(captcha, CheckPersistType.all);
				emc.commit();
				Wo wo = new Wo();
				wo.setId(captcha.getId());
				wo.setImage(Base64.getEncoder().encodeToString(bytes));
				result.setData(wo);
				return result;
			}
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
