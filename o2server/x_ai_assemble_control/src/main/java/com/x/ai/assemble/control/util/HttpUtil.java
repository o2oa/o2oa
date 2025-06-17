package com.x.ai.assemble.control.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.ExceptionMultiPartBinary;
import com.x.base.core.project.connection.FilePart;
import com.x.base.core.project.connection.FormField;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chengjian
 * @date 2025/04/14 14:03
 **/
public class HttpUtil {

    private static final Gson gson = XGsonBuilder.instance();

    private static final int DEFAULT_CONNECT_TIMEOUT = 2000;
    private static final int DEFAULT_READ_TIMEOUT = 300000;
    public static final String METHOD_POST = "POST";

    public static ActionResponse postMultiPartBinary(String address, List<NameValuePair> heads,
            List<FormField> formFields, List<FilePart> fileParts) throws Exception {
        return postMultiPartBinary(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, address, METHOD_POST, heads,
                formFields, fileParts);
    }

    private static ActionResponse postMultiPartBinary(int connectTimeout, int readTimeout, String address, String method,
            List<NameValuePair> heads, List<FormField> formFields, List<FilePart> fileParts)
            throws Exception {
        HttpURLConnection connection = null;
        String boundary = StringTools.TWO_HYPHENS + StringTools.TWO_HYPHENS + System.currentTimeMillis();
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            byte[] bytes = null;
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                if (null != fileParts) {
                    for (FilePart filePart : fileParts) {
                        ConnectionAction.writeFilePart(byteArrayOutputStream, filePart, boundary);
                    }
                }
                if (null != formFields) {
                    for (FormField formField : formFields) {
                        writeFormField(byteArrayOutputStream, formField, boundary);
                    }
                }
                IOUtils.write(StringTools.TWO_HYPHENS + boundary + StringTools.TWO_HYPHENS, byteArrayOutputStream,
                        DefaultCharset.charset_utf_8);
                bytes = byteArrayOutputStream.toByteArray();
            }
            addHeadsMultiPart(connection, heads, boundary);
            connection.setRequestProperty(ConnectionAction.CONTENT_LENGTH, bytes.length + "");
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.connect();
            try (OutputStream output = connection.getOutputStream()) {
                IOUtils.write(bytes, output);
            }
            ActionResponse response = new ActionResponse();
            return read(response, connection);
        } catch (Exception e) {
            throw new ExceptionMultiPartBinary(e, connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static ActionResponse read(ActionResponse response, HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        if (code >= 500) {
            try (InputStream input = connection.getErrorStream()) {
                byte[] buffer = IOUtils.toByteArray(input);
                response.setMessage(extractErrorMessageIfExist(new String(buffer, DefaultCharset.name)));
                response.setType(Type.error);
            }
        } else if (code >= 400) {
            response.setMessage(String.format("url invalid error, address: %s, method: %s, code: %d.",
                    connection.getURL(), connection.getRequestMethod(), code));
            response.setType(Type.error);
        } else if (code == 200) {
            try (InputStream input = connection.getInputStream()) {
                byte[] buffer = IOUtils.toByteArray(input);
                String value = new String(buffer, DefaultCharset.name);
                response = gson.fromJson(value, ActionResponse.class);
            } catch (Exception e) {
                response.setType(Type.connectFatal);
                response.setMessage(String.format(
                        "convert input to json error, address: %s, method: %s, code: %d, because: %s.",
                        connection.getURL(), connection.getRequestMethod(), code, e.getMessage()));
            }
        }
        return response;
    }

    private static void writeFormField(OutputStream output, FormField formField, String boundary) throws IOException {
        IOUtils.write(StringTools.TWO_HYPHENS + boundary, output, StandardCharsets.UTF_8);
        IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
        IOUtils.write("Content-Disposition: form-data; name=\"" + formField.getName() + "\"", output,
                StandardCharsets.UTF_8);
        IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
        IOUtils.write("Content-Length: " + formField.getValue().getBytes(StandardCharsets.UTF_8).length, output,
                StandardCharsets.UTF_8);
        IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
        IOUtils.write("Content-Type: text/plain; charset=" + StandardCharsets.UTF_8.name(), output,
                StandardCharsets.UTF_8);
        IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
        IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
        IOUtils.write(formField.getValue().getBytes(StandardCharsets.UTF_8), output);
        IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
    }

    private static void addHeadsMultiPart(HttpURLConnection connection, List<NameValuePair> heads, String boundary)
            throws Exception {
        Map<String, String> map = new TreeMap<>();
        map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_CREDENTIALS, ConnectionAction.ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE);
        map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_HEADERS,
                ConnectionAction.ACCESS_CONTROL_ALLOW_HEADERS_VALUE + ", " + Config.person().getTokenName());
        map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_METHODS, ConnectionAction.ACCESS_CONTROL_ALLOW_METHODS_VALUE);
        map.put(ConnectionAction.CACHE_CONTROL, ConnectionAction.CACHE_CONTROL_VALUE);
        connection.setRequestProperty(ConnectionAction.CONTENT_TYPE, String.format("multipart/form-data; boundary=%s", boundary));
        if (ListTools.isNotEmpty(heads)) {
            String value;
            for (NameValuePair o : heads) {
                value = Objects.toString(o.getValue(), "");
                if (StringUtils.isNotEmpty(o.getName()) && StringUtils.isNotEmpty(value)) {
                    map.put(o.getName(), value);
                }
            }
        }
        map.entrySet().forEach((o -> connection.addRequestProperty(o.getKey(), o.getValue())));
    }

    private static String extractErrorMessageIfExist(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        try {
            JsonElement jsonElement = gson.fromJson(str, JsonElement.class);
            if (jsonElement.isJsonObject()) {
                ActionResponse ar = gson.fromJson(jsonElement, ActionResponse.class);
                if (StringUtils.isNotEmpty(ar.getMessage())) {
                    return ar.getMessage();
                }
            }
        } catch (JsonParseException e) {
            // nothing
        }
        return str;
    }

}
