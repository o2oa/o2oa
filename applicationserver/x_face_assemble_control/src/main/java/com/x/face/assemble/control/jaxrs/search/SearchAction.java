package com.x.face.assemble.control.jaxrs.search;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("search")
@JaxrsDescribe("search操作")
public class SearchAction extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(SearchAction.class);

	//现在使用的邬树涛的测试key o2wsttest
	private static String API_KEY = "kyYSn8JOnv8IPYUzeJ6nVEtXkcovPUrk";
	private static String API_SECRET = "AXavTq8oETR_WdrKOwFb9Jg9Klj8Mclm";

	private static String SEACH_URL = "https://api-cn.faceplusplus.com/facepp/v3/search";

	@JaxrsMethodDescribe(value = "从一个指定的faceset里面,根据facetokens查找一个什么东西", action = StandardJaxrsAction.class)
	@POST
	@Path("/{faceset}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void searchfacefaceByImgBase64(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset, JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();

		map.put("api_key", API_KEY);
		map.put("api_secret", API_SECRET);
		map.put("outer_id", faceset);
		logger.info(jsonElement.getAsJsonObject().get("basecode").getAsString());
		String basecode = jsonElement.getAsJsonObject().get("basecode").getAsString();

		map.put("image_base64", basecode);
		try {
			byte[] bacd = post(SEACH_URL, map, null);
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

	@JaxrsMethodDescribe(value = "从一个指定的faceset里面,根据facetokens查找一个什么东西", action = StandardJaxrsAction.class)
	@POST
	@Path("bytokens/{faceset}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void searchfacefaceByFaceToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("faceset标识") @PathParam("faceset") String faceset, JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();

		HashMap<String, String> map = new HashMap<>();

		map.put("api_key", API_KEY);
		map.put("api_secret", API_SECRET);
		map.put("outer_id", faceset);
		logger.info(jsonElement.getAsJsonObject().get("face_tokens").getAsString());
		String face_tokens = jsonElement.getAsJsonObject().get("face_tokens").getAsString();

		map.put("face_tokens", face_tokens);
		try {
			byte[] bacd = post(SEACH_URL, map, null);
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
