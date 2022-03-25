package com.x.program.center.jaxrs.datastructure;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("datastructure")
@JaxrsDescribe("调试")
public class DataStructureAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(DataStructureAction.class);

	@GET
	@Path("modules/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@JaxrsMethodDescribe(value = "列示所有模块以及实体类依赖信息", action = ActionGetAllModuleStructure.class)
	public void listAllModules(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		ActionResult<List<ActionGetAllModuleStructure.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetAllModuleStructure().execute( effectivePerson, request );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@GET
	@Path("tables/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@JaxrsMethodDescribe(value = "列示所有的数据表结构设计", action = ActionGetAllTableStructure.class)
	public void listAllTables(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		ActionResult<List<ActionGetAllTableStructure.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetAllTableStructure().execute( effectivePerson, request );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@GET
	@Path("fileds/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@JaxrsMethodDescribe(value = "列示所有的数据表结构设计(按列展现)", action = ActionGetAllTableFields.class)
	public void listAllFields(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		ActionResult<List<ActionGetAllTableFields.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetAllTableFields().execute( effectivePerson, request );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}