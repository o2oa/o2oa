package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.file.core.entity.open.FileType;
import com.x.file.core.entity.open.OriginFile;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.Attachment3;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Optional;

class ActionGetImageWidthHeightBase64 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer width, Integer height)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment.getFolder() : attachment.getZoneId();
			if(!business.zoneViewable(effectivePerson, zoneId)){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			if (!FileType.getExtType(attachment.getExtension()).equals(FileType.image.name())) {
				throw new Exception("attachment not image file.");
			}
			OriginFile originFile = emc.find(attachment.getOriginFile(), OriginFile.class);
			if (null == originFile) {
				throw new ExceptionAttachmentNotExist(id,attachment.getOriginFile());
			}
			CacheCategory cacheCategory = new CacheCategory(Attachment3.class);
			CacheKey cacheKey = new CacheKey(this.getClass(), id+width+height);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				Wo wo = (Wo) optional.get();
				result.setData(wo);
			} else {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
						originFile.getStorage());
				try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
					originFile.readContent(mapping, output);
					try (ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray())) {
						BufferedImage src = ImageIO.read(input);
						int scalaWidth = (width <= 0 || width > src.getWidth()) ? src.getWidth() : width;
						int scalaHeight = (height <= 0 || height > src.getHeight()) ? src.getHeight() : height;
						Scalr.Mode mode = Scalr.Mode.FIT_TO_WIDTH;
						if(src.getWidth()>src.getHeight()){
							mode = Scalr.Mode.FIT_TO_HEIGHT;
						}
						BufferedImage scalaImage = Scalr.resize(src,Scalr.Method.SPEED, mode, NumberUtils.min(scalaWidth, src.getWidth()),
								NumberUtils.min(scalaHeight, src.getHeight()));
						try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							ImageIO.write(scalaImage, "png", baos);
							String str = Base64.encodeBase64String(baos.toByteArray());
							Wo wo = new Wo();
							wo.setValue(str);
							CacheManager.put(cacheCategory, cacheKey, wo);
							result.setData(wo);
						}
					}
				}
			}

			return result;
		}
	}

	public static class Wo extends WrapString {

	}
}
