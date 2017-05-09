package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;
import java.util.ArrayList;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ReportProcessLogListException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportDetailQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportFilterException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportListByWorkIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WrapInConvertException;
import com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapOutOkrWorkReportPersonLink;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.WrapOutOkrWorkReportProcessLog;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportPersonLinkService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

@Path( "okrworkreportbaseinfo" )
public class OkrWorkReportBaseInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrWorkReportBaseInfoAction.class );
	private OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	
	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportBaseInfo对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@GET
	@Path( "draft/{workId}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response draft(@Context HttpServletRequest request, @PathParam( "workId" ) String workId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		try {
			result = new ExcuteDraftReport().execute( request, effectivePerson, workId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDraftReport got an exception. " );
			logger.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新OkrWorkReportBaseInfo对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInOkrWorkReportBaseInfo wrapIn = null;
		Boolean check = true;		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkReportBaseInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteSave got an exception. " );
				logger.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 确定汇报工作流
	 * 1、先确定工作汇报工作流执行方式：1）工作管理员督办 - 中心工作阅知领导审阅； 2）工作部署者审阅
	 * 2、如果是方式1）
		    a.判断系统设置中是否有设置工作管理员
            b.如果有设置工作管理员，那么下一步处理者为工作管理员，如果没有设置工作管理员，那么判断中心工作是否有设置阅知领导
            c.如果中心工作没有设置阅知领导，那么下一步处理者为工作部署者审阅，并且在汇报的descript中说明原因
	 * 3、汇报工作流执行方式生效工作层级
	 * 
	 * @param request
	 * @param wrapIn
	 * @return
	 */
	@Path( "submit" )
	@HttpMethodDescribe(value = "提交工作汇报.", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submit( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInOkrWorkReportBaseInfo wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkReportBaseInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteSubmit().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteSubmit got an exception. " );
				logger.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkReportBaseInfo数据对象.", response = WrapOutId.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDelete got an exception. " );
			logger.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除OkrWorkReportBaseInfo数据对象.", response = WrapOutId.class)
	@GET
	@Path( "dispatch/{id}/over" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response dispatchOver( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDispatchToOver().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDispatchToOver got an exception. " );
			logger.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportBaseInfo对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@GET
	@Path( "{id}" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteGet got an exception. " );
			logger.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据工作ID获取OkrWorkReportBaseInfo对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@GET
	@Path( "list/work/{workId}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWork(@Context HttpServletRequest request, @PathParam( "workId" ) String workId) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<List<WrapOutOkrWorkReportBaseInfo>>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
		OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
		OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
		BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
		BeanCopyTools<OkrWorkReportProcessLog, WrapOutOkrWorkReportProcessLog> okrWorkReportProcessLog_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportProcessLog.class, WrapOutOkrWorkReportProcessLog.class, null, WrapOutOkrWorkReportProcessLog.Excludes);
		List<WrapOutOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		List<String> ids = null;
		List<String> logsIds = null;
		
		if( workId == null || workId.isEmpty() ){
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try {
				ids = okrWorkReportQueryService.listByWorkId( workId );
				if( ids != null && !ids.isEmpty()){
					okrWorkReportBaseInfoList = okrWorkReportQueryService.listByIds( ids );
					if( okrWorkReportBaseInfoList != null && !okrWorkReportBaseInfoList.isEmpty() ){
						wraps = wrapout_copier.copy( okrWorkReportBaseInfoList );
						for( WrapOutOkrWorkReportBaseInfo wrap : wraps ){
							try {
								logsIds = okrWorkReportProcessLogService.listByReportId( wrap.getId() );
								if( logsIds !=null ){
									okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( logsIds );
									if( okrWorkReportProcessLogList != null ){
										wrap.setProcessLogs( okrWorkReportProcessLog_wrapout_copier.copy( okrWorkReportProcessLogList ) );
									}
								}
							} catch (Exception e) {
								Exception exception = new ReportProcessLogListException( e, wrap.getId() );
								result.error( exception );
								logger.error( e, effectivePerson, request, null);
							}
							try {
								okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( wrap.getId() );
								if( okrWorkReportDetailInfo != null ){
									wrap.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
									wrap.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
									wrap.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
								}
							} catch (Exception e) {
								Exception exception = new WorkReportDetailQueryByIdException( e, wrap.getId() );
								result.error( exception );
								logger.error( e, effectivePerson, request, null);
							}
							String workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
							if( workDetail != null && !workDetail.isEmpty() ){
								wrap.setTitle( workDetail );
							}
						}
						
						SortTools.asc( wraps,"reportCount" );
						result.setData( wraps );
					}
				}
			} catch ( Exception e ) {
				Exception exception = new WorkReportListByWorkIdException( e, workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportBaseInfo[草稿],下一页.", response = WrapOutOkrWorkReportBaseInfo.class, request = JsonElement.class)
	@PUT
	@Path( "draft/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDraftNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<>();
		OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
		List<WrapOutOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			wrapIn.addQueryProcessStatus( "草稿" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if ( check ) {
			try{
				okrWorkReportBaseInfoList = okrWorkReportQueryService.listNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportQueryService.getCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkReportBaseInfoList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportBaseInfo wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch( Exception e ){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportBaseInfo>() );
		}	
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportBaseInfo[草稿],上一页.", response = WrapOutOkrWorkReportBaseInfo.class, request = JsonElement.class)
	@PUT
	@Path( "draft/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDraftPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<>();
		OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
		List<WrapOutOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );		
		OkrUserCache  okrUserCache  = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}	
		}
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			wrapIn.addQueryProcessStatus( "草稿" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			try{
				okrWorkReportBaseInfoList = okrWorkReportQueryService.listPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportQueryService.getCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkReportBaseInfoList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportBaseInfo wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch( Exception e ){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportBaseInfo>() );
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[处理中（待办）],下一页.", response = WrapOutOkrWorkReportPersonLink.class, request = JsonElement.class)
	@PUT
	@Path( "task/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyTaskNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
		BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}	
		}
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "处理中" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			try{
				okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
				wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportPersonLink wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch( Exception e ){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportPersonLink>() );
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[处理中（待办）],上一页.", response = WrapOutOkrWorkReportPersonLink.class, request = JsonElement.class)
	@PUT
	@Path( "task/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyTaskPrevWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
		BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "处理中" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			try{
				okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
				wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportPersonLink wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch( Exception e ){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportPersonLink>() );
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已处理（已办）],下一页.", response = WrapOutOkrWorkReportPersonLink.class, request = JsonElement.class)
	@PUT
	@Path( "process/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyProcessNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
		BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}	
		}
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}	
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "已处理" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			try{
				okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
				wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportPersonLink wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch(Exception e){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportPersonLink>() );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已处理（已办）],上一页.", response = WrapOutOkrWorkReportPersonLink.class, request = JsonElement.class)
	@PUT
	@Path( "process/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyProcessPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
		BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "已处理" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if(check){
			try{
				okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
				wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportPersonLink wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch(Exception e){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportPersonLink>() );
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已归档],下一页.", response = WrapOutOkrWorkReportPersonLink.class, request = JsonElement.class)
	@PUT
	@Path( "archive/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchiveNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
		BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}	
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "已归档" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			try{
				okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
				wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportPersonLink wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch(Exception e){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportPersonLink>() );
		}	
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已归档],上一页.", response = WrapOutOkrWorkReportPersonLink.class, request = JsonElement.class)
	@PUT
	@Path( "archive/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchivePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
		BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "已归档" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			try{
				okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
				wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
				String workDetail = null;
				if( wraps != null && !wraps.isEmpty() ){
					for( WrapOutOkrWorkReportPersonLink wrap : wraps ){
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
						if( workDetail != null && !workDetail.isEmpty() ){
							wrap.setTitle( workDetail );
						}
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch(Exception e){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkReportPersonLink>() );
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
