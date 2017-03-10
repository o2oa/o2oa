package com.x.okr.assemble.control.jaxrs.statistic;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.common.excel.writer.WorkReportStatisticExportExcelWriter;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.timertask.St_WorkReportContent;


@Path( "streportcontent" )
public class OkrStatisticReportContentAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrStatisticReportContentAction.class );

	@Path( "filter/list" )
	@HttpMethodDescribe( value = "根据条件获取OkrStatisticReportContent部分信息对象.", request = JsonElement.class, response = WrapOutOkrStatisticReportContentCenter.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listByCondition( @Context HttpServletRequest request, JsonElement jsonElement ){
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrStatisticReportContentCenter>> result = new ActionResult<>();
		WrapInFilterOkrStatisticReportContent wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterOkrStatisticReportContent.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteFilterList().execute( request, effectivePerson, wrapIn, false );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteFilterList got an exception. " );
				logger.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@Path( "filter/tree" )
	@HttpMethodDescribe( value = "根据条件获取OkrStatisticReportContent部分信息对象.", request = JsonElement.class, response = WrapOutOkrStatisticReportContentCenter.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response treeByCondition( @Context HttpServletRequest request, JsonElement jsonElement ){
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrStatisticReportContentCenter>> result = new ActionResult<>();
		WrapInFilterOkrStatisticReportContent wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterOkrStatisticReportContent.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteFilterList().execute( request, effectivePerson, wrapIn, true );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteFilterList got an exception. " );
				logger.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@Path( "filter/sub/{parentId}" )
	@HttpMethodDescribe( value = "根据条件获取OkrStatisticReportContent部分信息对象.", request = JsonElement.class, response = WrapOutOkrStatisticReportContentCenter.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listByCondition( @Context HttpServletRequest request, @PathParam( "parentId" ) String parentId, JsonElement jsonElement ){
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrStatisticReportContentCenter>> result = new ActionResult<>();
		WrapInFilterOkrStatisticReportContent wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterOkrStatisticReportContent.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteFilterSubList().execute( request, effectivePerson, parentId, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteFilterSubList got an exception. " );
				logger.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@Path( "date/list" )
	@HttpMethodDescribe( value = "根据条件获取统计的日期列表.", request = JsonElement.class, response = WrapOutOkrReportSubmitStatusDate.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listDateByCondition( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrReportSubmitStatusDate>> result = new ActionResult<>();
		WrapInFilterOkrStatisticReportContent wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterOkrStatisticReportContent.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteDateList().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteDateList got an exception. " );
				logger.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "测试工作汇报状态统计.", response = WrapOutOkrStatisticReportContent.class)
	@GET
	@Path( "process" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response process( @Context HttpServletRequest request ) {
		ActionResult<WrapOutOkrStatisticReportContent> result = new ActionResult<>();
		OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
		List<String> workIds = null;
		String status = "正常";// 如果不需要在统计里展示 ，就应该为已归档
		try {
			workIds = okrWorkBaseInfoService.listAllDeployedWorkIds( null, status );
		} catch (Exception e) {
			logger.error( e );
		}
		if (workIds != null && !workIds.isEmpty()) {
			new St_WorkReportContent().analyseWorksReportContent( workIds );
		}

		ThisApplication.setWorkReportStatisticTaskRunning(false);
		logger.debug("Timertask[WorkReportStatistic] completed and excute success.");

		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@Path( "export" )
	@HttpMethodDescribe( value = "根据条件获取OkrStatisticReportContent部分信息对象.", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response export( @Context HttpServletRequest request, @Context HttpServletResponse response, JsonElement jsonElement ){
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		String centerId = null;
		String flag = null;
		List<WrapOutOkrStatisticReportContentCenter> exportDataList = null;
		WrapInFilterOkrStatisticReportContent wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterOkrStatisticReportContent.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilterOkrStatisticReportContent();
				wrapIn.setCenterId("none");
			}
		}
				
		if (check) {
			try {
				request.setCharacterEncoding("UTF-8");
			} catch (UnsupportedEncodingException e ) {
				logger.warn( "system set character encoding for request got an exception. " );
				logger.error( e );
			}
		}
		if (check) {
			if ("all".equals( centerId )) {
				wrapIn.setCenterId( null );
			}
		}
		try {
			exportDataList = new ExcuteFilterExport().execute( request, effectivePerson, wrapIn.getReportCycle(), wrapIn.getCenterId(), wrapIn.getStatisticTimeFlag()  );
		} catch (Exception e ) {
			logger.warn( "system query data for export got an exception. " );
			logger.error( e );
		}
		if ( check ) {
			try {
				flag = new WorkReportStatisticExportExcelWriter().writeExcel( exportDataList );
				result.setData( new WrapOutId(flag) );
			} catch ( Exception e ) {
				logger.warn( "system write export data to excel file got an exception. " );
				logger.error( e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}
