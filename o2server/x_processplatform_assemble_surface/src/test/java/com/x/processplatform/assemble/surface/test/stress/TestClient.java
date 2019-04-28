package com.x.processplatform.assemble.surface.test.stress;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.utils.time.ClockStamp;
import com.x.processplatform.core.entity.content.Data;

public class TestClient {

	private Data dataTemplate = null;
	private String application = "压力测试应用";
	private String process = "压力测试流程";
	private String token = "HeEoZIVgPjSXTGH8McRJQdFg0b3fDkpBSfAY32RZgi34eG84WfazPutKkEuJeBcOgHYapgy5dnG-_7OqpIqxelT-amVE1Nlh";
	private Integer batch = 100;
	private Integer thread = 40;

	@Before
	public void init() throws Exception {
		this.dataTemplate = XGsonBuilder.instance()
				.fromJson(FileUtils.readFileToString(new File("d:/data.json"), DefaultCharset.charset), Data.class);
	}

	@Test
	public void test() throws Exception {
		ClockStamp.INIT("压力测试", "开始");
		Executor executor = Executors.newFixedThreadPool(thread);
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < thread; i++) {
			Integer t = i;
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				for (int j = 0; j < batch; j++) {
					this.create();
					System.out.println("thread: " + t + ", count:" + j + ".");
				}
			}, executor);
			futures.add(future);
		}
		for (CompletableFuture<Void> future : futures) {
			future.get();
		}
		ClockStamp.STAMP("结束");
		ClockStamp.TRACE();
	}

	private void create() {
		try {
			HttpConnection.postAsString(this.url(), this.heads(), this.wi(dataTemplate));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String url() {
		//String value = "http://172.16.96.100:20020/x_processplatform_service_processing/jaxrs/work";
		String value = "http://127.0.0.1:20020/x_processplatform_service_processing/jaxrs/work";
		return value;
	}

	private List<NameValuePair> heads() {
		List<NameValuePair> pairs = new ArrayList<>();

		pairs.add(new NameValuePair(HttpToken.X_Token, token));
		pairs.add(new NameValuePair(HttpConnection.Content_Type, "application/json;charset=utf-8"));
		return pairs;
	}

	private String identity() {
		Random random = new Random();
		Integer r = random.nextInt(100) + 1;
		return "test" + r + "@test" + r + "@I";
	}

	private String wi(Data template) {
		Data data = XGsonBuilder.convert(template, Data.class);
		Random random = new Random();
		Integer i = random.nextInt(100000000);
		data.put("tag", i.toString());
		data.put("taggewei", (i % 10) + "");
		data.put("tagshiwei", (i % 100) + "");
		data.put("tagbaiwei", (i % 1000) + "");
		data.put("tagqianwei", (i % 10000) + "");
		data.put("tagwanwei", (i % 100000) + "");
		Wi wi = new Wi();
		wi.setTitle("压力测试数据:" + i.toString());
		wi.setApplication(application);
		wi.setProcess(process);
		wi.setProcessing(true);
		wi.setIdentity(this.identity());
		wi.setData(data);
		return XGsonBuilder.instance().toJson(wi);
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("应用标识")
		private String application;
		@FieldDescribe("流程标识")
		private String process;
		@FieldDescribe("身份标识")
		private String identity;
		@FieldDescribe("标题")
		private String title;
		@FieldDescribe("业务数据")
		private Data data;
		@FieldDescribe("自动流转")
		private Boolean processing;

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getProcess() {
			return process;
		}

		public void setProcess(String process) {
			this.process = process;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public Boolean getProcessing() {
			return processing;
		}

		public void setProcessing(Boolean processing) {
			this.processing = processing;
		}

	}

}