package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.imgscalr.Scalr;

class ActionDownloadScale extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, boolean stream) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			File file = emc.find(id, File.class);
			if (null == file) {
				file = emc.flag(id, File.class);
				if (file == null) {
					throw new ExceptionEntityNotExist(id);
				}
			}
			if (effectivePerson.isNotManager() && !effectivePerson.getDistinguishedName()
					.equals(file.getCreator())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			int scale = 30;
			byte[] bytes;
			StorageMapping mapping = ThisApplication.context().storageMappings().get(File.class,
					file.getStorage());
			try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
				file.readContent(mapping, output);
				if(file.getLength() > 300 * 1024) {
					if(file.getLength() < 1000 * 1024){
						scale = 50;
					}
					try (ByteArrayInputStream input = new ByteArrayInputStream(
							output.toByteArray())) {
						BufferedImage src = ImageIO.read(input);
						int width = (src.getWidth() * scale) / 100;
						int height = (src.getHeight() * scale) / 100;
						BufferedImage scaleImage = Scalr.resize(src, width, height);
						try (ByteArrayOutputStream output2 = new ByteArrayOutputStream()) {
							ImageIO.write(scaleImage, "png", output2);
							bytes = output2.toByteArray();
						}
					}
				}else{
					bytes = output.toByteArray();
				}
			}
			String fastETag = file.getId() + file.getUpdateTime().getTime();
			Wo wo = new Wo(bytes, this.contentType(stream, file.getName()),
					this.contentDisposition(stream, file.getName()), fastETag);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition, String fastETag) {
			super(bytes, contentType, contentDisposition, fastETag);
		}

	}
}
