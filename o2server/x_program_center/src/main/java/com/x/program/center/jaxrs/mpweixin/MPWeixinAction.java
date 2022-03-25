package com.x.program.center.jaxrs.mpweixin;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 微信公众号接口 Created by fancyLou on 3/8/21. Copyright © 2021 O2. All rights
 * reserved.
 */

@Path("mpweixin")
@JaxrsDescribe("微信公众号接口")
public class MPWeixinAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(MPWeixinAction.class);

	@JaxrsMethodDescribe(value = "给微信公众号后台检测服务器配置.", action = ActionCheckMPWeixin.class)
	@GET
	@Path("check")
	public void check(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("signature") @QueryParam("signature") String signature,
			@JaxrsParameterDescribe("timestamp") @QueryParam("timestamp") Long timestamp,
			@JaxrsParameterDescribe("nonce") @QueryParam("nonce") String nonce,
			@JaxrsParameterDescribe("echostr") @QueryParam("echostr") String echostr) {
		ActionResult<ActionCheckMPWeixin.Wo> result = new ActionResult<>();
		try {
			result = new ActionCheckMPWeixin().execute(signature, timestamp, nonce, echostr);
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "微信公众号接收消息.", action = ActionReceiveMsg.class)
	@POST
	@Path("check")
	public void receiveMsg(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("signature") @QueryParam("signature") String signature,
			@JaxrsParameterDescribe("timestamp") @QueryParam("timestamp") Long timestamp,
			@JaxrsParameterDescribe("nonce") @QueryParam("nonce") String nonce,
			@JaxrsParameterDescribe("echostr") @QueryParam("echostr") String echostr, InputStream inputStream) {
		ActionResult<ActionReceiveMsg.Wo> result = new ActionResult<>();
		try {
			result = new ActionReceiveMsg().execute(signature, timestamp, nonce, echostr, inputStream);
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// ///////       media  素材  /////////////////////

	@JaxrsMethodDescribe(value = "上传永久素材到微信服务器.", action = ActionUploadMediaForever.class)
	@POST
	@Path("media/add/forever")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void addMedia(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								 @JaxrsParameterDescribe("类型： image、voice、video 、thumb") @FormDataParam("type") String type,
								 @JaxrsParameterDescribe("视频标题 video的类型必须传") @FormDataParam("videoTitle") String videoTitle,
								 @JaxrsParameterDescribe("视频介绍 video的类型必须传") @FormDataParam("videoIntroduction") String videoIntroduction,
								 @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
								 @JaxrsParameterDescribe("附件标识") @FormDataParam(FILE_FIELD) final byte[] bytes,
								 @JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition){
		ActionResult<ActionUploadMediaForever.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadMediaForever().execute(type, fileName, bytes, disposition, videoTitle, videoIntroduction);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}




	// /menu/* 需要管理员权限

	@JaxrsMethodDescribe(value = "微信菜单列表查看.", action = ActionListAllMenu.class)
	@GET
	@Path("menu/list/weixin")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void menuWeixinList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionListAllMenu.Wo> result = new ActionResult<>();
		try {
			result = new ActionListAllMenu().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "微信公众号关注回复的菜单对象.", action = ActionSubscribeMenu.class)
	@GET
	@Path("menu/subscribe")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void menuWeixinSubscribe(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionSubscribeMenu.WoMenu> result = new ActionResult<>();
		try {
			result = new ActionSubscribeMenu().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "给微信公众号创建菜单，【注意这个接口会把公众号菜单全部替换掉！】.", action = ActionCreateMenu.class)
	@GET
	@Path("menu/create/to/weixin")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void menuCreate2Weixin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionCreateMenu.Wo> result = new ActionResult<>();
		try {
			result = new ActionCreateMenu().execute();
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建一个菜单项.", action = ActionAddMenu.class)
	@POST
	@Path("menu/add")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void menuAdd(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionAddMenu.Wo> result = new ActionResult<>();
		try {
			result = new ActionAddMenu().execute(jsonElement);
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新一个菜单项.", action = ActionUpdateMenu.class)
	@POST
	@Path("menu/update/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void menuUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdateMenu.Wo> result = new ActionResult<>();
		try {
			result = new ActionUpdateMenu().execute(id, jsonElement);
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除一个菜单项.", action = ActionDeleteMenu.class)
	@DELETE
	@Path("menu/delete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void menuDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionDeleteMenu.Wo> result = new ActionResult<>();
		try {
			result = new ActionDeleteMenu().execute(id);
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
