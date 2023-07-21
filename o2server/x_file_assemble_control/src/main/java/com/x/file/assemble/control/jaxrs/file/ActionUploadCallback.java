package com.x.file.assemble.control.jaxrs.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.x.base.core.project.tools.FileTools;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

class ActionUploadCallback extends BaseAction {

	ActionResult<Wo<WoObject>> execute(EffectivePerson effectivePerson, String referenceType, String reference,
			Integer scale, String callback, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			ActionResult<Wo<WoObject>> result = new ActionResult<>();
			ReferenceType type = EnumUtils.getEnum(ReferenceType.class, referenceType);
			StorageMapping mapping = ThisApplication.context().storageMappings().random(File.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMaapingCallback(callback);
			}
			/** 由于这里需要根据craeteTime创建path,先进行赋值,再进行校验,最后保存 */
			/** 禁止不带扩展名的文件上传 */
			/** 文件名编码转换 */
			String fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
					DefaultCharset.charset);
			fileName = FilenameUtils.getName(fileName);
			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtensionCallback(callback, fileName);
			}
			FileTools.verifyConstraint(bytes.length, fileName, null);
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
			WoObject woObject = new WoObject();
			woObject.setId(file.getId());
			Wo<WoObject> wo = new Wo<>(callback, woObject);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {
	}
}
