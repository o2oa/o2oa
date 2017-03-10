package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapOutOkrWorkReportBaseInfo;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;

public class ExcuteListTaskCollect extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListTaskCollect.class );
	
	protected ActionResult<List<WrapOutOkrTaskCollect>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WrapOutOkrTaskCollect>> result = new ActionResult<>();
		List<WrapOutOkrTaskCollect> collectList = new ArrayList<WrapOutOkrTaskCollect>();	
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
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			userIdentity = okrUserCache.getLoginIdentityName() ;
		}
		
		if( check ){
			if( id == null ){
				check = false;
				Exception exception = new TaskIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}else{
				//查询该汇总待办信息
				try {
					okrTask = okrTaskService.get( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryByIdException( e, id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			if( okrTask != null ){
				taskTypeList.add( "工作汇报" );
				try {
					//获取所有符合条件的待办
					taskList = okrTaskService.listTaskByTaskType( taskTypeList, userIdentity, okrTask.getWorkType() );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskListByTaskTypeException( e, userIdentity );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}else{
				check = false;
				result.setCount( 0L );
			}
		}
		
		if( check ){
			try {
				workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigQueryByCodeException( e, "REPORT_SUPERVISOR" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( taskList != null && !taskList.isEmpty() ){
				for( OkrTask task : taskList ){
					try {
						okrWorkBaseInfo = okrWorkBaseInfoService.get( task.getWorkId() );
						if( okrWorkBaseInfo == null ){
							check = false;
							Exception exception = new WorkNotExistsException( task.getWorkId() );
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new WorkQueryByIdException( e, task.getWorkId() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
					try {
						okrWorkReportBaseInfo = okrWorkReportQueryService.get( task.getDynamicObjectId() );
					} catch (Exception e) {
						logger.warn( "get okrWorkReportBaseInfo by id got an exception." );
						logger.error(e);
					}
					try {
						okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( task.getDynamicObjectId() );
					} catch (Exception e) {
						logger.warn( "get okrWorkReportBaseInfo by id got an exception." );
						logger.error(e);
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
							logger.warn( "format okrWorkReportBaseInfo to wrap got an exception." );
							logger.error(e);
						}
					}
				}
			}
		}
		result.setData( collectList );
		return result;
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
	
}