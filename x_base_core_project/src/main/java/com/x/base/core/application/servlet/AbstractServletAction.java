package com.x.base.core.application.servlet;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.HttpToken;
import com.x.base.core.project.server.Config;

public abstract class AbstractServletAction extends HttpServlet {

	private static final long serialVersionUID = 3167094373222800192L;

	protected static String[] IMAGE_EXTENSIONS = new String[] { "jpg", "png", "bmp", "gif" };

	protected void setCharacterEncoding(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding(DefaultCharset.name);
		response.setCharacterEncoding(DefaultCharset.name);
	}

	protected String getURIPart(String uri, String prefix) throws Exception {
		Pattern pattern = Pattern.compile("/" + prefix + "/(\\S+?)(/|$)");
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	// protected String getURIPart(String uri, String prefix, String postfix)
	// throws Exception {
	// Pattern pattern = Pattern.compile("/" + prefix + "/(\\S+)$");
	// Matcher matcher = pattern.matcher(uri);
	// if (matcher.find()) {
	// return StringUtils.substringBeforeLast(matcher.group(1), "/" + postfix);
	// } else {
	// return "";
	// }
	// }

	protected long transfer(InputStream input, OutputStream output) throws Exception {
		byte[] buffer = new byte[AbstractServletAction.DefaultBufferSize];
		long length = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			output.flush();
			length += n;
		}
		return length;
	}

	protected void result(HttpServletResponse response, ActionResult<?> actionResult) {
		try {
			response.setHeader("Content-Type", HttpMediaType.APPLICATION_JSON_UTF_8);
			response.getWriter().print(actionResult.toJson());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String getFileName(String str) {
		String name = str;
		if (StringUtils.contains(name, "/")) {
			name = StringUtils.substringAfterLast(name, "/");
		}
		if (StringUtils.contains(name, "\\")) {
			name = StringUtils.substringAfterLast(name, "\\");
		}
		return name;
	}

	protected EffectivePerson effectivePerson(HttpServletRequest request) {
		Object o = request.getAttribute(HttpToken.X_Person);
		if (null != o) {
			return (EffectivePerson) o;
		} else {
			return EffectivePerson.anonymous();
		}
	}

	protected boolean isMultipartContent(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String CONTENT_LENGTH = "Content-Length";

	protected void setResponseHeader(HttpServletResponse response, StorageObject o, boolean streamContentType)
			throws Exception {
		if (streamContentType || StringUtils.isEmpty(o.getExtension())) {
			response.setHeader(CONTENT_TYPE, "application/octet-stream");
			response.setHeader(CONTENT_DISPOSITION,
					"attachment; filename=" + URLEncoder.encode(o.getName(), DefaultCharset.name));
		} else {
			response.setHeader(CONTENT_TYPE, Config.mimeTypes().getMimeByExtension("." + o.getExtension()));
			response.setHeader(CONTENT_DISPOSITION,
					"inline; filename=" + URLEncoder.encode(o.getName(), DefaultCharset.name));
		}
		response.setIntHeader(CONTENT_LENGTH, o.getLength().intValue());
	}

	protected FileItemIterator getItemIterator(HttpServletRequest request) throws Exception {
		return new ServletFileUpload().getItemIterator(request);
	}

	private static final int DefaultBufferSize = 1024 * 32;

	private static final int EOF = -1;

}