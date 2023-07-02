package com.x.organization.assemble.personal.jaxrs.icon;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("icon")
@JaxrsDescribe("头像")
/**
 * 用于无权限取得指定用户头像
 **/
public class IconAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(IconAction.class);

	@JaxrsMethodDescribe(value = "获取个人头像.", action = ActionGetWithPerson.class)
	@GET
	@Path("{person}")
	/**
	 多个适配外部取头像,比如阿里
	 * */
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public void getIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("应用标识") @PathParam("person") String person) {
		ActionResult<ActionGetWithPerson.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithPerson().execute(effectivePerson, person);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}