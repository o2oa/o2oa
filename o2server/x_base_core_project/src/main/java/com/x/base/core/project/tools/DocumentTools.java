package com.x.base.core.project.tools;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.WrapString;

public class DocumentTools {

	private static final String CL = "\r\n";
	private static final String twoHyphens = "--";

	public static byte[] toPdf(String fileName, byte[] bytes, String stamp) throws Exception {

		URL serverUrl = new URL(Config.collect().url() + "/o2_collect_assemble/jaxrs/document/to/pdf");

		HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

		String boundary = "----" + StringTools.uniqueToken();

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (OutputStream out = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
			writer.write(twoHyphens + boundary);
			writer.write(CL);
			writer.write("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ (StringUtils.isEmpty(fileName) ? StringTools.uniqueToken() : fileName) + "\"");
			writer.write(CL);
			writer.write("Content-Type: " + HttpMediaType.APPLICATION_OCTET_STREAM);
			writer.write(CL);
			writer.write(CL);
			writer.flush();
			out.write(bytes);
			out.flush();
			writer.write(CL);
			writer.write(twoHyphens + boundary);
			if (StringUtils.isNotEmpty(stamp)) {
				writer.write(CL);
				writer.write("Content-Disposition: form-data; name=\"stamp\"");
				writer.write(CL);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CL);
				writer.write(CL);
				writer.write(stamp);
				writer.write(CL);
				writer.write(twoHyphens + boundary);
			}
			writer.write(twoHyphens);
			writer.flush();
		}

		String respText = null;

		try (InputStream in = connection.getInputStream()) {
			respText = IOUtils.toString(in, DefaultCharset.charset_utf_8);
		}

		if (StringUtils.isNotEmpty(respText)) {
			ActionResponse response = XGsonBuilder.instance().fromJson(respText, ActionResponse.class);
			WrapString wrap = XGsonBuilder.instance().fromJson(response.getData(), WrapString.class);
			return Base64.decodeBase64(wrap.getValue());
		}
		return null;

	}

	public static byte[] toImage(String fileName, byte[] bytes, String stamp, Integer page) throws Exception {

		URL serverUrl = new URL(Config.collect().url() + "/o2_collect_assemble/jaxrs/document/to/image");

		HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

		String boundary = "----" + StringTools.uniqueToken();

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (OutputStream out = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
			writer.write(twoHyphens + boundary);
			writer.write(CL);
			writer.write("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ (StringUtils.isEmpty(fileName) ? StringTools.uniqueToken() : fileName) + "\"");
			writer.write(CL);
			writer.write("Content-Type: " + HttpMediaType.APPLICATION_OCTET_STREAM);
			writer.write(CL);
			writer.write(CL);
			writer.flush();
			out.write(bytes);
			out.flush();
			writer.write(CL);
			writer.write(twoHyphens + boundary);
			writer.write(CL);
			writer.write("Content-Disposition: form-data; name=\"page\"");
			writer.write(CL);
			writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
			writer.write(CL);
			writer.write(CL);
			writer.write("" + ((page == null || page < 0) ? 0 : page));
			writer.write(CL);
			writer.write(twoHyphens + boundary);
			if (StringUtils.isNotEmpty(stamp)) {
				writer.write(CL);
				writer.write("Content-Disposition: form-data; name=\"stamp\"");
				writer.write(CL);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CL);
				writer.write(CL);
				writer.write(stamp);
				writer.write(CL);
				writer.write(twoHyphens + boundary);
			}
			writer.write(twoHyphens);
			writer.flush();
		}

		String respText = null;

		try (InputStream in = connection.getInputStream()) {
			respText = IOUtils.toString(in, DefaultCharset.charset_utf_8);
		}

		if (StringUtils.isNotEmpty(respText)) {
			ActionResponse response = XGsonBuilder.instance().fromJson(respText, ActionResponse.class);
			WrapString wrap = XGsonBuilder.instance().fromJson(response.getData(), WrapString.class);
			return Base64.decodeBase64(wrap.getValue());
		}
		return null;

	}

}