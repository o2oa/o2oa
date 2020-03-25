package com.x.okr.assemble.control.jaxrs.login;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;

@Path("logout")
@JaxrsDescribe("工作管理系统登出服务")
public class OkrLogoutAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLogin.class);

	@JaxrsMethodDescribe(value = "用户登出，删除已经缓存的用户身份信息", action = ActionLogout.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void logout(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<OkrUserCache> result = new ActionResult<>();

		try {
			result = new ActionLogout().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统用户登出过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
