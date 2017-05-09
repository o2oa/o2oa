package com.x.portal.assemble.surface.jaxrs.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.portal.assemble.surface.wrapout.WrapOutPage;

@Path("page")
public class PageAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(PageAction.class);

	@HttpMethodDescribe(value = "获取Page内容.", response = WrapOutPage.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutPage> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示属于指定Portal的Page.", response = WrapOutPage.class)
	@GET
	@Path("list/portal/{portal}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request, @PathParam("portal") String portal) {
		ActionResult<List<WrapOutPage>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionList().execute(effectivePerson, portal);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示属于指定Portal的Page,其中Portal可以用name,alias和id标识,Page为在此Portal下的页面.", response = WrapOutPage.class)
	@GET
	@Path("{flag}/portal/{portalFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request, @PathParam("flag") String flag,
			@PathParam("portalFlag") String portalFlag) {
		ActionResult<WrapOutPage> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithPortal().execute(effectivePerson, flag, portalFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}