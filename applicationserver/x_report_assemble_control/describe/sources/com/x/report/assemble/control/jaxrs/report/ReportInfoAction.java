package com.x.report.assemble.control.jaxrs.report;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportWorkflowStart;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;

@Path("report")
@JaxrsDescribe("汇报信息管理服务")
public class ReportInfoAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ReportInfoAction.class);

	@JaxrsMethodDescribe(value = "列出某一个年份的所有五项重点工作", action = ActionListByYear.class)
	@GET
	@Path("filter/list/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByYear.Wo>> result = new ActionResult<>();
		
		try {
			result = new ActionListByYear().execute(request, effectivePerson, year);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取汇报信息的内容（查看）", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		
		try {
			result = new ActionGet().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "汇报业务处理人和读者作者信息修改保存", action = ActionWorkAndPersonInfoSave.class)
	@POST
	@Path("workperson/save")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void workAndPersonInfoSave( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							  JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionWorkAndPersonInfoSave.Wo> result = new ActionResult<>();

		try {
			result = new ActionWorkAndPersonInfoSave().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "汇报业务处理人和读者作者信息修改提交", action = ActionWorkAndPersonInfoSubmit.class)
	@POST
	@Path("workperson/submit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void workAndPersonInfoSubmit( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											 JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionWorkAndPersonInfoSubmit.Wo> result = new ActionResult<>();

		try {
			result = new ActionWorkAndPersonInfoSubmit().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "同步指定汇报的审批流程流转信息", action = ActionSyncWorkFlowInfo.class)
	@POST
	@Path("wfsync/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void syncWorkFlow( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报信息ID") @PathParam("id") String id,
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ActionSyncWorkFlowInfo().execute( request, effectivePerson, id, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "修改指定汇报信息的状态标识", action = ActionModifyReportStatus.class)
	@POST
	@Path("status/{id}/modify")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void modifyReportStatus( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报信息ID") @PathParam("id") String id,
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ActionModifyReportStatus().execute( request, effectivePerson, id, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "测试一下生成汇报{\"date\":\"2017-09-03\"}", action = ActionReportCreateImmediately.class)
	@POST
	@Path("createImmediately")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createImmediately(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionReportCreateImmediately().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionSettingInfoProcess(e, "新建或者更新系统配置信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe( value = "保存领导阅知审核意见.", action = ActionSaveOpinion.class )
	@PUT
	@Path("opinion/{reportId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveOpinion( @Context HttpServletRequest request,  
			@JaxrsParameterDescribe("汇报信息文档ID") @PathParam("reportId") String reportId, 
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionSaveOpinion.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionSaveOpinion().execute(request, effectivePerson, reportId, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "列示符合过滤条件的汇报信息内容, 下一页.", action = ActionListNextWithFilter.class )
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionListNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "列示符合过滤条件的审核过的汇报信息内容, 下一页.", action = ActionListMyAuditNextWithFilter.class )
	@PUT
	@Path("filter/list/{id}/next/{count}/audit")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyAuditNextWithFilter( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListMyAuditNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionListMyAuditNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "根据指定的日期和每页多少条计算日期所在的页码数", action = ActionGetPageNumberForDay.class )
	@GET
	@Path("page/date/{date}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPageNumberForDay( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("指定查询页码的日期") @PathParam("date") String date, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionGetPageNumberForDay.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionGetPageNumberForDay().execute( request, date, count, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "根据汇报的页码以及每页显示的条目数列示用户所有有汇报的日期，并且获得汇报的内容", action = ActionListDayForPage.class )
	@GET
	@Path("list/date/page/{page}/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDayForPage( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("页码") @PathParam("page") Integer page, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count ) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionListDayForPage.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionListDayForPage().execute(request, page, count, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        return ResponseFactory.getDefaultActionResultResponse(result);
    }
	
	@JaxrsMethodDescribe( value = "根据汇报的年份月份列示用户所有有汇报的日期，并且获得汇报的内容.", action = ActionListDayByYearMonth.class )
	@GET
	@Path("list/date/year/{year}/month/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDayByYearMonth( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("汇报的年份") @PathParam("year") String year, 
			@JaxrsParameterDescribe("汇报的月份") @PathParam("month") String month ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListDayByYearMonth.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionListDayByYearMonth().execute( request, year, month, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据汇报概要文件，为文件涉及的所有汇报信息启动相应的汇报流程", action = ActionStartWorkflowWithProfile.class)
	@GET
	@Path("workflow/start/{profileId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response startWorkFlowInProfile( @Context HttpServletRequest request, @JaxrsParameterDescribe("汇报概要文件ID") @PathParam("profileId") String profileId ) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionStartWorkflowWithProfile().execute( request, effectivePerson, profileId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionReportWorkflowStart( e, profileId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe( value = "列示指定年月涉及汇报的组织列表.", action = ActionListReportUnits.class )
	@PUT
	@Path("list/units")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUnitNamesForReport( @Context HttpServletRequest request, 
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListReportUnits.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionListReportUnits().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}