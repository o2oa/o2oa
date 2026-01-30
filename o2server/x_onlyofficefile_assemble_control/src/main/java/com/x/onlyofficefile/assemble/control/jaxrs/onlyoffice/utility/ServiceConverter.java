package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServiceConverter {

	private static int ConvertTimeout = 120000;
	private static String DocumentConverterUrl = null;
	private static String DocumentJwtHeader = null;

	public static class ConvertBody {
		public String url;
		public String outputtype;
		public String filetype;
		public String title;
		public String key;
		public Boolean async;
		public String token;
	}

	static {
		try {
			DocumentConverterUrl = ConfigManager.init(Config.base()).getDocserviceConverter();
			int timeout = Integer.parseInt(ConfigManager.init(Config.base()).getTimeout());
			if (timeout > 0) {
				ConvertTimeout = timeout;
			}
		} catch (Exception ex) {
		}
	}

	public static String getConvertedUri(String fileName, String documentUri, String fromExtension, String toExtension,
			String documentRevisionId, Boolean isAsync) throws Exception {

		fromExtension = fromExtension == null || fromExtension.isEmpty() ? FileUtility.getFileExtension(documentUri)
				: fromExtension;

		documentRevisionId = documentRevisionId == null || documentRevisionId.isEmpty() ? documentUri
				: documentRevisionId;
		documentRevisionId = GenerateRevisionId(documentRevisionId);

		ConvertBody body = new ConvertBody();
		body.url = documentUri;
		body.outputtype = toExtension.replace(".", "");
		body.filetype = fromExtension.replace(".", "");
		body.title = fileName;
		body.key = documentRevisionId;
		if (isAsync) {
			body.async = true;
		}

		String headerToken = "";
		if (DocumentManager.tokenEnabled()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", body.url);
			map.put("outputtype", body.outputtype);
			map.put("filetype", body.filetype);
			map.put("title", body.title);
			map.put("key", body.key);
			if (isAsync)
				map.put("async", body.async);

			String token = DocumentManager.createToken(map);
			body.token = token;

			Map<String, Object> payloadMap = new HashMap<String, Object>();
			payloadMap.put("payload", map);
			headerToken = DocumentManager.createToken(payloadMap);
		}

		Gson gson = new Gson();
		String bodyString = gson.toJson(body);

		byte[] bodyByte = bodyString.getBytes(StandardCharsets.UTF_8);

		URL url = new URL(DocumentConverterUrl);
		java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setFixedLengthStreamingMode(bodyByte.length);
		connection.setRequestProperty("Accept", "application/json");
		connection.setConnectTimeout(ConvertTimeout);

		if (DocumentManager.tokenEnabled()) {
			connection.setRequestProperty(DocumentJwtHeader == null ? "Authorization" : DocumentJwtHeader,
					"Bearer " + headerToken);
		}

		connection.connect();
		try (OutputStream os = connection.getOutputStream()) {
			os.write(bodyByte);
		}

		InputStream stream = connection.getInputStream();

		if (stream == null)
			throw new Exception("Could not get an answer");

		String jsonString = convertStreamToString(stream);

		connection.disconnect();

		return getResponseUri(jsonString);
	}

	public static String GenerateRevisionId(String expectedKey) {
		if (expectedKey.length() > 20)
			expectedKey = Integer.toString(expectedKey.hashCode());
		String key = expectedKey.replace("[^0-9-.a-zA-Z_=]", "_");
		return key.substring(0, Math.min(key.length(), 20));
	}

	private static void ProcessConvertServiceResponceError(int errorCode) throws Exception {
		String errorMessage = "";
		String errorMessageTemplate = "Error occurred in the ConvertService: ";

		switch (errorCode) {
		case -8:
			errorMessage = errorMessageTemplate + "Error document VKey";
			break;
		case -7:
			errorMessage = errorMessageTemplate + "Error document request";
			break;
		case -6:
			errorMessage = errorMessageTemplate + "Error database";
			break;
		case -5:
			errorMessage = errorMessageTemplate + "Error unexpected guid";
			break;
		case -4:
			errorMessage = errorMessageTemplate + "Error download error";
			break;
		case -3:
			errorMessage = errorMessageTemplate + "Error convertation error";
			break;
		case -2:
			errorMessage = errorMessageTemplate + "Error convertation timeout";
			break;
		case -1:
			errorMessage = errorMessageTemplate + "Error convertation unknown";
			break;
		case 0:
			break;
		default:
			errorMessage = "ErrorCode = " + errorCode;
			break;
		}

		throw new Exception(errorMessage);
	}

	private static String getResponseUri(String jsonString) throws Exception {
		JsonObject jsonObj = convertStringToJSON(jsonString);

		Object error = jsonObj.get("error");
		if (error != null)
			ProcessConvertServiceResponceError(Math.toIntExact((long) error));

		Boolean isEndConvert = jsonObj.get("endConvert").getAsBoolean();

		Long resultPercent = 0l;
		String responseUri = null;

		if (isEndConvert) {
			resultPercent = 100l;
			responseUri = (String) jsonObj.get("fileUrl").getAsString();
		} else {
			resultPercent = (Long) jsonObj.get("percent").getAsLong();
			resultPercent = resultPercent >= 100l ? 99l : resultPercent;
		}

		return resultPercent >= 100l ? responseUri : "";
	}

	private static String convertStreamToString(InputStream stream) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(stream);
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String line = bufferedReader.readLine();

		while (line != null) {
			stringBuilder.append(line);
			line = bufferedReader.readLine();
		}

		String result = stringBuilder.toString();
		return result;
	}

	private static JsonObject convertStringToJSON(String jsonString) {
		Gson gson = new Gson();
		JsonElement jsonObj = gson.fromJson(jsonString, JsonElement.class);
		return jsonObj.getAsJsonObject();
	}
}
