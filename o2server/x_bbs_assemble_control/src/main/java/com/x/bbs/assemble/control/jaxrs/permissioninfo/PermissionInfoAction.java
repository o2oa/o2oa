package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectIdEmpty;

/**
 * @author sword
 */
@Path("permission")
@JaxrsDescribe("权限查询服务")
public class PermissionInfoAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(PermissionInfoAction.class);

	@JaxrsMethodDescribe(value = "查询当前用户的操作权限.", action = ActionGetUserPermission.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getUserPermission(@Suspended final AsyncResponse asyncResponse,
											  @Context HttpServletRequest request) {
		ActionResult<ActionGetUserPermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetUserPermission().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询用户在指定板块中的所有操作权限.", action = ActionGetSectionOperationPermission.class)
	@GET
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getSectionOperationPermissoin(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("版块ID") @PathParam("sectionId") String sectionId) {
		ActionResult<ActionGetSectionOperationPermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetSectionOperationPermission().execute(request, effectivePerson, sectionId);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询用户对指定主题的所有操作权限.", action = ActionGetSubjectOperationPermissoin.class)
	@GET
	@Path("subject/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getSubjectOperationPermissoin(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴ID") @PathParam("subjectId") String subjectId) {
		ActionResult<ActionGetSubjectOperationPermissoin.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (subjectId == null || subjectId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionGetSubjectOperationPermissoin().execute(request, effectivePerson, subjectId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionConfigSettingProcess(e, "查询用户对指定主题的所有操作权限时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询用户对指定主题的所有操作权限.", action = ActionCheckSubjectPublishable.class)
	@GET
	@Path("subjectPublishable/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void subjectPublishable(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("版块ID") @PathParam("sectionId") String sectionId) {
		ActionResult<ActionCheckSubjectPublishable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckSubjectPublishable().execute(request, effectivePerson, sectionId);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询用户是否可以对指定主题进行回复.", action = ActionCheckReplyPublishable.class)
	@GET
	@Path("replyPublishable/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void replyPublishable(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴ID") @PathParam("subjectId") String subjectId) {
		ActionResult<ActionCheckReplyPublishable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (subjectId == null || subjectId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionCheckReplyPublishable().execute(request, effectivePerson, subjectId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionConfigSettingProcess(e, "查询用户是否可以对指定主题进行回复时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
