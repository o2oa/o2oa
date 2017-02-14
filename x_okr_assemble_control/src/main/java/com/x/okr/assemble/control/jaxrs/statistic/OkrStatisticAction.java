package com.x.okr.assemble.control.jaxrs.statistic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.service.OkrCenterWorkReportStatisticService;
import com.x.okr.assemble.control.timertask.entity.BaseWorkReportStatisticEntity;
import com.x.okr.assemble.control.timertask.entity.CenterWorkReportStatisticEntity;
import com.x.okr.entity.OkrCenterWorkReportStatistic;


@Path( "workreportstatistic" )
public class OkrStatisticAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrStatisticAction.class );
	private BeanCopyTools<OkrCenterWorkReportStatistic, WrapOutOkrWorkReportStatistic> wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkReportStatistic.class, WrapOutOkrWorkReportStatistic.class, null, WrapOutOkrWorkReportStatistic.Excludes);
	private OkrCenterWorkReportStatisticService okrCenterWorkReportStatisticService = new OkrCenterWorkReportStatisticService();
	private DateOperation dateOperation = new DateOperation();

	@Path( "filter/center" )
	@HttpMethodDescribe( value = "根据条件获取OkrCenterWorkReportStatistic部分信息对象.", request = OkrCenterWorkReportStatisticWrapInFilter.class, response = WrapOutOkrWorkReportStatistic.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listByCondition( @Context HttpServletRequest request, OkrCenterWorkReportStatisticWrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkReportStatistic>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportStatistic> wraps = null;
		List<OkrCenterWorkReportStatistic> okrCenterWorkReportStatisticList = null;
		Integer year = null;
		Integer month = null;
		Integer week = null;
		Date now = new Date();
		String reportCycle = null;
		String centerId = null;
		Boolean check = true;
		
		if( wrapIn == null ){
			check = false;
			logger.error( "wrapIn is null, system can not get any object." );
			result.error( new Exception("传入的参数为空，无法进行查询！") );
			result.setUserMessage("传入的参数为空，无法进行查询！");
		}
		
		if( check ){
			centerId = wrapIn.getCenterId();
			year = wrapIn.getYear();
			month = wrapIn.getMonth();
			week = wrapIn.getWeek();
			reportCycle = wrapIn.getReportCycle();
		}
		if( check ){
			try {
				okrCenterWorkReportStatisticList = okrCenterWorkReportStatisticService.list( centerId, reportCycle, year, month, week );
			} catch (Exception e) {
				check = false;
				logger.error( "system list work report statistic got an exception.", e );
				result.error( e );
				result.setUserMessage("系统在查询中心工作统计信息列表时发生异常！");
			}
		}
		
		if( check ){
			if( okrCenterWorkReportStatisticList != null ){
				try {
					wraps = wrapout_copier.copy( okrCenterWorkReportStatisticList );
					for( OkrCenterWorkReportStatistic wrap : wraps ){
						wrap.setReportStatistic( null );
					}
					SortTools.desc( wraps, "statisticTime" );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					logger.error( "system copy object to wrap got an exception.", e );
					result.error( e );
					result.setUserMessage("系统在查询中心工作统计信息列表时发生异常！");
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取OkrCenterWorkReportStatistic列表.", response = WrapOutOkrWorkReportStatistic.class)
	@GET
	@Path( "statistic/{id}/{parentWorkId}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticDetail( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "parentWorkId" ) String parentWorkId ) {
		ActionResult<List<BaseWorkReportStatisticEntity>> result = new ActionResult<List<BaseWorkReportStatisticEntity>>();
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic  = null;
		CenterWorkReportStatisticEntity centerWorkReportStatisticEntity = null;
		List<BaseWorkReportStatisticEntity> baseWorkReportStatisticEntityList = null;
		List<BaseWorkReportStatisticEntity> baseWorkReportStatisticEntityList_result = new ArrayList<BaseWorkReportStatisticEntity>();
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			logger.error( "id is null, system can not get any object." );
			result.error( new Exception("传入的参数'id'为空，无法进行查询！") );
			result.setUserMessage("传入的参数'id'为空，无法进行查询！");
		}
		
		if( check ){
			if( "0".equals( parentWorkId ) || "(0)".equals( parentWorkId ) || "".equals( parentWorkId )){
				parentWorkId = null;
			}
		}
		
		if( check ){
			try {
				okrCenterWorkReportStatistic = okrCenterWorkReportStatisticService.get( id );
			} catch (Exception e) {
				check = false;
				logger.error( "system get center work report statistic info with id got an exception.", e );
				result.error( e );
				result.setUserMessage("系统在根据ID查询中心工作统计信息列表时发生异常！");
			}
		}
		
		if( check ){
			if( okrCenterWorkReportStatistic != null && okrCenterWorkReportStatistic.getReportStatistic() != null ){
				//解析统计内容
				centerWorkReportStatisticEntity = gson.fromJson( okrCenterWorkReportStatistic.getReportStatistic(), CenterWorkReportStatisticEntity.class );
			}
		}
		
		if( check ){
			if( centerWorkReportStatisticEntity != null){
				baseWorkReportStatisticEntityList = centerWorkReportStatisticEntity.getWorkReportStatisticEntityList();
			}
		}
		
		if( check ){
			if( baseWorkReportStatisticEntityList != null && !baseWorkReportStatisticEntityList.isEmpty() ){
				for( BaseWorkReportStatisticEntity statistic : baseWorkReportStatisticEntityList ){
					if( parentWorkId == null ){
						//只要第一层
						if( statistic.getParentWorkId() == null || "".equals( statistic.getParentWorkId() ) || statistic.getParentWorkId().isEmpty() ){
							baseWorkReportStatisticEntityList_result.add( statistic );
						}
					} else {
						if( "all".equals( parentWorkId )){
							baseWorkReportStatisticEntityList_result.add( statistic );
						}else{
							if( statistic.getParentWorkId() != null && parentWorkId.equals( statistic.getParentWorkId() ) ){
								baseWorkReportStatisticEntityList_result.add( statistic );
							}
						}
					}
				}
			}
		}
		
		if( check ){
			result.setData( baseWorkReportStatisticEntityList_result );
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除OkrCenterWorkReportStatistic数据对象.", response = WrapOutOkrWorkReportStatistic.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrWorkReportStatistic> result = new ActionResult<>();
		//EffectivePerson currentPerson = this.effectivePerson(request);
		
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic = null;		
		try{
			okrCenterWorkReportStatistic = okrCenterWorkReportStatisticService.get( id );
			if( okrCenterWorkReportStatistic != null ){
				okrCenterWorkReportStatisticService.delete( id );
			}			
			result.setUserMessage( id );
		}catch(Exception e){
			logger.error( "system delete okrCenterWorkReportStatisticService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除中心工作汇报情况统计数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
