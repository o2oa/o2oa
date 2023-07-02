package com.x.file.assemble.control.jaxrs.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

class ActionUploadOctetStream extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String referenceType, String reference, Integer scale,
			byte[] bytes) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			ActionResult<Wo> result = new ActionResult<>();
			ReferenceType type = EnumUtils.getEnum(ReferenceType.class, referenceType);
			StorageMapping mapping = ThisApplication.context().storageMappings().random(File.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMaaping();
			}
			String fileName = "image.jpg";
			File file = new File(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), type, reference);
			emc.check(file, CheckPersistType.all);
			if ((scale > 0) && ArrayUtils.contains(IMAGE_EXTENSIONS_COMPRESS, file.getExtension())) {
				/** 如果是需要压缩的附件 */
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					BufferedImage image = ImageIO.read(in);
					if (image.getWidth() > scale) {
						/** 图像的实际大小比scale大的要进行压缩 */
						BufferedImage scalrImage = Scalr.resize(image, Method.QUALITY, Mode.FIT_TO_WIDTH, scale);
						ImageIO.write(scalrImage, file.getExtension(), baos);
					} else {
						/** 图像的实际大小比scale小,保存原图不进行压缩 */
						ImageIO.write(image, file.getExtension(), baos);
					}
					try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
						file.saveContent(mapping, bais, fileName);
					}
				}
			} else {
				file.saveContent(mapping, in, fileName);
			}
			emc.beginTransaction(File.class);
			emc.persist(file);
			emc.commit();
			CacheManager.notify(File.class);
			Wo wo = new Wo();
			wo.setId(file.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
