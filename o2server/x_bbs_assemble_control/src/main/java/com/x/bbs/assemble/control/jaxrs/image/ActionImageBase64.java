package com.x.bbs.assemble.control.jaxrs.image;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.image.exception.ExceptionURLEmpty;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Optional;

/**
 * 图片转base64
 * @author sword
 */
public class ActionImageBase64 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionImageBase64.class);

	protected ActionResult<String> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<String> result = new ActionResult<>();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		String wrap;

		if(StringUtils.isBlank(wrapIn.getUrl())){
			throw new ExceptionURLEmpty();
		}
		if(wrapIn.getSize() == null || wrapIn.getSize() < 1){
			wrapIn.setSize(800);
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), wrapIn.getUrl(), wrapIn.getSize());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
		if( optional.isPresent() ){
			wrap = (String) optional.get();
			result.setData(wrap);
		} else {
			URL url = new URL(wrapIn.getUrl());
			BufferedImage image = ImageIO.read(url);
			int width = image.getWidth();
			int height = image.getHeight();
			if (width * height > wrapIn.getSize() * wrapIn.getSize()) {
				image = Scalr.resize(image, wrapIn.getSize());
			}
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				ImageIO.write(image, "png", out);
				wrap = Base64.encodeBase64String(out.toByteArray());
				CacheManager.put( cacheCategory, cacheKey, wrap );
				result.setData(wrap);
			}
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("地址")
		private String url;

		@FieldDescribe("像素大小")
		private Integer size;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

	}
}
