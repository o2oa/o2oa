package com.x.organization.assemble.personal.jaxrs.person;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
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
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("person")
@JaxrsDescribe("个人操作")
public class PersonAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(PersonAction.class);

	@JaxrsMethodDescribe(value = "获取个人可以自己的内容", action = ActionGet.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新个人的信息,不更新superior和controllerList", action = ActionEdit.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "更新个人的信息,不更新superior和controllerList MockPutToPost", action = ActionEdit.class)
	@POST
	@Path("mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void editMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取个人头像.", action = ActionGetIcon.class)
	@GET
	@Path("icon")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetIcon.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetIcon().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置个人的头像.", action = ActionSetIcon.class)
	@PUT
	@Path("icon")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void setIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("头像文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionSetIcon.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetIcon().execute(effectivePerson, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置个人的头像 MockPutToPost.", action = ActionSetIcon.class)
	@POST
	@Path("icon/mockputtopost")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void setIconMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("头像文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionSetIcon.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetIcon().execute(effectivePerson, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/** 为前台flash切图兼容ie9单独增加的方法 */
	@JaxrsMethodDescribe(value = "设置头像.为了兼容前台增加的POST方法.", action = ActionSetIconOctetStream.class)
	@POST
	@Path("icon")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void uploadPostOctetStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			byte[] bytes) {
		ActionResult<ActionSetIconOctetStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetIconOctetStream().execute(effectivePerson, bytes);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定个人的密码.", action = ActionSetPassword.class)
	@PUT
	@Path("password")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetPassword.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetPassword().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "更新指定个人的密码 MockPutToPost.", action = ActionSetPassword.class)
	@POST
	@Path("password/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPasswordMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionSetPassword.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetPassword().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

}
