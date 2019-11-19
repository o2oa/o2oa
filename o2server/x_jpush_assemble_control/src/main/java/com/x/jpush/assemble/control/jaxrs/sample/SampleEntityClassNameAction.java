package com.x.jpush.assemble.control.jaxrs.sample;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.jpush.assemble.control.ThisApplication;


@Path("sample")
@JaxrsDescribe("示例-信息管理服务")
public class SampleEntityClassNameAction extends StandardJaxrsAction{
	
	private static Logger logger = LoggerFactory.getLogger( SampleEntityClassNameAction.class );
	
	
	@JaxrsMethodDescribe( value = "根据ID获取示例-信息", action = ActionGet.class )
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @JaxrsParameterDescribe("示例-信息ID") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute( request, this.effectivePerson(request), id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionSampleEntityClassFind( e, "根据ID获取示例-信息时发生异常！" );
			result.error( exception );
			logger.error( e, this.effectivePerson(request), request, null);
		}	
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "获取所有的示例-信息", action = ActionListAll.class )
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<ActionListAll.Wo>> result = new ActionResult<>();
		Boolean check = true;
		if(check){
			try {
				result = new ActionListAll().execute( request, this.effectivePerson(request) );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSampleEntityClassFind( e, "根据ID获取示例-信息时发生异常！" );
				result.error( exception );
				logger.error( e, this.effectivePerson(request), request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "更新新示例-信息", action = ActionUpdate.class )
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("需要更新的-信息ID") @PathParam("id") String id, 
			@JaxrsParameterDescribe("需要更新的信息") JsonElement jsonElement ) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		Boolean check = true;

		if(check){
			try {
				result = new ActionUpdate().execute( request, this.effectivePerson(request), id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSampleEntityClassFind( e, "新建或者更新示例-信息时发生异常！" );
				result.error( exception );
				logger.error( e, this.effectivePerson(request), request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "创建新示例-信息", action = ActionSave.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, @JaxrsParameterDescribe("需要保存的信息") JsonElement jsonElement) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;

		if(check){
			try {
				result = new ActionSave().execute( request, this.effectivePerson(request), jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSampleEntityClassFind( e, "新建或者更新示例-信息时发生异常！" );
				result.error( exception );
				logger.error( e, this.effectivePerson(request), request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据标识ID删除信息.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse,  @Context HttpServletRequest request,  @JaxrsParameterDescribe("需要删除的数据ID") @PathParam("id") String id ) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute(request, this.effectivePerson(request), id);
		} catch (Exception e) {
			logger.error(e, this.effectivePerson(request), request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}