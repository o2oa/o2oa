package com.x.okr.assemble.control.jaxrs.okrtask;
import java.util.ArrayList;
import java.util.Collection;
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
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapOutOkrWorkBaseInfo;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapOutOkrWorkReportBaseInfo;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;


@Path( "okrtask" )
public class OkrTaskAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrTaskAction.class );
	private BeanCopyTools<OkrTask, WrapOutOkrTask> wrapout_copier = BeanCopyToolsBuilder.create( OkrTask.class, WrapOutOkrTask.class, null, WrapOutOkrTask.Excludes);
	private BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> okrWorkReportBaseInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
	private BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseInfo> okrWorkBaseInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseInfo.class, null, WrapOutOkrWorkBaseInfo.Excludes);
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	private OkrWorkReportBaseInfoService okrWorkReportBaseInfoService = new OkrWorkReportBaseInfoService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	
	@HttpMethodDescribe(value = "获取我的所有工作汇报汇总的内容.", response = WrapOutOkrTaskCollect.class )
	@GET
	@Path( "department/reportTaskCollect/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showTaskCollect(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutOkrTaskCollect>> result = new ActionResult<>();
		List<WrapOutOkrTaskCollect> collectList = new ArrayList<WrapOutOkrTaskCollect>();	
		EffectivePerson currentPerson = this.effectivePerson(request);
		String userIdentity = null;
		String workAdminIdentity = null;
		List<OkrTask> taskList = null;
		OkrTask okrTask = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		WrapOutOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo = null;
		List<String> taskTypeList = new ArrayList<String>();
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( check ){
			userIdentity = okrUserCache.getLoginIdentityName() ;
		}
		
		if( check ){
			if( id == null ){
				check = false;
				result.error( new Exception( "传入的参数id为空，无法获取查询参数" ) );
				result.setUserMessage( "传入的参数id为空，无法获取查询参数" );
			}else{
				//查询该汇总待办信息
				try {
					okrTask = okrTaskService.get( id );
				} catch (Exception e) {
					logger.error( "system get okrTask by id got an exception.", e );
					check = false;
					result.setUserMessage( "系统根据ID获取待办信息时发生异常。" );
					result.error(e);
				}
			}
		}
		
		if( check ){
			if( okrTask != null ){
				taskTypeList.add( "工作汇报" );
				try {
					//获取所有符合条件的待办
					taskList = okrTaskService.listByTaskType( taskTypeList, userIdentity, okrTask.getWorkType() );
				} catch (Exception e) {
					check = false;
					logger.error( "system search task got an exception.", e);
					result.error( new Exception( "传入的参数id为空，无法获取查询参数" ) );
					result.setUserMessage( "传入的参数id为空，无法获取查询参数" );
				}
			}else{
				logger.error( "okrTask not exists{'id':'"+id+"'}." );
				check = false;
				result.setUserMessage( "系统根据ID未获取到待办信息，待办信息不存在。" );
				result.error( new Exception("系统根据ID未获取到待办信息，待办信息不存在。") );
			}
		}
		
		if( check ){
			try {
				workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				logger.error( "system get system config 'REPORT_SUPERVISOR' got an exception.", e );
				check = false;
				result.setUserMessage( "查询系统配置[REPORT_SUPERVISOR]时发生异常。" );
				result.error(e);
			}
		}
		
		if( check ){
			if( taskList != null && !taskList.isEmpty() ){
				for( OkrTask task : taskList ){
					try {
						okrWorkBaseInfo = okrWorkBaseInfoService.get( task.getWorkId() );
						if( okrWorkBaseInfo == null ){
							logger.error( "okrWorkBaseInfo{'id':'"+task.getWorkId()+"'} is not exsits." );
							check = false;
							result.setUserMessage( "汇报所关联的工作信息不存在。" );
						}
					} catch (Exception e) {
						logger.error( "system get okrWorkBaseInfo{'id':'"+task.getWorkId()+"'} got an exception.", e );
						check = false;
						result.setUserMessage( "系统根据工作ID查询工作信息时发生异常。" );
						result.error(e);
					}
					try {
						okrWorkReportBaseInfo = okrWorkReportBaseInfoService.get( task.getDynamicObjectId() );
					} catch (Exception e) {
						logger.error( "get okrWorkReportBaseInfo by id got an exception.", e );
					}
					try {
						okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( task.getDynamicObjectId() );
					} catch (Exception e) {
						logger.error( "get okrWorkReportBaseInfo by id got an exception.", e );
					}
					if( okrWorkReportBaseInfo != null ){
						try {
							wrapOutOkrWorkReportBaseInfo = okrWorkReportBaseInfo_wrapout_copier.copy( okrWorkReportBaseInfo );
							if( wrapOutOkrWorkReportBaseInfo != null ){
								//查询工作所对应的工作信息
								wrapOutOkrWorkReportBaseInfo.setWorkInfo(okrWorkBaseInfo_wrapout_copier.copy(okrWorkBaseInfo));	
							}
							
							if( okrWorkReportDetailInfo != null ){
								wrapOutOkrWorkReportBaseInfo.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
								wrapOutOkrWorkReportBaseInfo.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
								wrapOutOkrWorkReportBaseInfo.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo());
								wrapOutOkrWorkReportBaseInfo.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
							}else{
								logger.debug( ">>>>>>>>>>>>>>>>>>>>>okrWorkReportDetailInfo not exsits" );
							}
							
							if( wrapOutOkrWorkReportBaseInfo.getCreatorIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrapOutOkrWorkReportBaseInfo.getCreatorIdentity())){
								wrapOutOkrWorkReportBaseInfo.setIsCreator( true );
							}
							if( wrapOutOkrWorkReportBaseInfo.getReporterIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrapOutOkrWorkReportBaseInfo.getReporterIdentity())){
								wrapOutOkrWorkReportBaseInfo.setIsReporter(true);
							}
							if( "ADMIN_AND_ALLLEADER".equals( wrapOutOkrWorkReportBaseInfo.getReportWorkflowType() )){
								//从汇报审阅领导里进行比对
								if( wrapOutOkrWorkReportBaseInfo.getReadLeadersIdentity() != null && wrapOutOkrWorkReportBaseInfo.getReadLeadersIdentity().indexOf( okrUserCache.getLoginIdentityName()  ) >= 0 ){
									wrapOutOkrWorkReportBaseInfo.setIsReadLeader( true );
								}
							}else if( "DEPLOYER".equals( wrapOutOkrWorkReportBaseInfo.getReportWorkflowType() ) ){
								if( okrWorkBaseInfo != null ){
									//对比当前工作的部署者是否是当前用户
									if( okrWorkBaseInfo.getDeployerIdentity() != null && okrWorkBaseInfo.getDeployerIdentity().equalsIgnoreCase( okrUserCache.getLoginIdentityName()  ) ){
										wrapOutOkrWorkReportBaseInfo.setIsReadLeader( true );
									}
								}
							}
							if( workAdminIdentity != null && !workAdminIdentity.isEmpty() && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( workAdminIdentity )){
								wrapOutOkrWorkReportBaseInfo.setIsWorkAdmin( true );
							}
							//把汇报对象放进输出里
							addReportToCollectList( wrapOutOkrWorkReportBaseInfo, collectList );
							
						} catch (Exception e) {
							logger.error( "format okrWorkReportBaseInfo to wrap got an exception.", e );
						}
					}
				}
			}
		}
		result.setData( collectList );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	

	/**
	 * 将处理好的汇报对象放入输出的分类List里
	 * @param wrapOutOkrTaskReportEntity
	 * @param collectList
	 */
	private void addReportToCollectList( WrapOutOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo, List<WrapOutOkrTaskCollect> collectList) {
		
		List<WrapOutOkrTaskReportEntity> wrapOutOkrTaskReportEntityList = null;
		
		WrapOutOkrTaskCollectList wrapOutOkrTaskCollectList  = null;
		WrapOutOkrTaskReportEntity wrapOutOkrTaskReportEntity = null;
		WrapOutOkrTaskCollect collect = findCollectFormCollectList( wrapOutOkrWorkReportBaseInfo, collectList );
		
		boolean addAlready = false;
		
		if( collect != null ){
			
			if ( collect.getReportCollect() == null ) {
				collect.setReportCollect( new WrapOutOkrTaskCollectList() );
			}
			wrapOutOkrTaskCollectList = collect.getReportCollect();
			wrapOutOkrTaskCollectList.addOrganizationName( wrapOutOkrWorkReportBaseInfo.getReporterOrganizationName() );
			
			if( wrapOutOkrTaskCollectList.getReportInfos() == null ){
				wrapOutOkrTaskCollectList.setReportInfos( new ArrayList<WrapOutOkrTaskReportEntity>());
			}
			
			wrapOutOkrTaskReportEntityList = wrapOutOkrTaskCollectList.getReportInfos();
			for( WrapOutOkrTaskReportEntity _wrapOutOkrTaskReportEntity : wrapOutOkrTaskReportEntityList ){
				if( _wrapOutOkrTaskReportEntity.getName().equals( wrapOutOkrWorkReportBaseInfo.getReporterOrganizationName() )){
					_wrapOutOkrTaskReportEntity.addReports(wrapOutOkrWorkReportBaseInfo);
					collect.setCount( collect.getCount() + 1 );
					addAlready = true;
				}
			}
			
			if( !addAlready ){
				wrapOutOkrTaskReportEntity = new WrapOutOkrTaskReportEntity();
				wrapOutOkrTaskReportEntity.setName( wrapOutOkrWorkReportBaseInfo.getReporterOrganizationName() );
				wrapOutOkrTaskReportEntity.addReports( wrapOutOkrWorkReportBaseInfo );
				collect.setCount( collect.getCount() + 1 );
				wrapOutOkrTaskReportEntityList.add( wrapOutOkrTaskReportEntity );
			}
		}
	}
	
	

	private WrapOutOkrTaskCollect findCollectFormCollectList( WrapOutOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo, List<WrapOutOkrTaskCollect> collectList ) {
		if( collectList == null ){
			collectList = new ArrayList<WrapOutOkrTaskCollect>();
		}
		for( WrapOutOkrTaskCollect collect : collectList ){
			//先找到该汇报所在环节的集合对象
			if( collect.getActivityName().equalsIgnoreCase( wrapOutOkrWorkReportBaseInfo.getActivityName() )){
				//找到对象的集合对象，放进去
				return collect;
			}
		}
		//没找到，要根据环节名称创建一个新的集合，并且将汇报对象放入
		WrapOutOkrTaskCollect collect = new WrapOutOkrTaskCollect();
		collect.setActivityName( wrapOutOkrWorkReportBaseInfo.getActivityName() );
		collect.setCount( 0 );
		collectList.add( collect );
		
		return collect;
	}

	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,下一页.", response = WrapOutOkrTask.class, request = WrapInFilter.class)
	@PUT
	@Path( "filter/my/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyTaskNext(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrTask>> result = new ActionResult<>();
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			check = false;
			result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
		}
		if( check ){
			try{
				//只允许查询属于自己的登录身份的数据
				EqualsTerms equalsMap = new EqualsTerms();
				NotEqualsTerms notEqualsMap = new NotEqualsTerms();
				InTerms insMap = new InTerms();
				NotInTerms notInsMap = new NotInTerms();
				MemberTerms membersMap = new MemberTerms();
				NotMemberTerms notMembersMap = new NotMemberTerms();
				LikeTerms likesMap = new LikeTerms();
				
				equalsMap.put( "targetIdentity", okrUserCache.getLoginIdentityName()  );
							
				wrapIn.setId( id );
				wrapIn.setCount(count);
				
				Collection<String> dynamicObjectTypeNotIn = null;
				if( notInsMap.get( "dynamicObjectType" ) == null ){
					dynamicObjectTypeNotIn = new ArrayList<String>();
				}else{
					if( notInsMap.get( "dynamicObjectType" ) != null ){
						dynamicObjectTypeNotIn = ( Collection<String> )notInsMap.get( "dynamicObjectType" );
					}
				}
				dynamicObjectTypeNotIn.add( "工作汇报" );
				notInsMap.put( "dynamicObjectType", dynamicObjectTypeNotIn );
				
				wrapIn.setAndJoin( true );
				
				result = this.standardListNext( wrapout_copier, wrapIn.getId(), wrapIn.getCount(), wrapIn.getSequenceField(), 
						equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, wrapIn.isAndJoin(), wrapIn.getOrder() );
			}catch(Throwable th){
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "处理指定的待阅信息.", response = WrapOutOkrTask.class )
	@GET
	@Path( "process/read/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processRead(@Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		
		ActionResult< List<WrapOutOkrTask> > result = new ActionResult<>();
		OkrTask okrTask = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "传入的参数ID为空，无法进行待阅处理。" ) );
				result.setUserMessage( "传入的参数ID为空，无法进行待阅处理." );
			}
		}
		
		if(check){
			//判断待阅信息是否存在
			try{
				okrTask = okrTaskService.get(id);
			}catch(Exception e){
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID查询待阅信息时发生异常." );
			}
		}
		
		if(check){
			if( okrTask == null ){
				check = false;
				result.error( new Exception( "待阅信息不存在, id=" + id) );
				result.setUserMessage( "待阅信息不存在, id=" + id );
			}
		}
		
		if(check){
			//判断是否为待阅信息
			if( !"READ".equals( okrTask.getProcessType() )){
				check = false;
				result.error( new Exception( "您尝试处理的信息id=" + id + "，并不是待阅信息，无法进一步处理。" ) );
				result.setUserMessage( "您尝试处理的信息id=" + id + "，并不是待阅信息，无法进一步处理。" );
			}
		}
		
		if(check){
			if( !okrUserCache.getLoginIdentityName() .equals( okrTask.getTargetIdentity() )){
				check = false;
				result.error( new Exception( "您没有处理该条待阅的权限，请联系管理员。" ) );
				result.setUserMessage( "对不起，您没有处理该条待阅的权限，请联系管理员。" );
			}
		}
		
		if(check){
			try{
				//处理待阅信息
				okrTaskService.processRead( okrTask );
				try {
					okrWorkDynamicsService.processReadDynamic(
							okrTask, 
							"阅知工作汇报", 
							currentPerson.getName(),
							okrUserCache.getLoginIdentityName() ,
							okrUserCache.getLoginIdentityName() , 
							"阅知了工作汇报：" + okrTask.getTitle(),
							"工作汇报阅知成功！");
				} catch (Exception e) {
					logger.error("system save reportDynamic got an exception.", e);
				}
				result.setUserMessage( "工作汇报阅知完成！" );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在处理待阅信息时发生异常" );
				logger.error( "system process read got an exception.", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrTask数据对象.", response = WrapOutOkrTask.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrTask> result = new ActionResult<>();
		OkrTask okrTask = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			result.setUserMessage( "删除待办数据过程中发生异常。" );
			result.error( new Exception( "需要删除的待办ID为空，无法进行数据删除！" ) );
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			try {
				okrTask = okrTaskService.get( id );
				if( okrTask == null ){
					check = false;
					result.setUserMessage( "需要删除的待办数据不存在。" );
					result.error( new Exception( "需要删除的待办数据不存在，无法进行数据删除！" ) );
					logger.error( "system can not get any object by {'id':'"+id+"'}. " );
				}
			} catch ( Exception e) {
				check = false;
				result.setUserMessage( "系统在根据ID查询待办数据信息时发生异常。" );
				result.error( e );
				logger.error( "system get by id got an exception", e );
			}
		}
		if( check ){
			try{
				okrTaskService.delete( id );
				result.setUserMessage( "成功删除待办数据信息。id=" + id );
			}catch(Exception e){
				logger.error( "system delete okrTaskService get an exception, {'id':'"+id+"'}", e );
				result.setUserMessage( "删除待办数据过程中发生异常。" );
				result.error( e );
			}
		}
		if( check ){
			if( "工作汇报".equals( okrTask.getDynamicObjectType() )){
				try{
					List<String> workTypeList = new ArrayList<String>();
					workTypeList.add( okrTask.getWorkType() );
					okrWorkReportTaskCollectService.checkReportCollectTask( okrTask.getTargetIdentity(), workTypeList );
				}catch( Exception e ){
					logger.error( "待办信息删除成功，但对汇报者进行汇报待办汇总发生异常。", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrTask对象.", response = WrapOutOkrTask.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrTask> result = new ActionResult<>();
		WrapOutOkrTask wrap = null;
		OkrTask okrTask = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrTask = okrTaskService.get( id );
			if( okrTask != null ){
				wrap = wrapout_copier.copy( okrTask );
				result.setData(wrap);
			}else{
				logger.error( "system can not get any object by {'id':'"+id+"'}. " );
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
//	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,下一页.", response = WrapOutOkrTask.class, request = WrapInAdminFilter.class)
//	@PUT
//	@Path( "filter/list/{id}/next/{count}" )
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn) {
//		ActionResult<List<WrapOutOkrTask>> result = new ActionResult<>();
//		EffectivePerson currentPerson = this.effectivePerson(request);
//		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+okrUserCache.getLoginIdentityName() +"'] try to list OkrTask for nextpage, last id=" + id );
//		try{
//			result = this.standardListNext( wrapout_copier, wrapIn.getId(), wrapIn.getCount(), wrapIn.getSequenceField(), 
//					wrapIn.getEquals(), wrapIn.getNotEquals(), wrapIn.getLikes(), wrapIn.getIns(), wrapIn.getNotIns(), 
//					wrapIn.getMembers(), wrapIn.getNotMembers(), wrapIn.isAndJoin(), wrapIn.getOrder() );
//		}catch(Throwable th){
//			logger.error( "system filter OkrTask got an exception." );
//			th.printStackTrace();
//			result.error(th);
//		}
//		return ResponseFactory.getDefaultActionResultResponse(result);
//	}
//
//	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,上一页.", response = WrapOutOkrTask.class, request = WrapInAdminFilter.class)
//	@PUT
//	@Path( "filter/list/{id}/prev/{count}" )
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn) {
//		ActionResult<List<WrapOutOkrTask>> result = new ActionResult<>();
//		try {
//			EffectivePerson currentPerson = this.effectivePerson(request);
//			//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+okrUserCache.getLoginIdentityName() +"'] try to list OkrTask for nextpage, last id=" + id );
//			result = this.standardListPrev( wrapout_copier, wrapIn.getId(), wrapIn.getCount(), wrapIn.getSequenceField(), 
//					wrapIn.getEquals(), wrapIn.getNotEquals(), wrapIn.getLikes(), wrapIn.getIns(), wrapIn.getNotIns(), 
//					wrapIn.getMembers(), wrapIn.getNotMembers(), wrapIn.isAndJoin(), wrapIn.getOrder() );
//		} catch (Throwable th) {
//			logger.error( "system filter OkrTask got an exception." );
//			th.printStackTrace();
//			result.error(th);
//		}
//		return ResponseFactory.getDefaultActionResultResponse(result);
//	}
}
