package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.List;

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
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionPermissionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionRoleCodeEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionIdEmpty;

@Path("user/permission")
@JaxrsDescribe("权限管理服务")
public class PermissionInfoAdminAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(PermissionInfoAdminAction.class);

	@JaxrsMethodDescribe(value = "获取指定的角色Code绑定的所有PermissionInfo的信息列表.", action = ActionListPermissionByRoleCode.class)
	@GET
	@Path("role/{roleCode}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPermissionByRoleCode(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("角色编码") @PathParam("roleCode") String roleCode) {
		ActionResult<List<ActionListPermissionByRoleCode.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (roleCode == null || roleCode.isEmpty()) {
				check = false;
				Exception exception = new ExceptionRoleCodeEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionListPermissionByRoleCode().execute(request, effectivePerson, roleCode);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionPermissionInfoProcess(e,
						"获取指定的角色Code绑定的所有PermissionInfo的信息列表时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定的论坛绑定的所有PermissionInfo的信息列表.", action = ActionListPermissionByRoleCode.class)
	@GET
	@Path("forum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPermissionByForumId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("论坛ID") @PathParam("forumId") String forumId) {
		ActionResult<List<ActionListPermissionByRoleCode.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (forumId == null || forumId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionListPermissionByRoleCode().execute(request, effectivePerson, forumId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionPermissionInfoProcess(e, "获取指定的论坛绑定的所有PermissionInfo的信息列表时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定的版块绑定的所有PermissionInfo的信息列表.", action = ActionListPermissionBySection.class)
	@GET
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPermissionBySection(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("版块ID") @PathParam("sectionId") String sectionId) {
		ActionResult<List<ActionListPermissionBySection.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (sectionId == null || sectionId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionListPermissionBySection().execute(request, effectivePerson, sectionId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionPermissionInfoProcess(e, "获取指定的版块绑定的所有PermissionInfo的信息列表时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}