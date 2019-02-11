package com.x.processplatform.assemble.surface.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;

public class TestClientImportFile {

	@Test
	public void test4() throws Exception {
		File file = new File("d:/公文中等结果集");
		List<NameValuePair> heads = new ArrayList<>();
		String xtoken = "jlMh2guoFia-EPxnDppJq1pXYeQZDa_GCp05aUeYqxzMvh87Dpd_A725Uy1VQNhSXe8IsPd8nkw";
		heads.add(new NameValuePair("x-token", xtoken));
		for (File dir : FileUtils.listFilesAndDirs(file, FalseFileFilter.FALSE, DirectoryFileFilter.DIRECTORY)) {
			if (!StringUtils.equals(file.getAbsolutePath(), dir.getAbsolutePath())) {
				File docFile = new File(dir, "文件内容&文件内容.doc");
				File infoFile = new File(dir, "baseinfo.txt");
				if ((!docFile.exists()) || (!infoFile.exists())) {
					System.out.println(dir + "中的文件为空");
				} else {
					String infoText = StringUtils.trim(FileUtils.readFileToString(infoFile, "gbk"));
					infoText = StringUtils.substring(infoText, 1, infoText.length() - 1);
					Info info = XGsonBuilder.instance().fromJson(infoText, Info.class);
					System.out.println(info);
					Req req = new Req();
					req.setTitle(info.getSubject());
					req.setForm("00a9df18-4a5a-490a-a2a4-224faf7d7e2a");
					req.setIdentity("周睿@93b9c84c-e5be-4fd9-bcef-96754bc34371@I");
					Map<String, Object> map = new HashMap<>();
					map.put("mainSend", StringUtils.split(info.getFGMainDepartmentWP(), "#"));
					map.put("contactSend", StringUtils.split(info.getFGContactDepartmentRP(), "#"));
					map.put("date", DateTools.parse(info.getFLBeginDateOS(), "yyyy/MM/dd HH:mm:ss"));
					map.put("subject", info.getSubject());
					req.setData(XGsonBuilder.instance().toJsonTree(map));
					ActionResponse resp = ConnectionAction.post(
							"http://dev.:20020/x_processplatform_assemble_surface/jaxrs/workcompleted/process/	8a01b431-8aac-4ab1-b555-7686952e13a7",
							heads, req.toString());
					WoId id = resp.getData(WoId.class);
					CloseableHttpClient httpclient = HttpClients.createDefault();
					HttpPost httppost = new HttpPost(
							"http://127.0.0.1:20020/x_processplatform_assemble_surface/jaxrs/attachment/upload/workcompleted/"
									+ id.getId());
					ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, DefaultCharset.charset);
					HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", new FileBody(docFile))
							.addPart("fileName", new StringBody("文件内容&文件内容.doc", contentType))
							.addPart("site", new StringBody("attachment", contentType)).build();
					httppost.setEntity(reqEntity);
					httppost.addHeader("x-token", xtoken);
					// httpclient.execute(httppost);
					CloseableHttpResponse response = httpclient.execute(httppost);
				}
			}
		}
	}

	@Test
	public void test1() throws Exception {
		File file = new File("E:/公文文档1");
		List<NameValuePair> heads = new ArrayList<>();
		String xtoken = "HeEoZIVgPjQ31rEg7ARdQeRVMf3woS408yvnPg3u8lM7ddsEJNHFDLJTxCww6MWxaEy5G28B49o";
		heads.add(new NameValuePair("x-token", xtoken));
		for (File dir : FileUtils.listFilesAndDirs(file, FalseFileFilter.FALSE, DirectoryFileFilter.DIRECTORY)) {
			if (!StringUtils.equals(file.getAbsolutePath(), dir.getAbsolutePath())) {
				File docFile = new File(dir, "文件内容&文件内容.doc");
				File infoFile = new File(dir, "baseinfo.txt");
				if ((!docFile.exists()) || (!infoFile.exists())) {
					System.out.println(dir + "中的文件为空");
				} else {
					String infoText = StringUtils.trim(FileUtils.readFileToString(infoFile, "gbk"));
					infoText = StringUtils.substring(infoText, 1, infoText.length() - 1);
					infoText = "{" + infoText + "}";
					Info info = XGsonBuilder.instance().fromJson(infoText, Info.class);
					System.out.println(info);
					Req req = new Req();
					req.setTitle(info.getSubject());
					req.setForm("990c1e21-5643-4c89-abe7-ce7527520b74");
					req.setIdentity("周睿@ce37b1c0-61c5-4b94-bcf3-c489347cf062@I");
					Map<String, Object> map = new HashMap<>();
					map.put("mainSend", StringUtils.split(info.getFGMainDepartmentWP(), "#"));
					map.put("contactSend", StringUtils.split(info.getFGContactDepartmentRP(), "#"));
					map.put("date", DateTools.parse(info.getFLBeginDateOS(), "yyyy/MM/dd HH:mm:ss"));
					map.put("subject", info.getSubject());
					req.setData(XGsonBuilder.instance().toJsonTree(map));
					ActionResponse resp = ConnectionAction.post(
							"http://127.0.0.1:20020/x_processplatform_assemble_surface/jaxrs/workcompleted/process/c2da6212-7241-489f-9cfa-e2003fff89d4",
							heads, req.toString());
					WoId id = resp.getData(WoId.class);
					CloseableHttpClient httpclient = HttpClients.createDefault();
					HttpPost httppost = new HttpPost(
							"http://127.0.0.1:20020/x_processplatform_assemble_surface/jaxrs/attachment/upload/workcompleted/"
									+ id.getId());
					ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, DefaultCharset.charset);
					HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", new FileBody(docFile))
							.addPart("fileName", new StringBody("文件内容&文件内容.doc", contentType))
							.addPart("site", new StringBody("attachment", contentType)).build();
					httppost.setEntity(reqEntity);
					httppost.addHeader("x-token", xtoken);
					// httpclient.execute(httppost);
					CloseableHttpResponse response = httpclient.execute(httppost);
				}
			}
		}
	}

	public static class Info extends GsonPropertyObject {
		private String subject;
		private String FGMainDepartmentWP;
		private String FGContactDepartmentRP;
		private String FLBeginDateOS;

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getFGMainDepartmentWP() {
			return FGMainDepartmentWP;
		}

		public void setFGMainDepartmentWP(String fGMainDepartmentWP) {
			FGMainDepartmentWP = fGMainDepartmentWP;
		}

		public String getFGContactDepartmentRP() {
			return FGContactDepartmentRP;
		}

		public void setFGContactDepartmentRP(String fGContactDepartmentRP) {
			FGContactDepartmentRP = fGContactDepartmentRP;
		}

		public String getFLBeginDateOS() {
			return FLBeginDateOS;
		}

		public void setFLBeginDateOS(String fLBeginDateOS) {
			FLBeginDateOS = fLBeginDateOS;
		}

	}

	public static class Req extends GsonPropertyObject {

		@FieldDescribe("标题.")
		private String title;

		@FieldDescribe("序号.")
		private String serial;

		@FieldDescribe("指定表单.")
		private String form;

		@FieldDescribe("指定表单数据.")
		private String formData;

		@FieldDescribe("启动人员身份.")
		private String identity;

		@FieldDescribe("开始日期.")
		private Date startTime;

		@FieldDescribe("结束日期.")
		private Date completedTime;

		@FieldDescribe("工作数据.")
		private JsonElement data;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getForm() {
			return form;
		}

		public void setForm(String form) {
			this.form = form;
		}

		public String getFormData() {
			return formData;
		}

		public void setFormData(String formData) {
			this.formData = formData;
		}

		public String getSerial() {
			return serial;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

	}

}