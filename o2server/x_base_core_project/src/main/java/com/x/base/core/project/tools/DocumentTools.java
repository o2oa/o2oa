package com.x.base.core.project.tools;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class DocumentTools {

	private static Logger logger = LoggerFactory.getLogger(DocumentTools.class);

	private static final String CRLF = StringUtils.CR + StringUtils.LF;
	private static final String twoHyphens = "--";

	public static final String MEDIATYPE_DOC = "application/msword";
	public static final String MEDIATYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	public static byte[] toPdf(String fileName, byte[] bytes, String stamp) throws Exception {

		Config.collect().validate();

		/*Tika tika = new Tika();
		String type = tika.detect(bytes, fileName);

		switch (Objects.toString(type, "")) {
		case MEDIATYPE_DOC:
			break;
		case MEDIATYPE_DOCX:
			break;
		default:
			throw new ExceptionUnsupportedMediaType(type);
		}*/

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
			writer.write(CRLF);
			writer.write("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ (StringUtils.isEmpty(fileName) ? StringTools.uniqueToken() : fileName) + "\"");
			writer.write(CRLF);
			writer.write("Content-Type: " + HttpMediaType.APPLICATION_OCTET_STREAM);
			writer.write(CRLF);
			writer.write(CRLF);
			writer.flush();
			out.write(bytes);
			out.flush();
			writer.write(CRLF);
			writer.write(twoHyphens + boundary);
			if (StringUtils.isNotEmpty(stamp)) {
				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"stamp\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(stamp);
				writer.write(CRLF);
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

		Config.collect().validate();

		/*Tika tika = new Tika();
		String type = tika.detect(bytes, fileName);

		switch (Objects.toString(type, "")) {
		case MEDIATYPE_DOC:
			break;
		case MEDIATYPE_DOCX:
			break;
		default:
			throw new ExceptionUnsupportedMediaType(type);
		}*/

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
			writer.write(CRLF);
			writer.write("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ (StringUtils.isEmpty(fileName) ? StringTools.uniqueToken() : fileName) + "\"");
			writer.write(CRLF);
			writer.write("Content-Type: " + HttpMediaType.APPLICATION_OCTET_STREAM);
			writer.write(CRLF);
			writer.write(CRLF);
			writer.flush();
			out.write(bytes);
			out.flush();
			writer.write(CRLF);
			writer.write(twoHyphens + boundary);
			writer.write(CRLF);
			writer.write("Content-Disposition: form-data; name=\"page\"");
			writer.write(CRLF);
			writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
			writer.write(CRLF);
			writer.write(CRLF);
			writer.write("" + ((page == null || page < 0) ? 0 : page));
			writer.write(CRLF);
			writer.write(twoHyphens + boundary);
			if (StringUtils.isNotEmpty(stamp)) {
				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"stamp\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(stamp);
				writer.write(CRLF);
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

	public static byte[] docToWord(String fileName, String content) throws Exception {

		Config.collect().validate();

		DocToWordReq req = new DocToWordReq();
		req.setName(Config.collect().getName());
		req.setPassword(Config.collect().getPassword());
		req.setFileName(fileName);
		req.setContent(content);
		ActionResponse response = ConnectionAction
				.post(Config.collect().url("/o2_collect_assemble/jaxrs/document/doc/to/word"), null, req);
		WrapString wrap = response.getData(WrapString.class);
		if (StringUtils.isNotEmpty(wrap.getValue())) {
			return Base64.decodeBase64(wrap.getValue());
		}
		return null;
	}

	public static class DocToWordReq {

		public String getName() {
			return name;
		}

		public String getPassword() {
			return password;
		}

		public String getFileName() {
			return fileName;
		}

		public String getContent() {
			return content;
		}

		private String name;
		private String password;
		private String fileName;
		private String content;

		public void setName(String name) {
			this.name = name;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	public static byte[] toHtml(String fileName, byte[] bytes) throws Exception {

		String type = FilenameUtils.getExtension(fileName);
		if(StringUtils.isEmpty(type)){
			return null;
		}else{
			type = type.toLowerCase();
		}
		String toHtmlType = "#doc#docx#xls#xlsx#ppt#pptx";
		if(toHtmlType.indexOf(type) == -1){
			return null;
		}
		try {
			if(!Config.collect().connect()){
				return null;
			}

			URL serverUrl = new URL(Config.collect().url() + "/o2_collect_assemble/jaxrs/document/to/html");

			HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

			String boundary = "----" + StringTools.uniqueToken();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			try (OutputStream out = connection.getOutputStream();
				 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
				writer.write(twoHyphens + boundary);
				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ (StringUtils.isEmpty(fileName) ? StringTools.uniqueToken() : fileName) + "\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.APPLICATION_OCTET_STREAM);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.flush();
				out.write(bytes);
				out.flush();
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"name\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(Config.collect().getName());
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"password\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(Config.collect().getPassword());
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"type\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(type);
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

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
		} catch (Exception e) {
			logger.warn(fileName+"-转换html异常："+e.getMessage());
			return null;
		}
		return null;
	}

	public static byte[] toPdf2(String fileName, byte[] bytes) throws Exception {

		String type = FilenameUtils.getExtension(fileName);
		if(StringUtils.isEmpty(type)){
			return null;
		}else{
			type = type.toLowerCase();
		}
		String toHtmlType = "#doc#docx#xls#xlsx#ppt#pptx";
		if(toHtmlType.indexOf(type) == -1){
			return null;
		}
		try {
			if(!Config.collect().connect()){
				return null;
			}

			URL serverUrl = new URL(Config.collect().url() + "/o2_collect_assemble/jaxrs/document/to/pdf2");

			HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

			String boundary = "----" + StringTools.uniqueToken();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			try (OutputStream out = connection.getOutputStream();
				 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
				writer.write(twoHyphens + boundary);
				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ (StringUtils.isEmpty(fileName) ? StringTools.uniqueToken() : fileName) + "\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.APPLICATION_OCTET_STREAM);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.flush();
				out.write(bytes);
				out.flush();
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"name\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(Config.collect().getName());
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"password\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(Config.collect().getPassword());
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

				writer.write(CRLF);
				writer.write("Content-Disposition: form-data; name=\"type\"");
				writer.write(CRLF);
				writer.write("Content-Type: " + HttpMediaType.TEXT_PLAIN);
				writer.write(CRLF);
				writer.write(CRLF);
				writer.write(type);
				writer.write(CRLF);
				writer.write(twoHyphens + boundary);

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
		} catch (Exception e) {
			logger.warn(fileName+"-转换pdf异常："+e.getMessage());
			return null;
		}
		return null;
	}


}
