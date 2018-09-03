package o2.collect.assemble.jaxrs.captcha;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.StringTools;

import net.sf.ehcache.Element;
import nl.captcha.Captcha;
import nl.captcha.text.producer.NumbersAnswerProducer;
import o2.collect.assemble.Business;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(Integer width, Integer height) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			if (width <= 80 && width > 800) {
				width = 80;
			}
			if (width <= 30 && width > 800) {
				width = 30;
			}
			Captcha captcha = new Captcha.Builder(width, height).addText(new NumbersAnswerProducer(4)).addNoise()
					.build();
			Wo wo = new Wo();
			wo.setKey(StringTools.uniqueToken());
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(captcha.getImage(), "png", baos);
				wo.setImage(Base64.encodeBase64String(baos.toByteArray()));
			}
			business.captchaCache().put(new Element(wo.getKey(), captcha.getAnswer()));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("关键字")
		private String key;

		@FieldDescribe("base64图像编码")
		private String image;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

	}

}