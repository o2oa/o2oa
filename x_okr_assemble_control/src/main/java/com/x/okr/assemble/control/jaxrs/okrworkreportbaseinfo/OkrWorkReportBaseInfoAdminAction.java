package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;
import java.util.ArrayList;
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

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.MemberTerms;
import com.x.base.core.application.jaxrs.NotEqualsTerms;
import com.x.base.core.application.jaxrs.NotInTerms;
import com.x.base.core.application.jaxrs.NotMemberTerms;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilterAdminCenterWorkInfo;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.WrapOutOkrWorkReportProcessLog;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportOperationService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

@Path( "admin/okrworkreportbaseinfo" )
public class OkrWorkReportBaseInfoAdminAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkReportBaseInfoAdminAction.class );
	private BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
	private BeanCopyTools<OkrWorkReportProcessLog, WrapOutOkrWorkReportProcessLog> okrWorkReportProcessLog_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportProcessLog.class, WrapOutOkrWorkReportProcessLog.class, null, WrapOutOkrWorkReportProcessLog.Excludes);
	private OkrWorkReportOperationService okrWorkReportBaseInfoService = new OkrWorkReportOperationService();
	private OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();

	@HttpMethodDescribe(value = "根据ID删除OkrWorkReportBaseInfo数据对象.", response = WrapOutId.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		List<OkrTask> taskList = null;
		List<String> taskTargetName = new ArrayList<String>();
		Boolean check = true;
		
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new WorkReportIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try{
				okrWorkReportBaseInfoService.delete( id, currentPerson.getName() );
				result.setData( new WrapOutId( id ));
			}catch(Exception e){
				Exception exception = new WorkReportDeleteException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				taskList = okrTaskService.listIdsByReportId( id );
			} catch (Exception e) {
				check = false;
				logger.warn( "system get task by report id got an exception" );
				logger.error(e);
			}
		}
		if( check ){
			if( taskList != null && !taskList.isEmpty() ){
				List<String> workTypeList = new ArrayList<String>();
				for( OkrTask task : taskList ){
					if( !taskTargetName.contains( task.getTargetIdentity() )){
						try{
							workTypeList.clear();
							workTypeList.add( task.getWorkType() );
							okrWorkReportTaskCollectService.checkReportCollectTask( task.getTargetIdentity(), workTypeList );
						}catch( Exception e ){
							logger.warn( "待办信息删除成功，但对汇报者进行汇报待办汇总发生异常。");
							logger.error(e);
						}
					}
				}
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					okrWorkDynamicsService.reportDynamic(
							okrWorkReportBaseInfo.getCenterId(), 
							okrWorkReportBaseInfo.getTitle(), 
							okrWorkReportBaseInfo.getWorkId(), 
							okrWorkReportBaseInfo.getWorkTitle(), 
							okrWorkReportBaseInfo.getTitle(), 
							id, 
							"保存中心工作", 
							currentPerson.getName(), 
							currentPerson.getName(), 
							currentPerson.getName(), 
							"删除中心工作：" + okrWorkReportBaseInfo.getTitle(),
							"中心工作删除成功！" );
				} catch (Exception e) {
					logger.warn( "okrWorkDynamicsService reportDynamic got an exception" );
					logger.error(e);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportBaseInfo对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutOkrWorkReportBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		List<String> ids = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new WorkReportIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					wrap = wrapout_copier.copy( okrWorkReportBaseInfo );
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkReportWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrap.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new WorkNotExistsException( wrap.getWorkId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException( e, wrap.getWorkId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);	
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( id );
					if( okrWorkReportDetailInfo != null ){
						wrap.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
						wrap.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
						wrap.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
						wrap.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo() );
					}
				} catch (Exception e) {
					logger.warn( "system get okrWorkReportDetailInfo got an exception" );
					logger.error(e);
				}
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					ids = okrWorkReportProcessLogService.listByReportId( id );
					if( ids !=null ){
						okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( ids );
						if( okrWorkReportProcessLogList != null ){
							wrap.setProcessLogs( okrWorkReportProcessLog_wrapout_copier.copy( okrWorkReportProcessLogList ) );
						}
					}
				} catch (Exception e) {
					logger.warn( "system get okrWorkReportDetailInfo got an exception."  );
					logger.error(e);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的WrapOutOkrWorkReportBaseInfo,下一页.", response = WrapOutOkrWorkReportBaseInfo.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		WrapInFilterAdminCenterWorkInfo wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterAdminCenterWorkInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilterAdminCenterWorkInfo();
			}
		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
				likesMap.put( "creatorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "currentProcessorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "description", wrapIn.getFilterLikeContent() );
				likesMap.put( "processStatus", wrapIn.getFilterLikeContent() );
				likesMap.put( "reporterIdentity", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, false, wrapIn.getOrder() );
			}catch( Exception e ){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);	
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的WrapOutOkrWorkReportBaseInfo,下一页.", response = WrapOutOkrWorkReportBaseInfo.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListPrevWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		WrapInFilterAdminCenterWorkInfo wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterAdminCenterWorkInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilterAdminCenterWorkInfo();
			}
		}	
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
				likesMap.put( "creatorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "currentProcessorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "description", wrapIn.getFilterLikeContent() );
				likesMap.put( "processStatus", wrapIn.getFilterLikeContent() );
				likesMap.put( "reporterIdentity", wrapIn.getFilterLikeContent() );
			}
		}		
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				result = this.standardListPrev( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, false, wrapIn.getOrder() );
			}catch( Exception e ){
				Exception exception = new WorkReportFilterException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);	
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
}
