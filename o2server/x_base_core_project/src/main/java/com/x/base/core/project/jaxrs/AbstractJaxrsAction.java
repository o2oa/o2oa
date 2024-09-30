package com.x.base.core.project.jaxrs;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.JsonElementConvertToWrapInException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

abstract class AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AbstractJaxrsAction.class);

	protected static Gson gson = XGsonBuilder.instance();

	protected static String[] IMAGE_EXTENSIONS = new String[] { "jpg", "png", "bmp", "gif" };

	protected final static String FILE_FIELD = "file";
	protected final static String FILENAME_FIELD = "fileName";

	public final static String EMPTY_SYMBOL = "(0)";

	protected <T> T convertToWrapIn(JsonElement jsonElement, Class<T> clz) throws Exception {
		try {
			if (null == jsonElement || jsonElement.isJsonNull()) {
				return clz.newInstance();
			}
			return gson.fromJson(jsonElement, clz);
		} catch (Exception e) {
			throw new JsonElementConvertToWrapInException(e, clz);
		}
	}

	protected EffectivePerson effectivePerson(HttpServletRequest request) {
		Object o = request.getAttribute(HttpToken.X_PERSON);
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
			type = Config.mimeTypes(extension);
		}
		return type;
	}

	protected String contentDisposition(Boolean stream, String fileName) throws Exception {
		String encode = URLEncoder.encode(fileName, DefaultCharset.name).replaceAll("\\+", "%20");
		if (BooleanUtils.isTrue(stream)) {
			return "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encode;
		} else {
			return "inline; filename=\"" + fileName + "\"; filename*=UTF-8''" + encode;
		}
	}

	protected String fileName(FormDataContentDisposition disposition) {
		String fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
				DefaultCharset.charset);
		fileName = FilenameUtils.getName(fileName);
		return fileName;

	}

}
