package com.x.program.center.jaxrs.apppack;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
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

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

/**
 * android 打包 Created by fancyLou on 6/15/21. Copyright © 2021 O2. All rights
 * reserved.
 */
public class ActionAndroidPack extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAndroidPack.class);

	ActionResult<Wo> execute(String token, String appName, String o2ServerProtocol, String o2ServerHost,
			String o2ServerPort, String o2ServerContext, String isPackAppIdOuter, String urlMapping,
			String appVersionName, String appBuildNo, String deleteHuawei, String fileName, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		if (StringUtils.isEmpty(token)) {
			throw new ExceptionNoToken();
		}
		if (StringUtils.isEmpty(appName)) {
			throw new ExceptionEmptyProperty("appName");
		}
		if (appName.length() > 6) {
			throw new ExceptionAppNameMax6();
		}
		if (StringUtils.isEmpty(o2ServerProtocol)) {
			throw new ExceptionEmptyProperty("o2ServerProtocol");
		}
		if (StringUtils.isEmpty(o2ServerHost)) {
			throw new ExceptionEmptyProperty("o2ServerHost");
		}
		if (StringUtils.isEmpty(o2ServerPort)) {
			throw new ExceptionEmptyProperty("o2ServerPort");
		}
		if (StringUtils.isEmpty(o2ServerContext)) {
			throw new ExceptionEmptyProperty("o2ServerContext");
		}
		if (StringUtils.isNotEmpty(appBuildNo)) {
			if (!StringUtils.isNumeric(appBuildNo)) {
				throw new ExceptionBuildNoNotNumber();
			}
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
			throw new ExceptionFileNameEmpty();
		}
		if (!fileName.toLowerCase().endsWith("png")) {
			throw new ExceptionFileNotPng();
		}
		String s = postFormData(token, appName, o2ServerProtocol, o2ServerHost, o2ServerPort, o2ServerContext,
				isPackAppIdOuter, urlMapping, appVersionName, appBuildNo, deleteHuawei, fileName, bytes);
		Type type = new TypeToken<AppPackResult<IdValue>>() {
		}.getType();
		AppPackResult<IdValue> appPackResult = XGsonBuilder.instance().fromJson(s, type);
		Wo wo = new Wo();
		if (appPackResult.getResult().equals(AppPackResult.result_failure)) {
			wo.setValue(false);
			result.setMessage(appPackResult.getMessage());
		} else {
			wo.setValue(true);
		}
		result.setData(wo);
		return result;
	}

	/**
	 * formData 提交打包信息
	 * 
	 * @param token
	 * @param appName
	 * @param o2ServerProtocol
	 * @param o2ServerHost
	 * @param o2ServerPort
	 * @param o2ServerContext
	 * @param fileName
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	private String postFormData(String token, String appName, String o2ServerProtocol, String o2ServerHost,
			String o2ServerPort, String o2ServerContext, String isPackAppIdOuter, String urlMapping,
			String appVersionName, String appBuildNo, String deleteHuawei, String fileName, byte[] bytes)
			throws Exception {
		logger.info("发起打包请求，form : " + token + " ," + appName + " ," + o2ServerProtocol + " ," + o2ServerHost + " ,"
				+ o2ServerPort + " ," + o2ServerContext + " ," + isPackAppIdOuter + " ," + urlMapping + " ,"
				+ appVersionName + " ," + appBuildNo + " ," + deleteHuawei + " ," + fileName);
		String boundary = "abcdefghijk";
		String end = "\r\n";
		String twoHyphens = "--";
		String address = Config.collect().appPackServerApi(Collect.ADDRESS_APPPACK_SAVE);
		logger.info("发起打包请求，url " + address);
		URL url = new URL(address);
		HttpURLConnection connection = null;
		String result = "";
		try {
			connection = (HttpURLConnection) url.openConnection();
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
			map.put("token", token);

			for (Map.Entry<String, String> en : map.entrySet()) {
				if (StringUtils.isNotEmpty(en.getValue())) {
					connection.setRequestProperty(en.getKey(), en.getValue());
				}
			}
			// form data
			try (DataOutputStream ds = new DataOutputStream(connection.getOutputStream())) {
				// properties
				writeFormProperties("appName", appName, boundary, end, twoHyphens, ds);
				writeFormProperties("o2ServerProtocol", o2ServerProtocol, boundary, end, twoHyphens, ds);
				writeFormProperties("o2ServerHost", o2ServerHost, boundary, end, twoHyphens, ds);
				writeFormProperties("o2ServerPort", o2ServerPort, boundary, end, twoHyphens, ds);
				writeFormProperties("o2ServerContext", o2ServerContext, boundary, end, twoHyphens, ds);
				writeFormProperties("isPackAppIdOuter", isPackAppIdOuter, boundary, end, twoHyphens, ds);
				writeFormProperties("urlMapping", urlMapping, boundary, end, twoHyphens, ds);
				writeFormProperties("appVersionName", appVersionName, boundary, end, twoHyphens, ds);
				writeFormProperties("appBuildNo", appBuildNo, boundary, end, twoHyphens, ds);
				writeFormProperties("deleteHuawei", deleteHuawei, boundary, end, twoHyphens, ds);
				writeFormProperties("collectName", Config.collect().getName(), boundary, end, twoHyphens, ds);
				// file
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\";filename=\""
						+ URLEncoder.encode(fileName, DefaultCharset.name) + "\"" + end);
				ds.writeBytes(end);
				ds.write(bytes, 0, bytes.length);
				ds.writeBytes(end);
				ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
				/* close streams */
				ds.flush();
			}

			try (InputStream input = connection.getInputStream()) {
				result = IOUtils.toString(input, StandardCharsets.UTF_8);
			}
			int code = connection.getResponseCode();
			if (code != 200) {
				throw new Exception("connection{url:" + connection.getURL() + "}, response error{responseCode:" + code
						+ "}, response:" + result + ".");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		logger.info("打包请求返回，result : " + result);
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

	public static class IdValue {

		private String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 1L;

	}
}
