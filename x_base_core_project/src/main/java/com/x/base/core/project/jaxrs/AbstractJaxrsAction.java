package com.x.base.core.project.jaxrs;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.DefaultCharset;
import com.x.base.core.exception.JsonElementConvertToWrapInException;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.project.server.Config;

public abstract class AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AbstractJaxrsAction.class);

	protected static Gson gson = XGsonBuilder.instance();

	protected static String[] IMAGE_EXTENSIONS = new String[] { "jpg", "png", "bmp", "gif" };

	protected final static String FILE_FIELD = "file";

	public final static String EMPTY_SYMBOL = "(0)";

	protected <T> T convertToWrapIn(JsonElement jsonElement, Class<T> clz) throws Exception {
		try {
			return gson.fromJson(jsonElement, clz);
		} catch (Exception e) {
			throw new JsonElementConvertToWrapInException(e, clz);
		}
	}

	protected EffectivePerson effectivePerson(HttpServletRequest request) {
		Object o = request.getAttribute(HttpToken.X_Person);
		if (null != o) {
			return (EffectivePerson) o;
		} else {
			return EffectivePerson.anonymous();
		}
	}

	protected String contentType(Boolean stream, String fileName) throws Exception {
		String extension = FilenameUtils.getExtension(fileName);
		String type = "";
		if (BooleanUtils.isTrue(stream) || StringUtils.isEmpty(extension)) {
			type = MediaType.APPLICATION_OCTET_STREAM;
		} else {
			type = Config.mimeTypes().getMimeByExtension("." + extension);
		}
		if (StringUtils.isEmpty(type)) {
			type = MediaType.APPLICATION_OCTET_STREAM;
			logger.warn("can not find Content-Type of {}, use default Content-Type:{}.", extension, type);
		}
		return type;
	}

	protected String contentDisposition(Boolean stream, String fileName) throws Exception {
		if (BooleanUtils.isTrue(stream)) {
			return "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, DefaultCharset.name);
		} else {
			return "inline; filename*=UTF-8''" + URLEncoder.encode(fileName, DefaultCharset.name);
		}
	}

}