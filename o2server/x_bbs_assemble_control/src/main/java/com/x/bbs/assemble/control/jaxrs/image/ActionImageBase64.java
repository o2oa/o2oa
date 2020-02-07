package com.x.bbs.assemble.control.jaxrs.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.image.exception.ExceptionURLEmpty;
import com.x.bbs.assemble.control.jaxrs.image.exception.ExceptionWrapInConvert;

import net.sf.ehcache.Element;

public class ActionImageBase64 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionImageBase64.class);
	private String catchNamePrefix = this.getClass().getName();

	protected ActionResult<String> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<String> result = new ActionResult<>();
		Wi wrapIn = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String wrap = null;
		URL url = null;
		BufferedImage image = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if (check) {
			if (wrapIn.getUrl() != null || wrapIn.getUrl().isEmpty()) {
				check = false;
				Exception exception = new ExceptionURLEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getSize() != null || wrapIn.getSize() == 0) {
				wrapIn.setSize(800);
			}
		}

		String cacheKey = catchNamePrefix + "#url#" + wrapIn.getUrl() + "#size#" + wrapIn.getSize();
		Element element = null;
		element = cache.get(cacheKey);
		if (element != null) {
			wrap = (String) element.getObjectValue();
			result.setData(wrap);
		} else {
			if (check) {
				try {
					url = new URL(wrapIn.getUrl());
				} catch (MalformedURLException e) {
					check = false;
					Exception exception = new ExceptionURLEmpty(e, wrapIn.getUrl());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
			if (check) {
				try {
					image = ImageIO.read(url);
					if (image == null) {
						check = false;
						result.error(new Exception("system can not read image in url."));
					}
				} catch (IOException e) {
					check = false;
					result.error(e);
					logger.warn("system read picture with url got an exception!url:" + url);
					logger.error(e);
				}
			}
			if (check) {
				int width = image.getWidth();
				int height = image.getHeight();
				if (width * height > wrapIn.getSize() * wrapIn.getSize()) {
					image = Scalr.resize(image, wrapIn.getSize());
				}
			}
			if (check) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					ImageIO.write(image, "png", baos);
					wrap = Base64.encodeBase64String(baos.toByteArray());
					cache.put(new Element(cacheKey, wrap));
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					result.error(e);
					logger.warn("system encode picture in base64 got an exception!");
					logger.error(e);
				}
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