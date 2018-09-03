package com.x.face.assemble.control.jaxrs.faceset;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

@Path("faceset")
@JaxrsDescribe("faceset操作")
public class FaceSetAction extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(FaceSetAction.class);

	//现在使用的邬树涛的测试key o2wsttest
	private static String API_KEY = "kyYSn8JOnv8IPYUzeJ6nVEtXkcovPUrk";
	private static String API_SECRET = "AXavTq8oETR_WdrKOwFb9Jg9Klj8Mclm";

	//请求face++ 各个服务的url，懵逼
	private static String DETECT_URL = "https://api-cn.faceplusplus.com/facepp/v3/detect"; //上传一个图片，返回一堆图片里面人脸的信息，真正有用的是“face_token”
	private static String FACESET_CREATE_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create"; //创建一个FaceSet的服务URL
	private static String GET_FACESETS_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getfacesets"; //获取某一 API Key 下的 FaceSet 列表及其 faceset_token、outer_id、display_name 和 tags 等信息。
	private static String GET_FACESET_DETAIL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail"; //获取一个 FaceSet 的所有信息
	private static String ADDFACE_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface"; //为一个已经创建的 FaceSet 添加人脸标识 face_token。一个 FaceSet 最多存储10000个 face_token。
	private static String REMOVEFACE_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/removeface"; //移除一个FaceSet中的某些或者全部face_token

	@JaxrsMethodDescribe(value = "测试是否工作", action = StandardJaxrsAction.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("Json信息") JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		logger.debug(effectivePerson.getDistinguishedName());
		wrap.setValue("faceset iswork  is work!! Token:" + effectivePerson.getToken() + " Name:" + effectivePerson.getDistinguishedName() + " TokenType:" + effectivePerson.getTokenType());
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "创建一个faceset", action = StandardJaxrsAction.class)
	@POST
	@Path("createfaceset/{faceset}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void createFaceSet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();
		//HashMap<String, byte[]> byteMap = new HashMap<>();

		//map.put("api_key", "你的KEY");
		map.put("api_key", API_KEY);
		//map.put("api_secret", "你的SECRET");
		map.put("api_secret", API_SECRET);
		map.put("outer_id", faceset);
		map.put("display_name", faceset);
		//map.put("tags", "");				//FaceSet 自定义标签组成的字符串，用来对 FaceSet 分组。最长255个字符，多个 tag 用逗号分隔，每个 tag 不能包括字符^@,&=*'"
		//map.put("face_tokens", "");		//人脸标识 face_token，可以是一个或者多个，用逗号分隔。最多不超过5个 face_token
		//map.put("user_data", "");			//自定义用户信息，不大于16 KB，不能包括字符^@,&=*'"
		//map.put("force_merge","");		//Int 类型。0：不将 face_tokens 加入已存在的 FaceSet 中，直接返回 FACESET_EXIST 错误1：将 face_tokens 加入已存在的 FaceSet 中。默认值为0

		try {
			byte[] bacd = post2(FACESET_CREATE_URL, map);
			String str = new String(bacd);
			wrap.setValue(str);
			logger.info(str);
		} catch (Exception e) {
			String str = new String("createfaceset error!!!!");
			wrap.setValue(str);
			logger.info(str);
			e.printStackTrace();
		}

		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));

	}

	@JaxrsMethodDescribe(value = "获取某一 API Key 下的 FaceSet 列表", action = StandardJaxrsAction.class)
	@GET
	@Path("getfacesets")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void getFaceSets(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();
		//HashMap<String, byte[]> byteMap = new HashMap<>();

		//map.put("api_key", "你的KEY");
		map.put("api_key", API_KEY);
		//map.put("api_secret", "你的SECRET");
		map.put("api_secret", API_SECRET);
		//map.put("outer_id", faceset);
		//map.put("display_name", faceset);
		//map.put("tags", "");				//FaceSet 自定义标签组成的字符串，用来对 FaceSet 分组。最长255个字符，多个 tag 用逗号分隔，每个 tag 不能包括字符^@,&=*'"
		//map.put("face_tokens", "");		//人脸标识 face_token，可以是一个或者多个，用逗号分隔。最多不超过5个 face_token
		//map.put("user_data", "");			//自定义用户信息，不大于16 KB，不能包括字符^@,&=*'"
		//map.put("force_merge","");		//Int 类型。0：不将 face_tokens 加入已存在的 FaceSet 中，直接返回 FACESET_EXIST 错误1：将 face_tokens 加入已存在的 FaceSet 中。默认值为0

		try {
			byte[] bacd = requestFace1(GET_FACESETS_URL, map, "GET");
			String str = new String(bacd);
			wrap.setValue(str);
			logger.info(str);
		} catch (Exception e) {
			String str = new String("getFaceSets error!!!!");
			wrap.setValue(str);
			logger.info(str);
			e.printStackTrace();
		}

		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取某一FaceSet", action = StandardJaxrsAction.class)
	@GET
	@Path("getfaceset/{faceset}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void getFaceSet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();
		//HashMap<String, byte[]> byteMap = new HashMap<>();

		//map.put("api_key", "你的KEY");
		map.put("api_key", API_KEY);
		//map.put("api_secret", "你的SECRET");
		map.put("api_secret", API_SECRET);
		map.put("outer_id", faceset);
		//map.put("outer_id", faceset);
		//map.put("display_name", faceset);
		//map.put("tags", "");				//FaceSet 自定义标签组成的字符串，用来对 FaceSet 分组。最长255个字符，多个 tag 用逗号分隔，每个 tag 不能包括字符^@,&=*'"
		//map.put("face_tokens", "");		//人脸标识 face_token，可以是一个或者多个，用逗号分隔。最多不超过5个 face_token
		//map.put("user_data", "");			//自定义用户信息，不大于16 KB，不能包括字符^@,&=*'"
		//map.put("force_merge","");		//Int 类型。0：不将 face_tokens 加入已存在的 FaceSet 中，直接返回 FACESET_EXIST 错误1：将 face_tokens 加入已存在的 FaceSet 中。默认值为0

		try {
			byte[] bacd = requestFace1(GET_FACESET_DETAIL, map, "GET");
			String str = new String(bacd);
			wrap.setValue(str);
			logger.info(str);
		} catch (Exception e) {
			String str = new String("getFaceSets error!!!!");
			wrap.setValue(str);
			logger.info(str);
			e.printStackTrace();
		}

		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "向一个指定的faceset里面增加一个face", action = StandardJaxrsAction.class)
	@POST
	@Path("addfacebyimg/{faceset}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void addfaceByImg(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset, @FormDataParam(FILE_FIELD) final byte[] bytes, @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();
		HashMap<String, byte[]> byteMap = new HashMap<>();

		map.put("api_key", API_KEY);
		map.put("api_secret", API_SECRET);
		//map.put("outer_id", faceset);
		//map.put("display_name", faceset);

		byteMap.put("image_file", bytes);
		try {
			byte[] bacd = post(DETECT_URL, map, byteMap);
			String str = new String(bacd);
			//Gson gson = new Gson();
			JsonObject object = new JsonParser().parse(str).getAsJsonObject();
			JsonArray jsonArray = object.getAsJsonArray("faces");
			JsonElement jsonElement = jsonArray.get(0);
			JsonObject jo = jsonElement.getAsJsonObject();
			String face_token = jo.get("face_token").getAsString();
			logger.info("addface==》DETECT_URL==》face_token：" + face_token);

			HashMap<String, String> map1 = new HashMap<>();

			map1.put("api_key", API_KEY);
			map1.put("api_secret", API_SECRET);
			map1.put("outer_id", faceset);
			map1.put("face_tokens", face_token);
			byte[] bacd1 = requestFace1(ADDFACE_URL, map1, "POST");
			String str1 = new String(bacd1);
			wrap.setValue(str1);
			logger.info(str1);

			//wrap.setValue(str);
			//System.out.println(str);
		} catch (Exception e) {
			String str = new String("face error!!!!");
			//wrap.setValue(str);
			System.out.println(str);
			e.printStackTrace();
		}

		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "向一个指定的faceset里面增加一个face", action = StandardJaxrsAction.class)
	@POST
	@Path("addface/{faceset}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	//public void addfaceByFaceToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset, @FormDataParam(FILE_FIELD) final FormDataContentDisposition face_tokens) {
	public void addfaceByFaceToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset, JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();
		HashMap<String, byte[]> byteMap = new HashMap<>();

		map.put("api_key", API_KEY);
		map.put("api_secret", API_SECRET);
		map.put("outer_id", faceset);
		//map.put("display_name", faceset);
		logger.info(jsonElement.getAsJsonObject().get("face_tokens").getAsString());
		String face_tokens = jsonElement.getAsJsonObject().get("face_tokens").getAsString();
		map.put("face_tokens", face_tokens);
		try {
			byte[] bacd = post(ADDFACE_URL, map, null);
			String str = new String(bacd);
			wrap.setValue(str);
			logger.info(str);

		} catch (Exception e) {
			String str = new String("face error!!!!");
			wrap.setValue(str);
			System.out.println(str);
			e.printStackTrace();
		}

		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "从一个指定的faceset里面删除一个face", action = StandardJaxrsAction.class)
	@POST
	@Path("removeface/{faceset}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removefacefaceByFaceToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset, JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();
		//HashMap<String, byte[]> byteMap = new HashMap<>();

		map.put("api_key", API_KEY);
		map.put("api_secret", API_SECRET);
		map.put("outer_id", faceset);
		//map.put("display_name", faceset);
		logger.info(jsonElement.getAsJsonObject().get("face_tokens").getAsString());
		String face_tokens = jsonElement.getAsJsonObject().get("face_tokens").getAsString();
		map.put("face_tokens", face_tokens);
		try {
			byte[] bacd = post(REMOVEFACE_URL, map, null);
			String str = new String(bacd);
			wrap.setValue(str);
			logger.info(str);

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
