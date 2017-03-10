package com.x.test.alidayu;

import org.junit.Test;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import org.junit.Test;

public class TestClient {
	@Test
	public void test() throws Exception {
		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23363274",
				"546784eac4fbea08e36bc71373b0cada");
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("");
		req.setSmsType("normal");
		req.setSmsFreeSignName("O2平台");
		req.setSmsParamString("{code:'123'}");
		req.setRecNum("13336173316");
		req.setSmsTemplateCode("SMS_52205156");
		AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());
	}
}
