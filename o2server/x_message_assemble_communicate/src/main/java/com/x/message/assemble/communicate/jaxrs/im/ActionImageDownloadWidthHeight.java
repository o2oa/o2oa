package com.x.message.assemble.communicate.jaxrs.im;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsgFile;

/**
 * Created by fancyLou on 2020-06-15. Copyright © 2020 O2. All rights reserved.
 */
public class ActionImageDownloadWidthHeight extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionImageDownloadWidthHeight.class);

	private static String[] imageExtentionArray = new String[] { "jpg", "png", "bmp", "jpeg" };
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer width, Integer height)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}, width:{}, height:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> width, () -> height);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			IMMsgFile file = emc.find(id, IMMsgFile.class);
			if (null == file) {
				throw new ExceptionFileNotExist(id);
			}
			if (!ArrayUtils.contains(imageExtentionArray, file.getExtension().toLowerCase())) {
				throw new IllegalStateException("file is not image file.");
			}
			if (width < 0 || width > 5000) {
				throw new IllegalStateException("invalid width:" + width + ".");
			}
			if (height < 0 || height > 5000) {
				throw new IllegalStateException("invalid height:" + height + ".");
			}

			CacheCategory cacheCategory = new CacheCategory(ActionImageDownloadWidthHeight.class);
			CacheKey cacheKey = new CacheKey(this.getClass(), id + width + height);

			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

			if (optional.isPresent()) {
				wo = (Wo) optional.get();
				result.setData(wo);
			} else {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(IMMsgFile.class,
						file.getStorage());
				if (null == mapping) {
					throw new ExceptionStorageNotExist(file.getStorage());
				}
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					file.readContent(mapping, os);
					try {
						ByteArrayInputStream input = new ByteArrayInputStream(os.toByteArray());
						BufferedImage src = ImageIO.read(input);
						int scalrWidth = (width == 0) ? src.getWidth() : width;
						int scalrHeight = (height == 0) ? src.getHeight() : height;
						Scalr.Mode mode = Scalr.Mode.FIT_TO_WIDTH;
						if (src.getWidth() > src.getHeight()) {
							mode = Scalr.Mode.FIT_TO_HEIGHT;
						}
						BufferedImage scalrImage = Scalr.resize(src, Scalr.Method.SPEED, mode,
								NumberUtils.min(scalrWidth, src.getWidth()),
								NumberUtils.min(scalrHeight, src.getHeight()));
						try (org.apache.commons.io.output.ByteArrayOutputStream baos = new org.apache.commons.io.output.ByteArrayOutputStream()) {
							ImageIO.write(scalrImage, "png", baos);
							byte[] bs = baos.toByteArray();
							wo = new Wo(bs, this.contentType(false, file.getName()),
									this.contentDisposition(false, file.getName()));
							CacheManager.put(cacheCategory, cacheKey, wo);
							result.setData(wo);
						}
					} catch (Exception ex) { // 图片转化异常的情况 直接返回整个文件
						LOGGER.warn("图片转化异常", ex);
						byte[] bs = os.toByteArray();
						wo = new Wo(bs, this.contentType(false, file.getName()),
								this.contentDisposition(false, file.getName()));
						// 对10M以下的文件进行缓存
						if (bs.length < (1024 * 1024 * 10)) {
							CacheManager.put(cacheCategory, cacheKey, wo);
						}
						result.setData(wo);
					}
				} catch (Exception e) {
					if (e.getMessage() != null && e.getMessage().contains("existed")) {
						LOGGER.warn("原始附件{}-{}不存在，删除记录！", file.getId(), file.getName());
						emc.beginTransaction(IMMsgFile.class);
						emc.delete(IMMsgFile.class, file.getId());
						emc.commit();
					}
					throw e;
				}
			}
			return result;
		}
	}

	public static class Wo extends WoFile {

		private static final long serialVersionUID = -275816509098883542L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}
