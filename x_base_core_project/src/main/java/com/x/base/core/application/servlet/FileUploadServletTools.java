package com.x.base.core.application.servlet;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;

public class FileUploadServletTools {
	public static String getURIPart(String uri, String prefix) throws Exception {
		Pattern pattern = Pattern.compile("/" + prefix + "/(\\S+)$");
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
		// throw new Exception("can not get part on uri:" + uri);
	}

	public static String getURIPart(String uri, String prefix, String postfix) throws Exception {
		Pattern pattern = Pattern.compile("/" + prefix + "/(\\S+)$");
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			return StringUtils.substringBeforeLast(matcher.group(1), "/" + postfix);
		} else {
			return "";
		}
	}

	public static long transfer(InputStream input, OutputStream output) throws Exception {
		byte[] buffer = new byte[FileUploadServletTools.DefaultBufferSize];
		long length = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			output.flush();
			length += n;
		}
		return length;
	}

	public static void result(HttpServletResponse response, ActionResult<?> actionResult) {
		try {
			response.setHeader("Content-Type", "application/json; charset=UTF-8");
			response.getWriter().print(actionResult.toJson());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFileName(String str) {
		String name = str;
		if (StringUtils.contains(name, "/")) {
			name = StringUtils.substringAfterLast(name, "/");
		}
		if (StringUtils.contains(name, "\\")) {
			name = StringUtils.substringAfterLast(name, "\\");
		}
		return name;
	}

	public static EffectivePerson effectivePerson(HttpServletRequest request) throws Exception {
		Object o = request.getAttribute(HttpToken.X_Person);
		if (null != o) {
			return (EffectivePerson) o;
		} else {
			return null;
		}
	}

	public static final int DefaultBufferSize = 1024 * 32;

	public static final int EOF = -1;

}