package com.x.program.center.jaxrs.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Captcha;

import javax.imageio.ImageIO;

class V2Create extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Create.class);

	ActionResult<Wo> execute(Integer width, Integer height) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (width <= 80 || width > 800) {
				width = 80;
			}
			if (height <= 30 || height > 800) {
				height = 30;
			}
			Producer producer = createProducer(width, height);
			// 生成随机字符串
			String verifyCode = producer.createText();
			// 生成图片
			BufferedImage bufferedImage = producer.createImage(verifyCode);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", stream);

			emc.beginTransaction(Captcha.class);
			Captcha captcha = new Captcha();
			captcha.setAnswer(verifyCode);
			emc.persist(captcha, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(captcha.getId());
			wo.setImage(Base64.getEncoder().encodeToString(stream.toByteArray()));
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

	private static Producer createProducer(Integer width, Integer height) {
		Properties properties = new Properties();
		properties.setProperty(Constants.KAPTCHA_BORDER, "no");
		properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, "105,179,90");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");
		properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, width.toString());
		properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, height.toString());
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "40");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "宋体,楷体,微软雅黑");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "1234567890");
		//properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "5");
		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}
