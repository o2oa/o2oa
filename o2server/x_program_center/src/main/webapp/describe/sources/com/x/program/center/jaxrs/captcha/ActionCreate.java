package com.x.program.center.jaxrs.captcha;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Captcha;

import nl.captcha.text.producer.NumbersAnswerProducer;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	// // 验证码颜色
	// private Color _CodeColor = Color.BLACK;
	// // 使用字体名字
	// private String _FontName = "System";
	// // 使用字体类型
	// private int _FontType = Font.BOLD;
	// // 使用字体大小
	// private int _FontSize = 40;

	ActionResult<Wo> execute(Integer width, Integer height) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (width <= 80 && width > 800) {
				width = 80;
			}
			if (width <= 30 && width > 800) {
				width = 30;
			}
			// List<Font> fontList = new ArrayList<Font>();
			// List<Color> colorList = new ArrayList<Color>();
			// colorList.add(_CodeColor);
			// fontList.add(new Font(_FontName, _FontType, _FontSize));
			//
			// WordRenderer dwr = new ColoredEdgesWordRenderer(colorList, fontList);

			nl.captcha.Captcha o = new nl.captcha.Captcha.Builder(width, height).addText(new NumbersAnswerProducer(4))
					.addNoise().addNoise().build();
			emc.beginTransaction(Captcha.class);
			Captcha captcha = new Captcha();
			captcha.setAnswer(o.getAnswer());
			emc.persist(captcha, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(captcha.getId());
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(o.getImage(), "png", baos);
				wo.setImage(Base64.encodeBase64String(baos.toByteArray()));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo {

		private String id;

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
