package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

class ActionGetImageScaleBase64 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer scale) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Attachment2 attachment = emc.find(id, Attachment2.class, ExceptionWhen.not_found);
			/* 判断文件的当前用户是否是管理员或者文件创建者 或者当前用户在分享或者共同编辑中 */
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(attachment.getPerson())) {
				throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} access attachment{id:"
						+ id + "} denied.");
			}
			if (!ArrayUtils.contains(IMAGE_EXTENSIONS, attachment.getExtension())) {
				throw new Exception("attachment not image file.");
			}
			if (scale < 0 || scale > 100) {
				throw new Exception("invaild scale:" + scale + ".");
			}
			OriginFile originFile = emc.find(attachment.getOriginFile(),OriginFile.class);
			if (null == originFile) {
				throw new ExceptionAttachmentNotExist(id,attachment.getOriginFile());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
					originFile.getStorage());
			try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
				originFile.readContent(mapping, output);
				try (ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray())) {
					BufferedImage src = ImageIO.read(input);
					int width = (src.getWidth() * scale) / (int) 100;
					int height = (src.getHeight() * scale) / (int) 100;
					BufferedImage scalrImage = Scalr.resize(src, width, height);
					try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
						ImageIO.write(scalrImage, "png", baos);
						String str = Base64.encodeBase64String(baos.toByteArray());
						Wo wo = new Wo();
						wo.setValue(str);
						result.setData(wo);
					}
				}
			}
			return result;
		}
	}

	public static class Wo extends WrapString {
	}
}