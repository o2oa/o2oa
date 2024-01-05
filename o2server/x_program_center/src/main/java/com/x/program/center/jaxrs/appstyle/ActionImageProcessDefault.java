package com.x.program.center.jaxrs.appstyle;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import com.x.base.core.project.config.AppStyle.Image;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.Business;

/*90 *90*/
class ActionImageProcessDefault extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition, String fileName)
			throws Exception {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (!effectivePerson.isManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			String file = fileName;
			if (StringUtils.isEmpty(file)) {
				file = this.fileName(disposition);
			}
			boolean flag = !StringTools.isFileName(file) || !file.toLowerCase().endsWith(".png")
					|| (bytes == null || bytes.length == 0);
			if (flag) {
				throw new ExceptionIllegalFile(file);
			}
			BufferedImage image = ImageIO.read(bais);
			BufferedImage scalrImage = Scalr.resize(image, Method.QUALITY, 90, 90);
			ImageIO.write(scalrImage, "png", baos);
			String parentFolder = Image.default_image_folder_path;
			// 上传图片到 webserver 指定目录
			Business.dispatch(true, file, parentFolder, baos.toByteArray());

			
			Image o = Image.process_default();
			o.setValue(null);
			o.setPath(parentFolder + "/" + file);
			// 由于getImages设置了检查,所以只能对images进行处理
			Set<Image> images = Config.appStyle().getImages();
			images = images.stream().filter(img -> (!StringUtils.equals(img.getName(), Image.name_process_default)))
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