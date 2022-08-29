package com.x.program.center.jaxrs.appstyle;

import com.x.base.core.project.config.AppStyle.Image;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


/**
 * 应用页面顶部图片替换
 * 730 * 390
 */
class ActionImageApplicationTop extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (!effectivePerson.isManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			BufferedImage image = ImageIO.read(bais);
			BufferedImage scalrImage = Scalr.resize(image, Method.QUALITY, 730, 390);
			ImageIO.write(scalrImage, "png", baos);
			String value = Base64.encodeBase64String(baos.toByteArray());
			Image o = Image.application_top();
			o.setValue(value);
			// 由于getImages设置了检查,所以只能对images进行处理
			Set<Image> images = Config.appStyle().getImages();
			images = images.stream().filter(img -> (!StringUtils.equals(img.getName(), Image.name_application_top)))
					.collect(Collectors.toSet());
			images.add(o);
			Config.appStyle().setImages(new TreeSet<>(images));
			Config.appStyle().save();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			Config.flush();
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}