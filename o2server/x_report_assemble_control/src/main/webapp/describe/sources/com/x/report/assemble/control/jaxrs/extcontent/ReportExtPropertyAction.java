package com.x.report.assemble.control.jaxrs.extcontent;

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
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;

@Path("ext")
@JaxrsDescribe("汇报扩展信息管理服务")
public class ReportExtPropertyAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ReportExtPropertyAction.class);
	
	@JaxrsMethodDescribe(value = "根据ID获取汇报扩展信息对象", action = ActionGetContentWithId.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse,  @Context HttpServletRequest request,
			@JaxrsParameterDescribe("ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGetContentWithId.Wo> result = new ActionResult<>();
		try {
			result = new ActionGetContentWithId().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据汇报ID以及过滤条件获取汇报扩展信息列表", action = ActionFilterExtWithReport.class)
	@PUT
	@Path("filter/list/{reportId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilter(@Suspended final AsyncResponse asyncResponse,  @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报ID") @PathParam("reportId") String reportId, 
			@JaxrsParameterDescribe("过滤条件") JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionFilterExtWithReport.Wo>> result = new ActionResult<>();
		try {
			result = new ActionFilterExtWithReport().execute(request, effectivePerson, reportId, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe( value = "保存汇报扩展信息", action = ActionSaveExtContent.class )
	@POST
	@Path("save/{reportId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveGuanai( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("汇报ID") @PathParam("reportId") String reportId,
			@JaxrsParameterDescribe("扩展信息对象") JsonElement jsonElement) {
		ActionResult<ActionSaveExtContent.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSaveExtContent().execute( request, effectivePerson, reportId, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSettingInfoProcess( e, "保存关爱员工信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除关爱员工", action = ActionDeleteExtContent.class)
	@DELETE
	@Path("{reportId}/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteGuanai(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报扩展信息ID") @PathParam("reportId") String reportId, 
			@JaxrsParameterDescribe("汇报扩展信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDeleteExtContent.Wo> result = new ActionResult<>();
		try {
			result = new ActionDeleteExtContent().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe( value = "更新汇报扩展信息排序号", action = ActionUpdateOrderNumber.class )
	@PUT
	@Path("order/update/{reportId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateGuanaiOrder( @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报扩展信息ID") @PathParam("reportId") String reportId, 
			@JaxrsParameterDescribe("汇报扩展信息顺序对象列表") JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionUpdateOrderNumber().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSettingInfoProcess( e, "更新爱关员工信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
}