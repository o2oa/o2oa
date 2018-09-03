package com.x.face.assemble.control.jaxrs.face;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("face")
@JaxrsDescribe("face操作")
public class FaceAction extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(FaceAction.class);

	//现在使用的邬树涛的测试key o2wsttest
	private static String API_KEY = "kyYSn8JOnv8IPYUzeJ6nVEtXkcovPUrk";
	private static String API_SECRET = "AXavTq8oETR_WdrKOwFb9Jg9Klj8Mclm";

	//请求face++ 各个服务的url，懵逼
	private static String DETECT_URL = "https://api-cn.faceplusplus.com/facepp/v3/detect"; //上传一个图片，返回一堆图片里面人脸的信息，真正有用的是“face_token”

	@JaxrsMethodDescribe(value = "测试是否工作", action = StandardJaxrsAction.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void iswork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("Json信息") JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		logger.debug(effectivePerson.getDistinguishedName());
		wrap.setValue("face iswork  is work!! Token:" + effectivePerson.getToken() + " Name:" + effectivePerson.getDistinguishedName() + " TokenType:" + effectivePerson.getTokenType());
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "上传照片,返回照片里面的人脸信息，和face_token", action = StandardJaxrsAction.class)
	@POST
	@Path("detect/{name}/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void detect(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("不知道什么name") @PathParam("name") String name, @JaxrsParameterDescribe("不知道什么id") @PathParam("id") String userId, @FormDataParam(FILE_FIELD) final byte[] bytes, @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		//		File file = new File("你的本地图片路径");
		//		byte[] buff = getBytesFromFile(file);

		//String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, byte[]> byteMap = new HashMap<>();

		//map.put("api_key", "你的KEY");
		map.put("api_key", API_KEY);
		//map.put("api_secret", "你的SECRET");
		map.put("api_secret", API_SECRET);

		map.put("return_landmark", "1");
		map.put("return_attributes", "gender,age,smiling,headpose,facequality,blur,eyestatus,emotion,ethnicity,beauty,mouthstatus,eyegaze,skinstatus");
		//byteMap.put("image_file", buff);

		byteMap.put("image_file", bytes);
		try {
			byte[] bacd = post(DETECT_URL, map, byteMap);
			String str = new String(bacd);
			wrap.setValue(str);
			System.out.println(str);
		} catch (Exception e) {
			String str = new String("face error!!!!");
			wrap.setValue(str);
			System.out.println(str);
			e.printStackTrace();
		}

		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));

	}

}
