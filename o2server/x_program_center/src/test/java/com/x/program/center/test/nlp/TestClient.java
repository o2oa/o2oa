package com.x.program.center.test.nlp;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;

public class TestClient {

	@Test
	public void test() {

		String url = "http://api.cuobiezi.net/spellcheck/json_check/json_phrase";

		String sentence = "测试文本中国人民共和国张可诺雷非科技2018年23月35号习近平国家主席李克强总理这根邮寄达老旧烟囱已走到生命进头，中国人民解军雷落科技中国特色会社主义马少黄股票她的离去让哦们很悲伤， 客户侧中华人民共和李洪志台万第二大金融控股公司富邦金控已与腾讯谈成合作，上述保险产品将由富邦金控旗下内地子公司富邦财险开发或引进。";

		sentence = "习近平离京对西班牙、阿根廷、巴拿马和葡萄牙进行国事访问 并出仙二十国集团领导人第十三次峰会";

		// sentence = "华为云专业名词测试:对像存储服务是稳定、安全、高效、易用的云存储服务，具备标准Restful
		// API接口，可存储任意数量和形式的非结构化数据，提供99.999999999%的数据可靠性。";

		JsonObject json = new JsonObject();
		json.addProperty("content", sentence);// 固定 参数
		json.addProperty("username", "zhourui");// 可替换参数 --> 请注册账号后，向管理员申请权限， :-)
		json.addProperty("password", "1234abcd");// 固定测试参数
		json.addProperty("biz_type", "show");// 固定参数
		json.addProperty("mode", "advanced");// 固定参数
		// json.put("is_return_sentence",true);// 是否返回句子 ， 具体说明，可以参考文档。
		json.addProperty("user_channel", "api.cuobiezi.net"); // 固定参数
		// json.put("check_sensitive_word",true); // 敏感词检测
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair("", ""));
		System.out.println(json.toString());
		String str = null;
		try {
			str = HttpConnection.postAsString(url, null, json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(str);

	}

}
