package com.x.program.center.jaxrs.mpweixin;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Mpweixin;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

/**
 * 上传素材到微信 永久的素材 Created by fancyLou on 2022/3/1. Copyright © 2022 O2. All
 * rights reserved.
 */
public class ActionUploadMediaForever extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUploadMediaForever.class);

	ActionResult<Wo> execute(String type, String fileName, byte[] bytes, FormDataContentDisposition disposition,
			String videoTitle, String videoIntroduction) throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		if (StringUtils.isEmpty(type)) {
			throw new ExceptionNotEmpty("type");
		}
		type = type.toLowerCase();
		if (!"image".equals(type) && !"voice".equals(type) && !"video".equals(type) && !"thumb".equals(type)) {
			throw new ExceptionMediaTypeNotSupport();
		}
		/** 文件名编码转换 */
		if (StringUtils.isEmpty(fileName)) {
			try {
				fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
						DefaultCharset.charset);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		fileName = FilenameUtils.getName(fileName);
		if (StringUtils.isEmpty(fileName)) {
			throw new ExceptionNotEmpty("fileName");
		}
		// 视频需要上传 title 和 introduction
		if ("video".equalsIgnoreCase(type)) {
			if (StringUtils.isEmpty(videoTitle)) {
				throw new ExceptionNotEmpty("videoTitle");
			}
			if (StringUtils.isEmpty(videoIntroduction)) {
				throw new ExceptionNotEmpty("videoIntroduction");
			}
		}

		logger.info("发起打包请求，form : " + type + " ," + fileName + " ," + videoTitle + " ," + videoIntroduction);
		String boundary = "abcdefghijk";
		String end = "\r\n";
		String twoHyphens = "--";

		String accessToken = Config.mpweixin().accessToken();
		String addMediaUrl = Mpweixin.default_apiAddress + "/cgi-bin/material/add_material?access_token=" + accessToken
				+ "&type=" + type;
		logger.info("上传永久素材url: " + addMediaUrl);

		URL url = new URL(addMediaUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		String wxResult = "";
		try {
			connection.setUseCaches(false);
			connection.setRequestMethod(ConnectionAction.METHOD_POST);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// heads
			Map<String, String> map = new HashMap<>();
			map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_CREDENTIALS,
					ConnectionAction.ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE);
			map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_HEADERS,
					"x-requested-with, x-request, Content-Type, x-cipher, x-client, x-token, token");
			map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_METHODS, ConnectionAction.ACCESS_CONTROL_ALLOW_METHODS_VALUE);
			map.put(ConnectionAction.CACHE_CONTROL, ConnectionAction.CACHE_CONTROL_VALUE);
			map.put(ConnectionAction.CONTENT_TYPE, "multipart/form-data;boundary=" + boundary);
			// 设置字符编码连接参数
			map.put("Connection", "Keep-Alive");
			map.put("Charset", "UTF-8");

			for (Map.Entry<String, String> en : map.entrySet()) {
				if (StringUtils.isNotEmpty(en.getValue())) {
					connection.setRequestProperty(en.getKey(), en.getValue());
				}
			}
			// form data
			try (DataOutputStream ds = new DataOutputStream(connection.getOutputStream())) {
				// properties
				if ("video".equalsIgnoreCase(type)) {
					String videoDes = "{\"title\":\"" + videoTitle + "\", \"introduction\":\"" + videoIntroduction
							+ "\"}";
					writeFormProperties("description", videoDes, boundary, end, twoHyphens, ds);
				}

				// file
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"media\";filename=\""
						+ URLEncoder.encode(fileName, DefaultCharset.name) + "\"" + end);
				ds.writeBytes(end);
				ds.write(bytes, 0, bytes.length);
				ds.writeBytes(end);
				ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
				/* close streams */
				ds.flush();
			}

			try (InputStream input = connection.getInputStream()) {
				wxResult = IOUtils.toString(input, StandardCharsets.UTF_8);
			}
			int code = connection.getResponseCode();
			if (code != 200) {
				throw new Exception("connection{url:" + connection.getURL() + "}, response error{responseCode:" + code
						+ "}, response:" + result + ".");
			}
			logger.info("微信上传素材请求返回，result : " + wxResult);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		Wo wo = XGsonBuilder.instance().fromJson(wxResult, Wo.class);
		result.setData(wo);
		return result;
	}

	private void writeFormProperties(String name, String value, String boundary, String end, String twoHyphens,
			DataOutputStream ds) throws IOException {
		ds.writeBytes(twoHyphens + boundary + end);
		ds.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
		ds.writeBytes(end);
		ds.writeBytes("Content-Length:" + value.length());
		ds.writeBytes(end);
		ds.writeBytes(end);
		ds.write(value.getBytes(StandardCharsets.UTF_8));
		ds.writeBytes(end);
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("微信返回的素材id")
		private String media_id;
		@FieldDescribe("微信返回的素材url")
		private String url;
		@FieldDescribe("微信返回code")
		private Integer errcode;
		@FieldDescribe("微信返回错误信息")
		private String errmsg;

		public String getMedia_id() {
			return media_id;
		}

		public void setMedia_id(String media_id) {
			this.media_id = media_id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
	}
}
