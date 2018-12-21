package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

/**
 * 类   名：OkrWorkReportBaseInfoService<br/>
 * 实体类：OkrWorkReportBaseInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportOperationService{
	private static  Logger logger = LoggerFactory.getLogger( OkrWorkReportOperationService.class );
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
	private OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	private OkrWorkReportFlowService okrWorkReportFlowService = new OkrWorkReportFlowService();
	private OkrSendNotifyService okrNotifyService = new OkrSendNotifyService();
	private DateOperation dateOperation = new DateOperation();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	
	/**
	 * 向数据库保存OkrWorkReportBaseInfo对象
	 * @param wrapIn
	 * @param workPointAndRequirements
	 * @param progressDescription 填写汇报时填写的具体进展描述信息
	 * @param workPlan 下一步工作计划信息
	 * @param adminSuperviseInfo 管理员督办信息
	 * @param memo 说明备注信息
	 * @param opinion
	 * @return
	 * @throws Exception
	 */
	public OkrWorkReportBaseInfo save( OkrWorkReportBaseInfo wrapIn, String workPointAndRequirements,
			String progressDescription, String workPlan, String adminSuperviseInfo, String memo, String opinion ) throws Exception {	
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		List<String> ids = null;
		Business business = null;
		
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
			//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				okrWorkReportBaseInfo =  emc.find( wrapIn.getId(), OkrWorkReportBaseInfo.class );
				okrWorkReportDetailInfo =  emc.find( wrapIn.getId(), OkrWorkReportDetailInfo.class );
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				emc.beginTransaction( OkrWorkReportDetailInfo.class );
				if( okrWorkReportBaseInfo != null ){//如果信息已经存在，那么不需要更新基础信息
					List<String> attachments = okrWorkReportBaseInfo.getAttachmentList();
					wrapIn.copyTo( okrWorkReportBaseInfo, JpaObject.FieldsUnmodify );
					okrWorkReportBaseInfo.setAttachmentList( attachments );
					emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
				}else{//信息不存在，创建一个新的记录
					okrWorkReportBaseInfo = new OkrWorkReportBaseInfo();
					wrapIn.copyTo( okrWorkReportBaseInfo );
					okrWorkReportBaseInfo.setId( wrapIn.getId());
					emc.persist( okrWorkReportBaseInfo, CheckPersistType.all);
				}
				
				if( okrWorkReportDetailInfo != null ){
					okrWorkReportDetailInfo.setId( okrWorkReportBaseInfo.getId() );
					okrWorkReportDetailInfo.setCenterId(okrWorkReportBaseInfo.getCenterId());
					okrWorkReportDetailInfo.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
					okrWorkReportDetailInfo.setTitle( okrWorkReportBaseInfo.getTitle() );
					okrWorkReportDetailInfo.setWorkId( okrWorkReportBaseInfo.getWorkId() );
					okrWorkReportDetailInfo.setStatus( "正常" );
					okrWorkReportDetailInfo.setWorkPlan( workPlan );
					okrWorkReportDetailInfo.setProgressDescription( progressDescription );
					okrWorkReportDetailInfo.setWorkPointAndRequirements( workPointAndRequirements );
					okrWorkReportDetailInfo.setMemo( memo );
					emc.check( okrWorkReportDetailInfo, CheckPersistType.all );
				}else{
					okrWorkReportDetailInfo = new OkrWorkReportDetailInfo();
					okrWorkReportDetailInfo.setId( okrWorkReportBaseInfo.getId() );//使用参数传入的ID作为记录的ID
					okrWorkReportDetailInfo.setCenterId(okrWorkReportBaseInfo.getCenterId());
					okrWorkReportDetailInfo.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
					okrWorkReportDetailInfo.setTitle( okrWorkReportBaseInfo.getTitle() );
					okrWorkReportDetailInfo.setWorkId( okrWorkReportBaseInfo.getWorkId() );
					okrWorkReportDetailInfo.setStatus( "正常" );
					okrWorkReportDetailInfo.setWorkPlan( workPlan );
					okrWorkReportDetailInfo.setProgressDescription( progressDescription );
					okrWorkReportDetailInfo.setWorkPointAndRequirements( workPointAndRequirements );
					okrWorkReportDetailInfo.setMemo( memo );
					emc.persist( okrWorkReportDetailInfo, CheckPersistType.all);	
				}
				
				//判断该汇报创建者的待办是否存在，如果不存在就创建一个新的待办
				ids = business.okrTaskFactory().listIdsByTargetActivityAndObjId( "TASK", "工作汇报", okrWorkReportBaseInfo.getId(), "拟稿", okrWorkReportBaseInfo.getReporterIdentity() );
				if( ids == null || ids.isEmpty() ){
					//创建工作汇报的待办
					OkrTask okrTask = new OkrTask();
					okrTask.setTitle( okrWorkReportBaseInfo.getTitle() );
					okrTask.setCenterId( okrWorkReportBaseInfo.getCenterId() );
					okrTask.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
					okrTask.setWorkId( okrWorkReportBaseInfo.getWorkId() );
					okrTask.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle() );
					okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
					okrTask.setTargetIdentity( okrWorkReportBaseInfo.getReporterIdentity() );
					okrTask.setTargetName( okrWorkReportBaseInfo.getReporterName() );
					okrTask.setTargetUnitName( okrWorkReportBaseInfo.getReporterUnitName() );
					okrTask.setTargetTopUnitName( okrWorkReportBaseInfo.getReporterTopUnitName() );			
					okrTask.setActivityName( "拟稿" );
					okrTask.setArriveDateTime( new Date() );
					okrTask.setArriveDateTimeStr( dateOperation.getNowDateTime() );						
					okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId() );
					okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle() );
					okrTask.setDynamicObjectType( "工作汇报" );//工作汇报拟稿
					okrTask.setProcessType( "TASK" );
					okrTask.setStatus( "正常" );	
					okrTask.setViewUrl( "" );
					emc.persist( okrTask, CheckPersistType.all );
				}
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrWorkReportBaseInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				emc.beginTransaction( OkrWorkReportDetailInfo.class );
				okrWorkReportBaseInfo = new OkrWorkReportBaseInfo();
				wrapIn.copyTo( okrWorkReportBaseInfo );
				
				okrWorkReportDetailInfo = new OkrWorkReportDetailInfo();
				okrWorkReportDetailInfo.setId( okrWorkReportBaseInfo.getId() );//使用参数传入的ID作为记录的ID
				okrWorkReportDetailInfo.setCenterId(okrWorkReportBaseInfo.getCenterId());
				okrWorkReportDetailInfo.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
				okrWorkReportDetailInfo.setTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportDetailInfo.setWorkId( okrWorkReportBaseInfo.getWorkId() );
				okrWorkReportDetailInfo.setStatus( "正常" );
				okrWorkReportDetailInfo.setWorkPlan( workPlan );
				okrWorkReportDetailInfo.setProgressDescription( progressDescription );
				okrWorkReportDetailInfo.setWorkPointAndRequirements( workPointAndRequirements );
				okrWorkReportDetailInfo.setMemo( memo );
				emc.persist( okrWorkReportDetailInfo, CheckPersistType.all);
				emc.persist( okrWorkReportBaseInfo, CheckPersistType.all);	
				//判断该汇报创建者的待办是否存在，如果不存在就创建一个新的待办
				//工作汇报拟稿
				ids = business.okrTaskFactory().listIdsByTargetActivityAndObjId( "TASK", "工作汇报", okrWorkReportBaseInfo.getId(), "拟稿", okrWorkReportBaseInfo.getReporterIdentity() );
				if( ids == null || ids.isEmpty() ){
					//创建工作汇报的待办
					OkrTask okrTask = new OkrTask();
					okrTask.setTitle( okrWorkReportBaseInfo.getTitle() );
					okrTask.setCenterId( okrWorkReportBaseInfo.getCenterId() );
					okrTask.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
					okrTask.setWorkId( okrWorkReportBaseInfo.getWorkId() );
					okrTask.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle() );
					okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
					okrTask.setTargetIdentity( okrWorkReportBaseInfo.getReporterIdentity() );
					okrTask.setTargetName( okrWorkReportBaseInfo.getReporterName() );
					okrTask.setTargetUnitName( okrWorkReportBaseInfo.getReporterUnitName() );
					okrTask.setTargetTopUnitName( okrWorkReportBaseInfo.getReporterTopUnitName() );			
					okrTask.setActivityName( "拟稿" );
					okrTask.setArriveDateTime( new Date() );
					okrTask.setArriveDateTimeStr( dateOperation.getNowDateTime() );						
					okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId() );
					okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle() );
					okrTask.setDynamicObjectType( "工作汇报" );//工作汇报拟稿
					okrTask.setProcessType( "TASK" );
					okrTask.setStatus( "正常" );	
					okrTask.setViewUrl( "" );
					emc.persist( okrTask, CheckPersistType.all );
				}
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrWorkReportBaseInfo create got a error!" );
				throw e;
			}
		}
		
		try{
			List<String> workTypeList = new ArrayList<String>();
			workTypeList.add( wrapIn.getWorkType() );
			okrWorkReportTaskCollectService.checkReportCollectTask( okrWorkReportBaseInfo.getReporterIdentity(), workTypeList );
		}catch( Exception e ){
			logger.warn( "汇报信息保存成功，但对汇报者进行汇报待办汇总发生异常。" );
			throw e;
		}
		return okrWorkReportBaseInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkReportBaseInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id, String operator ) throws Exception {
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		OkrTask okrTask = null;
		OkrTaskHandled okrTaskHandled = null;
		List<String> ids = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction( OkrWorkReportBaseInfo.class );
			emc.beginTransaction( OkrWorkReportDetailInfo.class );
			emc.beginTransaction( OkrWorkReportPersonLink.class );
			emc.beginTransaction( OkrWorkReportProcessLog.class );
			emc.beginTransaction( OkrTask.class );
			emc.beginTransaction( OkrTaskHandled.class );
			okrWorkReportBaseInfo = emc.find( id, OkrWorkReportBaseInfo.class );
			if ( null != okrWorkReportBaseInfo ) {
				emc.remove( okrWorkReportBaseInfo,CheckRemoveType.all );
			}
			//删除所有的汇报详情
			okrWorkReportDetailInfo = emc.find( id, OkrWorkReportDetailInfo.class );
			if ( null != okrWorkReportDetailInfo ) {
				emc.remove( okrWorkReportDetailInfo,CheckRemoveType.all );
			}
			ids = business.okrWorkReportPersonLinkFactory().listIdsByReportId( id );
			if( ids != null && !ids.isEmpty() ){
				for( String _id : ids ){
					okrWorkReportPersonLink = emc.find( _id, OkrWorkReportPersonLink.class );
					if ( null != okrWorkReportPersonLink ) {
						emc.remove( okrWorkReportPersonLink,CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkReportProcessLogFactory().listIdsByReportId( id );
			if( ids != null && !ids.isEmpty() ){
				for( String _id : ids ){
					okrWorkReportProcessLog = emc.find( _id, OkrWorkReportProcessLog.class );
					if ( null != okrWorkReportProcessLog ) {
						emc.remove( okrWorkReportProcessLog,CheckRemoveType.all );
					}
				}
			}
			ids = business.okrTaskFactory().listIdsByReportId( id );
			if( ids != null && !ids.isEmpty() ){
				for( String _id : ids ){
					okrTask = emc.find( _id, OkrTask.class );
					if ( null != okrTask ) {
						emc.remove( okrTask,CheckRemoveType.all );
					}
				}
			}
			ids = business.okrTaskHandledFactory().listIdsByReportId( id );
			if( ids != null && !ids.isEmpty() ){
				for( String _id : ids ){
					okrTaskHandled = emc.find( _id, OkrTaskHandled.class );
					if ( null != okrTaskHandled ) {
						emc.remove( okrTaskHandled,CheckRemoveType.all );
					}
				}
			}
			okrNotifyService.notifyReportDeleteSuccess( okrWorkReportBaseInfo, operator );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据工作ID，查询该工作的最大汇报次序
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Integer getMaxReportCount( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().getMaxReportCount( workId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据工作ID，删除工作汇报信息
	 * @param workId
	 * @throws Exception
	 */
	public void deleteByWorkId( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null, system can not delete any object." );
		}
		List<String> ids = null;
		Business business = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo  = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		OkrWorkReportPersonLink okrWorkReportPersonLink  = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			
			emc.beginTransaction( OkrWorkReportBaseInfo.class );
			emc.beginTransaction( OkrWorkReportDetailInfo.class );
			emc.beginTransaction( OkrWorkReportPersonLink.class );
			emc.beginTransaction( OkrWorkReportProcessLog.class );
			
			ids = business.okrWorkReportBaseInfoFactory().listByWorkId( workId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrWorkReportBaseInfo = business.okrWorkReportBaseInfoFactory().get(id);
					okrWorkReportBaseInfo.setStatus( "已删除" );
					emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
				}
			}
			
			ids = business.okrWorkReportDetailInfoFactory().listByWorkId( workId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrWorkReportDetailInfo = business.okrWorkReportDetailInfoFactory().get(id);
					okrWorkReportDetailInfo.setStatus( "已删除" );
					emc.check( okrWorkReportDetailInfo, CheckPersistType.all );
				}
			}
			
			ids = business.okrWorkReportPersonLinkFactory().listByWorkId( workId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrWorkReportPersonLink = business.okrWorkReportPersonLinkFactory().get(id);
					okrWorkReportPersonLink.setStatus( "已删除" );
					emc.check( okrWorkReportPersonLink, CheckPersistType.all );
				}
			}
			
			ids = business.okrWorkReportProcessLogFactory().listByWorkId( workId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrWorkReportProcessLog = business.okrWorkReportProcessLogFactory().get(id);
					okrWorkReportProcessLog.setStatus( "已删除" );
					emc.check( okrWorkReportProcessLog, CheckPersistType.all );
				}
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 为具体工作生成工作汇报草稿信息，并且推送待办
	 * @param string
	 * @param wrapInOkrWorkReportBaseInfo
	 * @param okrWorkBaseInfo
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo createReportDraft( OkrWorkBaseInfo okrWorkBaseInfo, String report_auto_over ) throws Exception {
		if( okrWorkBaseInfo == null ){
			throw new Exception( "okrWorkBaseInfo is null, can not create report." );
		}
		Integer maxReportCount = 0;
		Date nextReportTime = null;
		List<String> ids = null;
		//需要对工作汇报的基础信息进行补全，对审核方式进行校验
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = new OkrWorkReportBaseInfo();
		//查询最大的汇报次数来决定本次汇报的次数
		try {
			maxReportCount = getMaxReportCount( okrWorkBaseInfo.getId() );
			if( maxReportCount == null ){
				maxReportCount = 0;
			}
			okrWorkReportBaseInfo.setReportCount( maxReportCount + 1 );
		} catch (Exception e) {
			throw e;
		}
		
		//根据汇报次数确定汇报的标题
		okrWorkReportBaseInfo.setTitle(  okrWorkBaseInfo.getTitle() );
		okrWorkReportBaseInfo.setShortTitle( "第" + okrWorkReportBaseInfo.getReportCount() + "次工作汇报" );
		
		okrWorkReportBaseInfo.setWorkType( okrWorkBaseInfo.getWorkType() );
		okrWorkReportBaseInfo.setWorkId( okrWorkBaseInfo.getId() );
		okrWorkReportBaseInfo.setWorkTitle( okrWorkBaseInfo.getTitle() );
		okrWorkReportBaseInfo.setCenterId( okrWorkBaseInfo.getCenterId() );
		okrWorkReportBaseInfo.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
		
		okrWorkReportBaseInfo.setProcessStatus( "草稿" );
		okrWorkReportBaseInfo.setStatus( "正常" );
		okrWorkReportBaseInfo.setProcessType( "审批" );
		okrWorkReportBaseInfo.setProgressPercent(0);
		okrWorkReportBaseInfo.setIsWorkCompleted(false);
		
		//创建者是系统创建
		okrWorkReportBaseInfo.setCreatorTopUnitName( "SYSTEM" );
		okrWorkReportBaseInfo.setCreatorIdentity( "SYSTEM" );
		okrWorkReportBaseInfo.setCreatorName( "SYSTEM" );
		okrWorkReportBaseInfo.setCreatorUnitName( "SYSTEM" );
		
		//汇报是工作的责任者
		okrWorkReportBaseInfo.setReporterTopUnitName( okrWorkBaseInfo.getResponsibilityTopUnitName() );
		okrWorkReportBaseInfo.setReporterIdentity( okrWorkBaseInfo.getResponsibilityIdentity() );
		okrWorkReportBaseInfo.setReporterName( okrWorkBaseInfo.getResponsibilityEmployeeName() );
		okrWorkReportBaseInfo.setReporterUnitName( okrWorkBaseInfo.getResponsibilityUnitName() );
		
		//当前处理者是工作的责任者，汇报还是草稿状态
		List<String> names = new ArrayList<>();
		List<String> identities = new ArrayList<>();
		List<String> unitNames = new ArrayList<>();
		List<String> topUnitNames = new ArrayList<>();
		names.add( okrWorkBaseInfo.getResponsibilityEmployeeName() );
		identities.add( okrWorkBaseInfo.getResponsibilityIdentity() );
		unitNames.add( okrWorkBaseInfo.getResponsibilityUnitName() );
		topUnitNames.add( okrWorkBaseInfo.getResponsibilityTopUnitName() );	
		
		okrWorkReportBaseInfo.setCurrentProcessorTopUnitNameList( topUnitNames );
		okrWorkReportBaseInfo.setCurrentProcessorIdentityList( identities );
		okrWorkReportBaseInfo.setCurrentProcessorNameList( names );
		okrWorkReportBaseInfo.setCurrentProcessorUnitNameList( unitNames );
		//判断汇报的审批方式可以放在提交的时候进行
		okrWorkReportBaseInfo.setNeedAdminAudit(false);
		okrWorkReportBaseInfo.setWorkAdminIdentity( "" ); //工作督办员身份
		okrWorkReportBaseInfo.setWorkAdminName( "" ); //工作督办员姓名
		
		//创建工作汇报的待办
		OkrTask okrTask = new OkrTask();
		okrTask.setTitle( okrWorkReportBaseInfo.getTitle() );
		okrTask.setCenterId( okrWorkBaseInfo.getCenterId() );
		okrTask.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
		okrTask.setWorkId( okrWorkBaseInfo.getId() );
		okrTask.setWorkTitle( okrWorkBaseInfo.getTitle() );
		okrTask.setWorkType( okrWorkBaseInfo.getWorkType() );
		okrTask.setTargetIdentity( okrWorkReportBaseInfo.getReporterIdentity() );
		okrTask.setTargetName( okrWorkReportBaseInfo.getReporterName() );
		okrTask.setTargetUnitName( okrWorkReportBaseInfo.getReporterUnitName() );
		okrTask.setTargetTopUnitName( okrWorkReportBaseInfo.getReporterTopUnitName() );			
		okrTask.setActivityName( "拟稿" );
		okrTask.setArriveDateTime( new Date() );
		okrTask.setArriveDateTimeStr( dateOperation.getNowDateTime() );						
		okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId() );
		okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle() );
		okrTask.setDynamicObjectType( "工作汇报" );//工作汇报拟稿
		okrTask.setProcessType( "TASK" );
		okrTask.setStatus( "正常" );	
		okrTask.setViewUrl( "" );
		
		//从计算好的汇报时间序列里找出下一次汇报时间，如果找不到，那么留为null
		//定时代理WorkProgressConfirm会对下一次汇报时间为空的，未完成的工作进行工作汇报时间计算，自动延时
		nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime( okrWorkBaseInfo.getReportTimeQue(), okrTask.getArriveDateTime() );
		logger.info( "work id：" + okrWorkBaseInfo.getId() + "lastreporttime: "+ okrWorkBaseInfo.getLastReportTime() +" nextreporttime1:" + nextReportTime );
		if( nextReportTime == null ){
			nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime( okrWorkBaseInfo );
		}
		logger.info( "work id：" + okrWorkBaseInfo.getId() + "lastreporttime: "+ okrWorkBaseInfo.getLastReportTime() +" nextreporttime2:" + nextReportTime );
		if( report_auto_over != null && "OPEN".equals( report_auto_over )){
			//根据配置查询该工作所有正在流转中的工作汇报ID列表,包括草稿
			try {
				ids = okrWorkReportQueryService.listProcessingReportIdsByWorkId( okrWorkBaseInfo.getId() );
				if ( ids != null && !ids.isEmpty() ) {
					for (String id : ids) {
						logger.info( "system try to dispatch exists report to over，id：" + id );
						okrWorkReportFlowService.dispatchToOver( id );
					}
				}
			} catch (Exception e) {
				logger.warn( "system dispatch processing report to over got an exception." );
				logger.error(e);
			}
		}
		
		//保存数据到数据库中
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrWorkBaseInfo = emc.find( okrWorkBaseInfo.getId(), OkrWorkBaseInfo.class );
			if( okrWorkBaseInfo != null ){
				emc.beginTransaction(OkrWorkReportBaseInfo.class);
				emc.beginTransaction(OkrWorkBaseInfo.class);
				emc.beginTransaction(OkrTask.class);				
				//还要修改工作的下一次汇报信息
				okrWorkBaseInfo.setNextReportTime( nextReportTime );
				okrWorkBaseInfo.setLastReportTime( okrTask.getArriveDateTime() );
				okrWorkBaseInfo.setReportCount( okrWorkReportBaseInfo.getReportCount() );
				// 保存汇报基础信息
				emc.persist( okrWorkReportBaseInfo, CheckPersistType.all );
				// 保存汇报待办
				emc.persist( okrTask, CheckPersistType.all );
				emc.check( okrWorkBaseInfo, CheckPersistType.all );
				emc.commit();
				logger.info( "system try to save new report draft.id：" + okrWorkReportBaseInfo.getId() );
				logger.info( "system try to update work report time and count.id：" + okrWorkBaseInfo.getId() );
			}
		} catch (Exception e) {
			throw e;
		}
		
		if( okrTask != null ){
			List<String> workTypeList = new ArrayList<String>();
			workTypeList.add( okrTask.getWorkType() );
			okrWorkReportTaskCollectService.checkReportCollectTask( okrTask.getTargetIdentity(), workTypeList );
		}
		
		return okrWorkReportBaseInfo;
	}

	public void saveAdminSuperviseInfo( String reportId, String adminSuperviseInfo ) throws Exception {
		if( reportId == null ){
			throw new Exception( "reportId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkReportDetailInfo okrWorkReportDetailInfo = emc.find( reportId, OkrWorkReportDetailInfo.class );
			if( okrWorkReportDetailInfo != null ){
				emc.beginTransaction( OkrWorkReportDetailInfo.class );
				okrWorkReportDetailInfo.setAdminSuperviseInfo( adminSuperviseInfo );
				emc.check( okrWorkReportDetailInfo, CheckPersistType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 保存汇报审阅信息
	 * @param wrapIn
	 * @throws Exception 
	 */
	public void saveLeaderOpinionInfo( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String opinion, String processorIdentity ) throws Exception {
		Business business = null;
		List<String> ids = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		if( okrWorkReportBaseInfo == null ){
			throw new Exception( "okrWorkReportBaseInfo is null!" );
		}
		if( opinion == null ){
			throw new Exception( "opinion is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.okrWorkReportProcessLogFactory().listByReportIdAndProcessor( okrWorkReportBaseInfo.getId(), okrWorkReportBaseInfo.getActivityName(), processorIdentity, "草稿" );			
			
			emc.beginTransaction( OkrWorkReportDetailInfo.class );
			emc.beginTransaction( OkrWorkReportProcessLog.class );
			
			if( ids != null && !ids.isEmpty() ){
				//有数据需要更新
				for( String id : ids ){
					okrWorkReportProcessLog = emc.find( id, OkrWorkReportProcessLog.class );
					if( okrWorkReportProcessLog != null ){
						okrWorkReportProcessLog.setOpinion( opinion );
						emc.check( okrWorkReportProcessLog, CheckPersistType.all );
					}
				}
			}else{
				//没有数据，需要创建新的日志
				String personName  = okrUserManagerService.getPersonNameByIdentity( processorIdentity );
				if( personName != null ){
					okrWorkReportProcessLog = new OkrWorkReportProcessLog(); 
					okrWorkReportProcessLog.setArriveTime( new Date() );
					okrWorkReportProcessLog.setArriveTimeStr( dateOperation.getNowDateTime());
					okrWorkReportProcessLog.setCenterId( okrWorkReportBaseInfo.getCenterId() );
					okrWorkReportProcessLog.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
					okrWorkReportProcessLog.setDecision( "保存" );
					okrWorkReportProcessLog.setOpinion( opinion );
					okrWorkReportProcessLog.setActivityName( "领导批示" );
					okrWorkReportProcessLog.setProcessStatus( "草稿" );
					okrWorkReportProcessLog.setProcessLevel( okrWorkReportBaseInfo.getCurrentProcessLevel() );
					okrWorkReportProcessLog.setProcessTime( new Date() );
					okrWorkReportProcessLog.setProcessTimeStr(  dateOperation.getNowDateTime() );
					okrWorkReportProcessLog.setReportTitle( okrWorkReportBaseInfo.getTitle() );
					okrWorkReportProcessLog.setStayTime( 0L );
					okrWorkReportProcessLog.setTitle( okrWorkReportBaseInfo.getTitle() );
					okrWorkReportProcessLog.setWorkId( okrWorkReportBaseInfo.getWorkId() );
					okrWorkReportProcessLog.setWorkReportId( okrWorkReportBaseInfo.getId() );
					okrWorkReportProcessLog.setProcessorName( personName );
					okrWorkReportProcessLog.setProcessorUnitName( okrUserManagerService.getUnitNameByIdentity( processorIdentity ) );
					okrWorkReportProcessLog.setProcessorIdentity( processorIdentity );
					okrWorkReportProcessLog.setProcessorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( processorIdentity ) );
					
					emc.persist( okrWorkReportProcessLog, CheckPersistType.all );
				}
			}
			
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public OkrAttachmentFileInfo saveAttachment(String reportId, OkrAttachmentFileInfo attachment) throws Exception {
		if( reportId == null ){
			throw new Exception( "reportId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkReportBaseInfo report = emc.find( reportId, OkrWorkReportBaseInfo.class );
			if( report != null ){
				emc.beginTransaction( OkrAttachmentFileInfo.class );
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				emc.persist(attachment, CheckPersistType.all );
				if( report.getAttachmentList()== null  ) {
					report.setAttachmentList( new ArrayList<>());
				}
				if( !report.getAttachmentList().contains( attachment.getId() )) {
					report.getAttachmentList().add( attachment.getId() );
					emc.check( report, CheckPersistType.all );
				}
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return attachment;
	}
}
