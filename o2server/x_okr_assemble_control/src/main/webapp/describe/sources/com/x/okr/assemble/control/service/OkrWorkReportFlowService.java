package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
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
public class OkrWorkReportFlowService{
	private static  Logger logger = LoggerFactory.getLogger( OkrWorkReportFlowService.class );
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	private DateOperation dateOperation = new DateOperation();
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	private OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();

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
	 * 领导审批工作汇报
	 * @param name
	 * @param wrapIn
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo adminProcess( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String adminSuperviseInfo, String userIdentity ) throws Exception {
		if( okrWorkReportBaseInfo == null ){
			throw new Exception( "okrWorkReportBaseInfo is null." );
		}		
		if( adminSuperviseInfo == null || adminSuperviseInfo.isEmpty() ){
			throw new Exception( "adminSuperviseInfo id is null, can not process report info." );
		}		
		if( userIdentity == null ){
			throw new Exception( "userIdentity id is null, can not process report info." );
		}
		Business business = null;
		Date taskArriveDate = null;
		String taskArriveDateString = null;
		Boolean isWorkCompleted = false;
		Integer progressPercent = 0;
		Integer processLevel = 0;
		Integer maxProcessLevel = 0;
		OkrTask okrTask = null;
		OkrTask _okrTask = null;
		OkrTaskHandled okrTaskHandled = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		OkrWorkReportPersonLink _okrWorkReportPersonLink = null;
		List<String> ids = null;
		List<String> taskIds = null;
		List<OkrTask> taskList = new ArrayList<>();
		List<OkrTaskHandled> taskHandledList = new ArrayList<>();
		List<OkrWorkReportPersonLink> next_okrWorkReportPersonLinkList = null;
		
		String  processorName = okrUserManagerService.getPersonNameByIdentity(userIdentity);
		
		isWorkCompleted = okrWorkReportBaseInfo.getIsWorkCompleted();
		progressPercent = okrWorkReportBaseInfo.getProgressPercent();		
		if( isWorkCompleted ) {
			progressPercent = 100;
		}
		if( progressPercent == 100 ) {
			isWorkCompleted = true;
		}
		okrWorkReportBaseInfo.setIsWorkCompleted( isWorkCompleted );
		okrWorkReportBaseInfo.setProgressPercent( progressPercent );
		if( processorName != null ){			
			//二、处理本层级的处理人处理信息以及待办信息删除，新增已办信息
			//获取汇报信息当前的处理级别
			processLevel = okrWorkReportBaseInfo.getCurrentProcessLevel();
			//logger.debug( "okrWorkReportBaseInfo.getCurrentProcessLevel() : " + okrWorkReportBaseInfo.getCurrentProcessLevel());
			//查询本处理层级的处理人信息，更新处理状态为已处理
			ids = okrWorkReportPersonLinkService.getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, userIdentity, "处理中", "正常" );
			//logger.debug( "ids.size() : " + ids.size() );
			//查询该次处理的待办信息
			taskIds = okrTaskService.listIdsByTargetActivityAndObjId( "工作汇报", okrWorkReportBaseInfo.getId(), okrWorkReportBaseInfo.getActivityName(), userIdentity );
		
			//获取该汇报的最大审批等级
			maxProcessLevel = okrWorkReportPersonLinkService.getMaxProcessLevel( okrWorkReportBaseInfo.getId() );
			if( maxProcessLevel == null  || maxProcessLevel <= 0 ){
				throw new Exception( "okrWorkReportBaseInfo{'id':'"+okrWorkReportBaseInfo.getId()+"'} can not find any processor in person link." );
			}
		
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business( emc );
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrTaskHandled.class );
				
				//更新处理状态为处理中
				if( ids != null && !ids.isEmpty() ){
					for( String id : ids ){
						_okrWorkReportPersonLink = emc.find( id, OkrWorkReportPersonLink.class );
						_okrWorkReportPersonLink.setProcessStatus( "已处理" );
						emc.check( _okrWorkReportPersonLink, CheckPersistType.all );
					}
				}
				//添加已办，删除待办
				if( taskIds != null && !taskIds.isEmpty() ){
					for( String id : taskIds ){
						_okrTask = emc.find( id, OkrTask.class );
						if( _okrTask != null ){
							taskArriveDate = _okrTask.getArriveDateTime();
							taskArriveDateString = _okrTask.getArriveDateTimeStr();
							//生成已办信息
							okrTaskHandled = new OkrTaskHandled();
							okrTaskHandled.setActivityName( _okrTask.getActivityName() );
							okrTaskHandled.setArriveDateTime( _okrTask.getArriveDateTime() );
							okrTaskHandled.setArriveDateTimeStr( _okrTask.getArriveDateTimeStr() );
							okrTaskHandled.setCenterId( _okrTask.getCenterId() );
							okrTaskHandled.setCenterTitle( _okrTask.getCenterTitle() );
							okrTaskHandled.setDynamicObjectId( _okrTask.getDynamicObjectId() );
							okrTaskHandled.setDynamicObjectTitle( _okrTask.getDynamicObjectTitle() );
							okrTaskHandled.setDynamicObjectType( _okrTask.getDynamicObjectType() );
							okrTaskHandled.setProcessDateTime( new Date() );
							okrTaskHandled.setProcessDateTimeStr( dateOperation.getNowDateTime() );
							okrTaskHandled.setTargetTopUnitName( _okrTask.getTargetTopUnitName() );
							okrTaskHandled.setTargetIdentity( _okrTask.getTargetIdentity() );
							okrTaskHandled.setTargetName( _okrTask.getTargetName() );
							okrTaskHandled.setTargetUnitName( _okrTask.getTargetUnitName() );
							okrTaskHandled.setTitle( _okrTask.getTitle() );
							okrTaskHandled.setWorkType( _okrTask.getWorkType() );
							okrTaskHandled.setViewUrl( "" );
							okrTaskHandled.setWorkId( _okrTask.getWorkId() );
							okrTaskHandled.setWorkTitle( _okrTask.getWorkTitle() );
							taskHandledList.add( okrTaskHandled );
							//保存已办
							emc.persist( okrTaskHandled, CheckPersistType.all );
							//删除待办
							emc.remove( _okrTask, CheckRemoveType.all );
							taskList.add( _okrTask );
						}
					}
				}
				if( taskArriveDate == null ){
					taskArriveDate = new Date();
					taskArriveDateString = dateOperation.getNowDateTime();
				}
				//一、记录督办意见和处理日志
				okrWorkReportProcessLog = new OkrWorkReportProcessLog(); 
				okrWorkReportProcessLog.setActivityName( okrWorkReportBaseInfo.getActivityName() );
				okrWorkReportProcessLog.setArriveTime( taskArriveDate );
				okrWorkReportProcessLog.setArriveTimeStr( taskArriveDateString );
				okrWorkReportProcessLog.setCenterId( okrWorkReportBaseInfo.getCenterId() );
				okrWorkReportProcessLog.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
				okrWorkReportProcessLog.setDecision( "提交" );
				okrWorkReportProcessLog.setOpinion( adminSuperviseInfo );
				okrWorkReportProcessLog.setProcessLevel( processLevel );
				okrWorkReportProcessLog.setProcessTime( new Date() );
				okrWorkReportProcessLog.setProcessTimeStr(  dateOperation.getNowDateTime() );
				okrWorkReportProcessLog.setReportTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportProcessLog.setStayTime( 0L );
				okrWorkReportProcessLog.setTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportProcessLog.setWorkId( okrWorkReportBaseInfo.getWorkId() );
				okrWorkReportProcessLog.setProcessStatus( "已生效" );
				okrWorkReportProcessLog.setWorkReportId( okrWorkReportBaseInfo.getId() );
				okrWorkReportProcessLog.setProcessorName( processorName );
				okrWorkReportProcessLog.setProcessorUnitName( okrUserManagerService.getUnitNameByIdentity(userIdentity) );
				okrWorkReportProcessLog.setProcessorIdentity( userIdentity );
				okrWorkReportProcessLog.setProcessorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity(userIdentity) );
				//保存处理记录
				emc.persist( okrWorkReportProcessLog, CheckPersistType.all );
				emc.commit();
			}catch ( Exception e ) {
				throw e;
			}
			
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business( emc );
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				emc.beginTransaction( OkrWorkReportDetailInfo.class );
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrTaskHandled.class );
				okrWorkReportBaseInfo = emc.find( okrWorkReportBaseInfo.getId(), OkrWorkReportBaseInfo.class );
				okrWorkReportDetailInfo = emc.find( okrWorkReportBaseInfo.getId(), OkrWorkReportDetailInfo.class );
				if( okrWorkReportDetailInfo != null ){
					okrWorkReportDetailInfo.setAdminSuperviseInfo( adminSuperviseInfo );
				}
				
				//三、查询本层是否还有用户未处理，如果没有就准备发送下一个级别的待办
				ids = business.okrWorkReportPersonLinkFactory().getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, null, "处理中", "正常" );
				if( ids == null || ids.isEmpty() ){//该环节已经没有处理人了，准备处理下一层级
					//根据当前环节查询汇报的下一批处理人，可能是一个，也可能是多个
					//根据汇报当前的处理级别来计算下一个级别代号
					//下一个处理级别是 prcessLevel+1
					processLevel++;
					//查询下一个层级的所有处理人
					do{//根据汇报ID和需要的处理级别查询所有的处理人信息
						ids = business.okrWorkReportPersonLinkFactory().getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, null, "待处理", "正常" );
						if( ids != null && !ids.isEmpty()){
							break;
						}else{
							processLevel++ ;
						}
					}while( processLevel < maxProcessLevel || ( ids!=null && !ids.isEmpty()) );
					okrWorkReportBaseInfo.setCurrentProcessLevel( processLevel );
					
					//根据ID列表获取所有的处理人信息，更新处理状态为处理中
					next_okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list( ids );
					
					//当前是督办人处理, 
					//如果存在下一步处理人(审阅领导)就对所有的领导发送待办,
					//如果没有下一步处理人,就直接向汇报者发送待阅,通知汇报处理完成
					if( next_okrWorkReportPersonLinkList != null && !next_okrWorkReportPersonLinkList.isEmpty() ){
						okrWorkReportBaseInfo.setCurrentProcessorNameList( new ArrayList<>() );
						okrWorkReportBaseInfo.setCurrentProcessorIdentityList( new ArrayList<>() );
						okrWorkReportBaseInfo.setCurrentProcessorUnitNameList( new ArrayList<>() );
						okrWorkReportBaseInfo.setCurrentProcessorTopUnitNameList( new ArrayList<>() );
						
						for( OkrWorkReportPersonLink okrWorkReportPersonLink : next_okrWorkReportPersonLinkList ){
							okrWorkReportBaseInfo.setActivityName( okrWorkReportPersonLink.getActivityName() );
							okrWorkReportBaseInfo.setProcessStatus( okrWorkReportPersonLink.getActivityName() );
							okrWorkReportBaseInfo.setCurrentProcessLevel(processLevel);
							okrWorkReportBaseInfo.getCurrentProcessorNameList().add(okrWorkReportPersonLink.getProcessorName() );
							okrWorkReportBaseInfo.getCurrentProcessorIdentityList().add( okrWorkReportPersonLink.getProcessorIdentity() );
							okrWorkReportBaseInfo.getCurrentProcessorUnitNameList().add( okrWorkReportPersonLink.getProcessorUnitName() );
							okrWorkReportBaseInfo.getCurrentProcessorTopUnitNameList().add( okrWorkReportPersonLink.getProcessorTopUnitName() );
							
							//更新处理状态为处理中
							okrWorkReportPersonLink.setProcessStatus( "处理中" );
							//发送待办信息
							okrTask = new OkrTask();
							okrTask.setTitle( okrWorkReportBaseInfo.getTitle() );
							okrTask.setCenterId( okrWorkReportBaseInfo.getCenterId() );
							okrTask.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
							okrTask.setWorkId( okrWorkReportBaseInfo.getWorkId() );
							okrTask.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle() );
							okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
							okrTask.setTargetIdentity( okrWorkReportPersonLink.getProcessorIdentity() );
							okrTask.setTargetName( okrWorkReportPersonLink.getProcessorName() );
							okrTask.setTargetUnitName( okrWorkReportPersonLink.getProcessorUnitName() );
							okrTask.setTargetTopUnitName( okrWorkReportPersonLink.getProcessorTopUnitName() );			
							okrTask.setActivityName( okrWorkReportPersonLink.getActivityName() );
							okrTask.setArriveDateTime( new Date() );
							okrTask.setArriveDateTimeStr( dateOperation.getNowDateTime() );						
							okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId() );
							okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle() );
							okrTask.setDynamicObjectType( "工作汇报" );
							okrTask.setProcessType( "TASK" );
							okrTask.setStatus( "正常" );	
							okrTask.setViewUrl( "" );
							emc.persist( okrTask, CheckPersistType.all );
							taskList.add( okrTask );
						}
					}else{
						//没有查询到需要处理的下级处理人，那么汇报已经审阅完成
						okrWorkReportBaseInfo.setProcessStatus( "已完成" );
						okrWorkReportBaseInfo.setActivityName( "已完成" );
						okrWorkReportBaseInfo.setCurrentProcessLevel(processLevel);
						okrWorkReportBaseInfo.setCurrentProcessorTopUnitNameList( new ArrayList<>() );
						okrWorkReportBaseInfo.setCurrentProcessorIdentityList( new ArrayList<>() );
						okrWorkReportBaseInfo.setCurrentProcessorNameList( new ArrayList<>() );
						okrWorkReportBaseInfo.setCurrentProcessorUnitNameList( new ArrayList<>() );
					}
				}else{
					logger.debug( "本等级还有其他人员未完成处理，不需要处理下一审批层级的信息。" );
				}
				emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
				emc.commit();
			} catch ( Exception e ) {
				throw e;
			}
		}else{
			throw new Exception( "处理者不存在，身份：" + userIdentity);
		}
		if( taskList != null && taskList.size() > 0  ){
			for( OkrTask task : taskList ){
				List<String> workTypeList = new ArrayList<String>();
				workTypeList.add( task.getWorkType() );
				okrWorkReportTaskCollectService.checkReportCollectTask( task.getTargetIdentity(), workTypeList );
			}
			for( OkrTaskHandled taskHandled : taskHandledList ){
				List<String> workTypeList = new ArrayList<String>();
				workTypeList.add( taskHandled.getWorkType() );
				okrWorkReportTaskCollectService.checkReportCollectTask( taskHandled.getTargetIdentity(), workTypeList );
			}
		}
		return okrWorkReportBaseInfo;
	}

	/**
	 * 领导审批工作汇报
	 * @param name
	 * @param wrapIn
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo leaderProcess( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String opinion, String userIdentity ) throws Exception {
		if( okrWorkReportBaseInfo == null ){
			throw new Exception( "okrWorkReportBaseInfo is null." );
		}		
		if( opinion == null || opinion.isEmpty() ){
			throw new Exception( "opinion id is null, can not process report info." );
		}		
		if( userIdentity == null ){
			throw new Exception( "userIdentity id is null, can not process report info." );
		}
		Business business = null;
		Date taskArriveDate = null;
		String taskArriveDateString = null;
		Integer processLevel = 0;
		Integer maxProcessLevel = 0;
		Boolean isWorkCompleted = false;
		Integer progressPercent = 0;
		OkrTask okrTask = null;
		OkrTaskHandled okrTaskHandled = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		List<String> ids = null;
		List<String> log_ids = null;
		List<String> taskIds = null;
		List<OkrTask> taskList = new ArrayList<OkrTask>();
		List<OkrTaskHandled> taskHandledList = new ArrayList<>();
		List<OkrWorkReportPersonLink> current_okrWorkReportPersonLinkList = null;
		List<OkrWorkReportPersonLink> next_okrWorkReportPersonLinkList = null;
		
		String  processorName = okrUserManagerService.getPersonNameByIdentity( userIdentity );
		
		isWorkCompleted = okrWorkReportBaseInfo.getIsWorkCompleted();
		progressPercent = okrWorkReportBaseInfo.getProgressPercent();
		if( isWorkCompleted ) {
			progressPercent = 100;
		}				
		if( progressPercent == 100 ) {
			isWorkCompleted = true;
		}
		okrWorkReportBaseInfo.setIsWorkCompleted( isWorkCompleted );
		okrWorkReportBaseInfo.setProgressPercent( progressPercent );
		
		if( processorName != null ){
			//二、处理本层级的处理人处理信息以及待办信息删除，新增已办信息
			//获取汇报信息当前的处理级别
			processLevel = okrWorkReportBaseInfo.getCurrentProcessLevel();

			//查询本处理层级的处理人信息，更新处理状态为已处理
			ids = okrWorkReportPersonLinkService.getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, userIdentity, "处理中", "正常" );		

			//查询该次处理的待办信息
			taskIds = okrTaskService.listIdsByTargetActivityAndObjId( "工作汇报", okrWorkReportBaseInfo.getId(), okrWorkReportBaseInfo.getActivityName(), userIdentity );
			
			//获取该汇报的最大审批等级
			maxProcessLevel = okrWorkReportPersonLinkService.getMaxProcessLevel( okrWorkReportBaseInfo.getId() );
			if( maxProcessLevel == null  || maxProcessLevel <= 0 ){
				throw new Exception( "okrWorkReportBaseInfo{'id':'"+okrWorkReportBaseInfo.getId()+"'} can not find any processor in person link." );
			}
			//查询正在处理中的草稿状态的日志，更新日志中的审批意见
			//记录本次的处理意见,先根据汇报ID，环节，处理人，信息状态来查询是否有处理日志
			log_ids = okrWorkReportProcessLogService.listByReportIdAndProcessor( okrWorkReportBaseInfo.getId(), okrWorkReportBaseInfo.getActivityName(), userIdentity, "草稿" );
			
			//一、记录处理意见和处理日志
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business( emc );
				
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrTaskHandled.class );

				//将当前汇报,当前环节,当前人的待办清除,并且添加已办
				if( taskIds != null && !taskIds.isEmpty() ){
					for( String id : taskIds ){
						okrTask = emc.find( id, OkrTask.class );
						if( okrTask != null ){
							taskArriveDate = okrTask.getArriveDateTime();
							taskArriveDateString = okrTask.getArriveDateTimeStr();
							
							//生成已办信息
							okrTaskHandled = new OkrTaskHandled();
							okrTaskHandled.setActivityName( okrTask.getActivityName() );
							okrTaskHandled.setArriveDateTime( okrTask.getArriveDateTime() );
							okrTaskHandled.setArriveDateTimeStr( okrTask.getArriveDateTimeStr() );
							okrTaskHandled.setCenterId( okrTask.getCenterId() );
							okrTaskHandled.setCenterTitle( okrTask.getCenterTitle() );
							okrTaskHandled.setDynamicObjectId( okrTask.getDynamicObjectId() );
							okrTaskHandled.setDynamicObjectTitle( okrTask.getDynamicObjectTitle() );
							okrTaskHandled.setDynamicObjectType( okrTask.getDynamicObjectType() );
							okrTaskHandled.setProcessDateTime( new Date() );
							okrTaskHandled.setProcessDateTimeStr( dateOperation.getNowDateTime() );
							//okrTaskHandled.setDuration(duration);
							okrTaskHandled.setTargetTopUnitName( okrTask.getTargetTopUnitName() );
							okrTaskHandled.setTargetIdentity( okrTask.getTargetIdentity() );
							okrTaskHandled.setTargetName( okrTask.getTargetName() );
							okrTaskHandled.setTargetUnitName( okrTask.getTargetUnitName() );
							okrTaskHandled.setTitle( okrTask.getTitle() );
							okrTaskHandled.setViewUrl( "" );
							okrTaskHandled.setWorkId( okrTask.getWorkId() );
							okrTaskHandled.setWorkTitle( okrTask.getWorkTitle() );
							okrTaskHandled.setWorkType( okrTask.getWorkType() );
							taskHandledList.add( okrTaskHandled );
							//保存已办
							emc.persist( okrTaskHandled, CheckPersistType.all );
							//删除待办
							emc.remove( okrTask, CheckRemoveType.all );
							taskList.add( okrTask );
						}
					}
				}
				if( taskArriveDate == null ){
					taskArriveDate = new Date();
					taskArriveDateString = dateOperation.getNowDateTime();
				}
				//保存处理日志
				if( log_ids != null && log_ids.size() > 0 ){
					for( String id : log_ids ){
						okrWorkReportProcessLog  = emc.find( id, OkrWorkReportProcessLog.class );
						if( okrWorkReportProcessLog != null ){
							okrWorkReportProcessLog.setOpinion(opinion);
							okrWorkReportProcessLog.setProcessStatus( "已生效" );
							emc.check( okrWorkReportProcessLog, CheckPersistType.all );
						}
					}
				}else{
					okrWorkReportProcessLog = new OkrWorkReportProcessLog(); 
					okrWorkReportProcessLog.setActivityName( okrWorkReportBaseInfo.getActivityName() );
					okrWorkReportProcessLog.setArriveTime( taskArriveDate );
					okrWorkReportProcessLog.setArriveTimeStr( taskArriveDateString );
					okrWorkReportProcessLog.setCenterId( okrWorkReportBaseInfo.getCenterId() );
					okrWorkReportProcessLog.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
					okrWorkReportProcessLog.setDecision( "提交" );
					okrWorkReportProcessLog.setOpinion( opinion );
					okrWorkReportProcessLog.setProcessLevel( 0 );
					okrWorkReportProcessLog.setProcessTime( new Date() );
					okrWorkReportProcessLog.setProcessTimeStr(  dateOperation.getNowDateTime() );
					okrWorkReportProcessLog.setReportTitle( okrWorkReportBaseInfo.getTitle() );
					okrWorkReportProcessLog.setStayTime( 0L );
					okrWorkReportProcessLog.setTitle( okrWorkReportBaseInfo.getTitle() );
					okrWorkReportProcessLog.setWorkId( okrWorkReportBaseInfo.getWorkId() );
					okrWorkReportProcessLog.setProcessStatus( "已生效" );
					okrWorkReportProcessLog.setWorkReportId( okrWorkReportBaseInfo.getId() );
					okrWorkReportProcessLog.setProcessorName( processorName );
					okrWorkReportProcessLog.setProcessorUnitName( okrUserManagerService.getUnitNameByIdentity(userIdentity) );
					okrWorkReportProcessLog.setProcessorIdentity( userIdentity );
					okrWorkReportProcessLog.setProcessorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity(userIdentity) );
					
					emc.persist( okrWorkReportProcessLog, CheckPersistType.all );
				}
				
				if( ids != null && !ids.isEmpty() ){
					current_okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list( ids );
					if( current_okrWorkReportPersonLinkList != null && !current_okrWorkReportPersonLinkList.isEmpty() ){
						for( OkrWorkReportPersonLink okrWorkReportPersonLink : current_okrWorkReportPersonLinkList ){
							//更新处理状态为处理中
							okrWorkReportPersonLink.setProcessStatus( "已处理" );
							emc.check(okrWorkReportPersonLink, CheckPersistType.all );
						}
					}
				}

				emc.commit();
			} catch ( Exception e ) {
				throw e;
			}
			
			//计算当前处理人
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business( emc );
				
				// 三、查询本层是否还有用户未处理，如果没有就准备发送下一个级别的待办
				ids = business.okrWorkReportPersonLinkFactory().getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, null, "处理中", "正常" );

				okrWorkReportBaseInfo = emc.find(okrWorkReportBaseInfo.getId(), OkrWorkReportBaseInfo.class);

				emc.beginTransaction( OkrWorkReportProcessLog.class );
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrTaskHandled.class );
				
				if ( ids == null || ids.isEmpty() ) {
					// 该环节已经没有处理人了，准备处理下一层级
					// 根据当前环节查询汇报的下一批处理人，可能是一个，也可能是多个
					// 根据汇报当前的处理级别来计算下一个级别代号
					// 下一个处理级别是 prcessLevel+1
					processLevel++;
					// 查询下一个层级的所有处理人
					do {
						// 根据汇报ID和需要的处理级别查询所有的处理人信息
						ids = business.okrWorkReportPersonLinkFactory().getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, null, "待处理", "正常" );
						if (ids != null && !ids.isEmpty()) {
							break;
						} else {
							processLevel++;
						}
					} while ( processLevel < maxProcessLevel || ( ids != null && !ids.isEmpty()) );

					okrWorkReportBaseInfo.setCurrentProcessLevel(processLevel);
					// 根据ID列表获取所有的处理人信息，更新处理状态为处理中
					next_okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);
					
					if ( next_okrWorkReportPersonLinkList != null && !next_okrWorkReportPersonLinkList.isEmpty() ) {
						for ( OkrWorkReportPersonLink okrWorkReportPersonLink : next_okrWorkReportPersonLinkList ) {
							// 更新处理状态为处理中
							okrWorkReportPersonLink.setProcessStatus( "处理中" );
							// 发送待办信息
							okrTask = new OkrTask();
							okrTask.setTitle(okrWorkReportBaseInfo.getTitle());
							okrTask.setCenterId(okrWorkReportBaseInfo.getCenterId());
							okrTask.setCenterTitle(okrWorkReportBaseInfo.getCenterTitle());
							okrTask.setWorkId(okrWorkReportBaseInfo.getWorkId());
							okrTask.setWorkTitle(okrWorkReportBaseInfo.getWorkTitle());
							okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
							okrTask.setTargetIdentity(okrWorkReportPersonLink.getProcessorIdentity());
							okrTask.setTargetName(okrWorkReportPersonLink.getProcessorName());
							okrTask.setTargetUnitName(okrWorkReportPersonLink.getProcessorUnitName());
							okrTask.setTargetTopUnitName(okrWorkReportPersonLink.getProcessorTopUnitName());
							okrTask.setActivityName(okrWorkReportPersonLink.getActivityName());
							okrTask.setArriveDateTime(new Date());
							okrTask.setArriveDateTimeStr(dateOperation.getNowDateTime());
							okrTask.setDynamicObjectId(okrWorkReportBaseInfo.getId());
							okrTask.setDynamicObjectTitle(okrWorkReportBaseInfo.getTitle());
							okrTask.setDynamicObjectType( "工作汇报" );
							okrTask.setProcessType( "TASK" );
							okrTask.setStatus( "正常" );
							okrTask.setViewUrl( "" );
							emc.persist(okrTask, CheckPersistType.all);
							taskList.add( okrTask );
						}
					} else {
						// 没有查询到需要处理的下级处理人，那么汇报已经审阅完成
						okrWorkReportBaseInfo.setActivityName( "已完成" );
						okrWorkReportBaseInfo.setCurrentProcessorTopUnitNameList(new ArrayList<>());
						okrWorkReportBaseInfo.setCurrentProcessorIdentityList(new ArrayList<>());
						okrWorkReportBaseInfo.setCurrentProcessorNameList(new ArrayList<>());
						okrWorkReportBaseInfo.setCurrentProcessorUnitNameList(new ArrayList<>());
					}
				} else {
					// logger.debug( "本等级还有其他人员未完成处理，不需要处理下一审批层级的信息。 ids.size=" + ids.size() );
					// 需要把汇报的当前处理人重新组织
					okrWorkReportBaseInfo.setCurrentProcessorTopUnitNameList(new ArrayList<>());
					okrWorkReportBaseInfo.setCurrentProcessorIdentityList(new ArrayList<>());
					okrWorkReportBaseInfo.setCurrentProcessorNameList(new ArrayList<>());
					okrWorkReportBaseInfo.setCurrentProcessorUnitNameList(new ArrayList<>());
					next_okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);

					if (next_okrWorkReportPersonLinkList != null && !next_okrWorkReportPersonLinkList.isEmpty()) {
						for (OkrWorkReportPersonLink okrWorkReportPersonLink : next_okrWorkReportPersonLinkList) {
							okrWorkReportBaseInfo.getCurrentProcessorNameList().add( okrWorkReportPersonLink.getProcessorName() );
							okrWorkReportBaseInfo.getCurrentProcessorIdentityList().add( okrWorkReportPersonLink.getProcessorIdentity() );
							okrWorkReportBaseInfo.getCurrentProcessorUnitNameList().add( okrWorkReportPersonLink.getProcessorUnitName() );
							okrWorkReportBaseInfo.getCurrentProcessorTopUnitNameList().add( okrWorkReportPersonLink.getProcessorTopUnitName() );
						}
					}
				}
				emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
				emc.commit();
			} catch ( Exception e ) {
				throw e;
			}
		}else{
			throw new Exception( "处理者不存在，身份：" + userIdentity);
		}
		
		if( taskList != null && taskList.size() > 0  ){
			for( OkrTask task : taskList ){
				List<String> workTypeList = new ArrayList<String>();
				workTypeList.add( task.getWorkType() );
				okrWorkReportTaskCollectService.checkReportCollectTask( task.getTargetIdentity(), workTypeList );
			}
			for( OkrTaskHandled taskHandled : taskHandledList ){
				List<String> workTypeList = new ArrayList<String>();
				workTypeList.add( taskHandled.getWorkType() );
				okrWorkReportTaskCollectService.checkReportCollectTask( taskHandled.getTargetIdentity(), workTypeList );
			}
		}
		
		return okrWorkReportBaseInfo;
	}

	/**
	 * 根据传入的参数来保存工作汇报信息记录
	 * 一般来说，汇报的审核人就是工作的部署者，部分顶层组织需要根据要求增加环节：
	 * 模式一： 拟稿 - 顶层组织工作管理员 - 工作部署者
	 * 模式二： 拟稿 - 工作部署者 
	 * 将需要处理的人员记录到OkrWorkReportPersonLink，处理审核时，按这个LINK来
	 * 
	 * 提交后处理方式:
	 * 提交后会按配置[REPORT_AUTHOR_NOTICE]要求向工作授权者发送待办或者待阅信息
	 * 
	 * @param wrapIn
	 * @param okrWorkBaseInfo 
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo submitReportInfo( OkrWorkReportBaseInfo wrapInOkrWorkReportBaseInfo, OkrCenterWorkInfo okrCenterWorkInfo, OkrWorkBaseInfo okrWorkBaseInfo,
			String workPointAndRequirements,
			String progressDescription, String workPlan, String adminSuperviseInfo, String memo, String opinion ) throws Exception {
		if( wrapInOkrWorkReportBaseInfo == null ){
			throw new Exception( "传入的工作汇报信息为空，无法继续提交汇报信息" );
		}
		if( okrWorkBaseInfo == null ){
			throw new Exception( "传入的工作基础信息okrWorkBaseInfo为空，无法继续提交汇报信息" );
		}
		if( okrCenterWorkInfo == null ){
			throw new Exception( "传入的中心工作信息okrCenterWorkInfo为空，无法继续提交汇报信息" );
		}
		String workAdminIdentity = null; //通过系统配置来判断汇报是否需要工作管理员来补充信息或者审核
		/**
		 * 汇报流程方式
		 * reportWorkflowType = ADMIN_AND_ALLLEADER - 经过工作管理员和所有的批示领导
		 * reportWorkflowType = DEPLOYER - 工作部署者审核（默认）
		 */
		String reportWorkflowType = null; 
		/**
		 * report_author_notice = "NONE" 或者空，不通知授权者
		 * report_author_notice = "READ" 待阅通知
		 * report_author_notice = "TASK" 待办通知
		 */
		String report_author_notice = null;
		String personName  = null;
		Boolean isWorkCompleted = false;
		Integer progressPercent = 0;
		List<String> oldOkrTaskIds = null;
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList = new ArrayList<OkrWorkReportPersonLink>();
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo_tmp = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		List<OkrTask> taskList = new ArrayList<OkrTask>(); //需要发送待办的待办人信息
		List<OkrTask> oldOkrTaskList = null;
		OkrTask oldOkrTask = null;
		OkrTaskHandled okrTaskHandled = null;
		Integer processLevel = 0;
		String report_audit_control_level_str = null;
		Integer report_audit_control_level = 0;
		
		isWorkCompleted = wrapInOkrWorkReportBaseInfo.getIsWorkCompleted();
		progressPercent = wrapInOkrWorkReportBaseInfo.getProgressPercent();
		if( isWorkCompleted ) {
			progressPercent = 100;
		}				
		if( progressPercent == 100 ) {
			isWorkCompleted = true;
		}				
		wrapInOkrWorkReportBaseInfo.setIsWorkCompleted( isWorkCompleted );
		wrapInOkrWorkReportBaseInfo.setProgressPercent( progressPercent );
		
		okrWorkReportPersonLink = new OkrWorkReportPersonLink();
		okrWorkReportPersonLink.setActivityName( "草稿" );
		okrWorkReportPersonLink.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
		okrWorkReportPersonLink.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
		okrWorkReportPersonLink.setProcessLevel( processLevel );
		okrWorkReportPersonLink.setProcessorIdentity( wrapInOkrWorkReportBaseInfo.getReporterIdentity() );
		okrWorkReportPersonLink.setProcessorName( wrapInOkrWorkReportBaseInfo.getReporterName() );
		okrWorkReportPersonLink.setProcessorUnitName( wrapInOkrWorkReportBaseInfo.getReporterUnitName() );
		okrWorkReportPersonLink.setProcessorTopUnitName( wrapInOkrWorkReportBaseInfo.getReporterTopUnitName() );			
		okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
		okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
		okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
		okrWorkReportPersonLink.setProcessStatus( "已处理" );
		okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );
		
		//查询原有待办信息
		oldOkrTaskIds = okrTaskService.listIdsByTargetActivityAndObjId( 
				"工作汇报", //工作汇报拟稿
				wrapInOkrWorkReportBaseInfo.getId(), 
				"拟稿",
				wrapInOkrWorkReportBaseInfo.getReporterIdentity()
		);
		
		if( oldOkrTaskIds != null && !oldOkrTaskIds.isEmpty() ){
			oldOkrTaskList = okrTaskService.list( oldOkrTaskIds );
			if( oldOkrTaskList != null && !oldOkrTaskList.isEmpty() ){
				oldOkrTask = oldOkrTaskList.get( 0 );
			}
		}
		//新增已办
		okrTaskHandled = new OkrTaskHandled();
		if( oldOkrTask != null ){
			okrTaskHandled.setTitle( oldOkrTask.getTitle() );
			okrTaskHandled.setActivityName( oldOkrTask.getActivityName() );
			okrTaskHandled.setCenterId( oldOkrTask.getCenterId() );
			okrTaskHandled.setCenterTitle( oldOkrTask.getCenterTitle() );
			okrTaskHandled.setWorkId( oldOkrTask.getWorkId() );
			okrTaskHandled.setWorkTitle( oldOkrTask.getWorkTitle() );
			okrTaskHandled.setWorkType( oldOkrTask.getWorkType() );
			okrTaskHandled.setDynamicObjectId( oldOkrTask.getId() );
			okrTaskHandled.setDynamicObjectTitle( oldOkrTask.getTitle() );
			okrTaskHandled.setDynamicObjectType( oldOkrTask.getDynamicObjectType() );
			okrTaskHandled.setArriveDateTime( oldOkrTask.getArriveDateTime() );
			okrTaskHandled.setArriveDateTimeStr( oldOkrTask.getArriveDateTimeStr() );
		}else{
			okrTaskHandled.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
			okrTaskHandled.setActivityName( "拟稿" );
			okrTaskHandled.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
			okrTaskHandled.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
			okrTaskHandled.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
			okrTaskHandled.setWorkTitle( wrapInOkrWorkReportBaseInfo.getWorkTitle() );
			okrTaskHandled.setWorkType( wrapInOkrWorkReportBaseInfo.getWorkType() );
			okrTaskHandled.setDynamicObjectId( wrapInOkrWorkReportBaseInfo.getId() );
			okrTaskHandled.setDynamicObjectTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
			okrTaskHandled.setDynamicObjectType( "工作汇报" );//工作汇报拟稿
			okrTaskHandled.setArriveDateTime( null );
			okrTaskHandled.setArriveDateTimeStr( null );
		}
		okrTaskHandled.setTargetTopUnitName( wrapInOkrWorkReportBaseInfo.getReporterTopUnitName() );
		okrTaskHandled.setTargetIdentity( wrapInOkrWorkReportBaseInfo.getReporterIdentity() );
		okrTaskHandled.setTargetName( wrapInOkrWorkReportBaseInfo.getReporterName() );
		okrTaskHandled.setTargetUnitName( wrapInOkrWorkReportBaseInfo.getReporterUnitName() );
		okrTaskHandled.setProcessDateTime( new Date() );
		okrTaskHandled.setProcessDateTimeStr( dateOperation.getNowDateTime() );
		//okrTaskHandled.setDuration(duration);
		okrTaskHandled.setViewUrl( "" );
		
		//获取系统配置 - 汇报工作流方式（两种选择）
		try {
			reportWorkflowType = okrConfigSystemService.getValueWithConfigCode( "REPORT_WORKFLOW_TYPE" );
			wrapInOkrWorkReportBaseInfo.setReportWorkflowType( reportWorkflowType );
		} catch (Exception e) {
			logger.warn( "system get system config 'REPORT_WORKFLOW_TYPE' got an exception" );
			throw e;
		}
		
		//从系统设置中查询全局工作管理员身份
		try {
			workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
		} catch (Exception e) {
			logger.warn( "system get system config 'REPORT_SUPERVISOR' got an exception" );
			throw e;
		}
		
		//从系统设置中查询汇报审阅控制层级数
		try {
			report_audit_control_level_str = okrConfigSystemService.getValueWithConfigCode( "REPORT_AUDIT_LEVEL" );
			report_audit_control_level = Integer.parseInt( report_audit_control_level_str );
		} catch (Exception e) {
			logger.warn( "system get system config 'REPORT_AUDIT_LEVEL' got an exception" );
			report_audit_control_level = 0;
		}
		
		//从系统设置中查询是否需要给工作委托人发送待办或者待阅（NONE|TASK|READ）
		try {
			report_author_notice = okrConfigSystemService.getValueWithConfigCode("REPORT_AUTHOR_NOTICE");
		} catch (Exception e) {
			logger.warn("system get system config 'REPORT_AUTHOR_NOTICE' got an exception" );
			logger.error(e);
			report_audit_control_level = 0;
		}

		//首先判断该工作是否有授权，授权者是谁
		try {
			okrWorkAuthorizeRecord = getLastAuthorizeRecord( wrapInOkrWorkReportBaseInfo.getWorkId() );
		} catch (Exception e) {
			logger.warn("system get okrWorkAuthorizeRecord with work id got an exception" );
			logger.error(e);
		}
		
		if( okrWorkAuthorizeRecord != null ){
			//如果需要给委托者发待阅，那么直接给委托者发待阅
			//如果需要给委托者发待办，那么增加给委托者发待办的记录
			if( "TASK".equalsIgnoreCase( report_author_notice ) ){
				//增加处理链
				processLevel++;
				okrWorkReportPersonLink = new OkrWorkReportPersonLink();
				okrWorkReportPersonLink.setActivityName( "授权者审核" );
				okrWorkReportPersonLink.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
				okrWorkReportPersonLink.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
				okrWorkReportPersonLink.setProcessLevel( processLevel );
				okrWorkReportPersonLink.setProcessorIdentity( okrWorkAuthorizeRecord.getDelegatorIdentity() );
				okrWorkReportPersonLink.setProcessorName( okrWorkAuthorizeRecord.getDelegatorName() );
				okrWorkReportPersonLink.setProcessorUnitName( okrWorkAuthorizeRecord.getDelegatorUnitName() );
				okrWorkReportPersonLink.setProcessorTopUnitName( okrWorkAuthorizeRecord.getDelegatorTopUnitName() );			
				okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
				okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
				okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
				okrWorkReportPersonLink.setProcessStatus( "待处理" );
				okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );	
			}else if( "READ".equalsIgnoreCase( report_author_notice ) ){
				//直接发待阅消息
				addReportAuditReader( wrapInOkrWorkReportBaseInfo, okrWorkAuthorizeRecord.getDelegatorIdentity(), okrWorkAuthorizeRecord.getDelegatorName(),
						okrWorkAuthorizeRecord.getDelegatorUnitName(), okrWorkAuthorizeRecord.getDelegatorTopUnitName() );
			}
		}

		if( okrWorkBaseInfo.getWorkAuditLevel() <= report_audit_control_level ){
			/**
			 * 确定汇报工作流
			 * 
			 * 1、先确定工作汇报工作流执行方式：1）工作管理员督办 - 中心工作阅知领导审阅； 2）工作部署者审阅
			 * 2、如果是方式1）
				    a.判断系统设置中是否有设置工作管理员
		            b.如果有设置工作管理员，那么下一步处理者为工作管理员，如果没有设置工作管理员，那么判断中心工作是否有设置阅知领导
		            c.如果中心工作没有设置阅知领导，那么下一步处理者为工作部署者审阅，并且在汇报的descript中说明原因
			 * 3、汇报工作流执行方式生效工作层级
			 */
			//判断汇报工作流方式
			if( "ADMIN_AND_ALLLEADER".equalsIgnoreCase( wrapInOkrWorkReportBaseInfo.getReportWorkflowType() ) ){
				wrapInOkrWorkReportBaseInfo.setReportWorkflowType( "ADMIN_AND_ALLLEADER" );
				//判断系统设置中是否有设置工作管理员
				if( StringUtils.isNotEmpty( workAdminIdentity )){
					personName = okrUserManagerService.getPersonNameByIdentity( workAdminIdentity );
					wrapInOkrWorkReportBaseInfo.setWorkAdminIdentity( workAdminIdentity );
					wrapInOkrWorkReportBaseInfo.setWorkAdminName( personName );
					wrapInOkrWorkReportBaseInfo.setNeedAdminAudit( true );
				}else{//系统未设置全局的工作管理员
					wrapInOkrWorkReportBaseInfo.setDescription( "系统设置中未配置全局工作管理员，尝试将汇报提交给中心工作阅知领导。" );
				}
				//继续判断工作所属的中心工作中是否设置了汇报审阅领导
				if( ListTools.isNotEmpty( okrCenterWorkInfo.getReportAuditLeaderIdentityList() ) ){//中心工作已经配置了阅知领导
					wrapInOkrWorkReportBaseInfo.setReadLeadersNameList(okrCenterWorkInfo.getReportAuditLeaderNameList());
					wrapInOkrWorkReportBaseInfo.setReadLeadersIdentityList( okrCenterWorkInfo.getReportAuditLeaderIdentityList() );
					wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( true );
				}else{
					wrapInOkrWorkReportBaseInfo.setDescription( "工作汇报所在的中心工作未设置汇报阅知领导。" );
				}
				//如果管理员和汇报领导都没有设置，那么将方式设置为DEPLOYER
				if( StringUtils.isEmpty(workAdminIdentity) && ListTools.isNotEmpty( okrCenterWorkInfo.getReportAuditLeaderIdentityList() )){
					wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( false );
					wrapInOkrWorkReportBaseInfo.setNeedAdminAudit( false );
					wrapInOkrWorkReportBaseInfo.setReportWorkflowType( "DEPLOYER" );
					wrapInOkrWorkReportBaseInfo.setDescription( "中心工作未设置全局工作管理员和阅知领导，将汇报工作流方式修改为部署者阅知(DEPLOYER)。" );
				}
			}
			
			if( "DEPLOYER".equalsIgnoreCase( wrapInOkrWorkReportBaseInfo.getReportWorkflowType() ) ){
				wrapInOkrWorkReportBaseInfo.setNeedAdminAudit( false );
				wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( false );
			}
		}else{//不属于汇报控制级别内的直接汇报给工作部署者
			wrapInOkrWorkReportBaseInfo.setReportWorkflowType( "DEPLOYER" );
			wrapInOkrWorkReportBaseInfo.setNeedAdminAudit( false );
			wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( false );
			wrapInOkrWorkReportBaseInfo.setDescription( "汇报审核层级控制未控制到本级工作。" );
		}
		
		/**
		 * 根据系统设置以及中心工作设置来记录工作汇报的处理过程
		 */
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorNameList( new ArrayList<>() );
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorIdentityList( new ArrayList<>() );
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorUnitNameList( new ArrayList<>() );
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorTopUnitNameList( new ArrayList<>() );
		
		if( "ADMIN_AND_ALLLEADER".equalsIgnoreCase( wrapInOkrWorkReportBaseInfo.getReportWorkflowType() )){
			if( wrapInOkrWorkReportBaseInfo.getNeedAdminAudit() ){
				processLevel ++;
				workAdminIdentity = wrapInOkrWorkReportBaseInfo.getWorkAdminIdentity();
				if( StringUtils.isNotEmpty(workAdminIdentity) ){					
					personName = okrUserManagerService.getPersonNameByIdentity( workAdminIdentity );
					if( personName != null ){
						okrWorkReportPersonLink = new OkrWorkReportPersonLink();
						if( ListTools.isEmpty( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentityList() ) ){
							wrapInOkrWorkReportBaseInfo.setProcessStatus( "管理员督办" );
							wrapInOkrWorkReportBaseInfo.setActivityName( "管理员督办" );
							wrapInOkrWorkReportBaseInfo.setProcessType( "审批" );
							wrapInOkrWorkReportBaseInfo.setCurrentProcessLevel(processLevel);
							wrapInOkrWorkReportBaseInfo.getCurrentProcessorNameList().add( personName );
							wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentityList().add( workAdminIdentity );
							wrapInOkrWorkReportBaseInfo.getCurrentProcessorUnitNameList().add( okrUserManagerService.getUnitNameByIdentity( workAdminIdentity ) );
							wrapInOkrWorkReportBaseInfo.getCurrentProcessorTopUnitNameList().add( okrUserManagerService.getTopUnitNameByIdentity( workAdminIdentity ) );
							okrWorkReportPersonLink.setProcessStatus( "处理中" );
							taskList = addNewTask( wrapInOkrWorkReportBaseInfo, taskList );
						}else{
							okrWorkReportPersonLink.setProcessStatus( "待处理" );
						}
						okrWorkReportPersonLink.setActivityName( "管理员督办" );
						okrWorkReportPersonLink.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
						okrWorkReportPersonLink.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
						okrWorkReportPersonLink.setProcessLevel( processLevel );
						okrWorkReportPersonLink.setProcessorIdentity( workAdminIdentity );
						okrWorkReportPersonLink.setProcessorName( personName );
						okrWorkReportPersonLink.setProcessorUnitName( okrUserManagerService.getUnitNameByIdentity( workAdminIdentity ) );
						okrWorkReportPersonLink.setProcessorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( workAdminIdentity ) );			
						okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
						okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
						okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
						okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );
					}
				}
			}
			
			boolean hasAdminProcessor = false;
			if( ListTools.isNotEmpty( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentityList() )){
				hasAdminProcessor = true;
			}
			
			//为审阅领导添加okrWorkReportPersonLink
			if( wrapInOkrWorkReportBaseInfo.getNeedLeaderRead() ){
				if( ListTools.isNotEmpty( okrCenterWorkInfo.getReportAuditLeaderIdentityList() ) ){
					processLevel ++;
					for( String identity : okrCenterWorkInfo.getReportAuditLeaderIdentityList() ){
						personName = okrUserManagerService.getPersonNameByIdentity( identity );
						if( personName != null ){
							okrWorkReportPersonLink = new OkrWorkReportPersonLink();
							if( !hasAdminProcessor ){
								wrapInOkrWorkReportBaseInfo.setProcessStatus( "领导批示" );
								wrapInOkrWorkReportBaseInfo.setActivityName( "领导批示" );
								wrapInOkrWorkReportBaseInfo.setProcessType( "审阅" );
								wrapInOkrWorkReportBaseInfo.setCurrentProcessLevel(processLevel);
								wrapInOkrWorkReportBaseInfo.getCurrentProcessorNameList().add( personName );
								wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentityList().add( identity );
								wrapInOkrWorkReportBaseInfo.getCurrentProcessorUnitNameList().add( okrUserManagerService.getUnitNameByIdentity( identity ) );
								wrapInOkrWorkReportBaseInfo.getCurrentProcessorTopUnitNameList().add( okrUserManagerService.getTopUnitNameByIdentity( identity ) );
								okrWorkReportPersonLink.setProcessStatus( "处理中" );									
								taskList = addNewTask( wrapInOkrWorkReportBaseInfo, taskList );
							} else{
								okrWorkReportPersonLink.setProcessStatus( "待处理" );
							}
							okrWorkReportPersonLink.setActivityName( "领导批示" );
							okrWorkReportPersonLink.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
							okrWorkReportPersonLink.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
							okrWorkReportPersonLink.setProcessLevel( processLevel );
							okrWorkReportPersonLink.setProcessorIdentity( identity );
							okrWorkReportPersonLink.setProcessorName( personName );
							okrWorkReportPersonLink.setProcessorUnitName( okrUserManagerService.getUnitNameByIdentity( identity ) );
							okrWorkReportPersonLink.setProcessorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( identity ) );			
							okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
							okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
							okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
							okrWorkReportPersonLink.setProcessStatus( "待处理" );
							okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );
						}
					}
				}
			}
		}else{
			//直接汇报给工作部署者
			wrapInOkrWorkReportBaseInfo.setProcessStatus( "领导批示" );
			wrapInOkrWorkReportBaseInfo.setActivityName( "领导批示" );
			wrapInOkrWorkReportBaseInfo.setProcessType( "审阅" );
			wrapInOkrWorkReportBaseInfo.setCurrentProcessLevel(processLevel);
			wrapInOkrWorkReportBaseInfo.getCurrentProcessorNameList().add( okrWorkBaseInfo.getDeployerName() );
			wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentityList().add( okrWorkBaseInfo.getDeployerIdentity() );
			wrapInOkrWorkReportBaseInfo.getCurrentProcessorUnitNameList().add( okrWorkBaseInfo.getDeployerUnitName() );
			wrapInOkrWorkReportBaseInfo.getCurrentProcessorTopUnitNameList().add( okrWorkBaseInfo.getDeployerTopUnitName() );
			taskList = addNewTask( wrapInOkrWorkReportBaseInfo, taskList );
			
			processLevel ++;
			okrWorkReportPersonLink = new OkrWorkReportPersonLink();
			okrWorkReportPersonLink.setActivityName( "领导批示" );
			okrWorkReportPersonLink.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
			okrWorkReportPersonLink.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
			okrWorkReportPersonLink.setProcessLevel( processLevel );
			okrWorkReportPersonLink.setProcessorIdentity( okrWorkBaseInfo.getDeployerIdentity() );
			okrWorkReportPersonLink.setProcessorName( okrWorkBaseInfo.getDeployerName() );
			okrWorkReportPersonLink.setProcessorUnitName( okrWorkBaseInfo.getDeployerUnitName() );
			okrWorkReportPersonLink.setProcessorTopUnitName( okrWorkBaseInfo.getDeployerTopUnitName() );			
			okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
			okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
			okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
			okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );			
		}

		//创建汇报处理日志
		okrWorkReportProcessLog = new OkrWorkReportProcessLog(); 
		okrWorkReportProcessLog.setActivityName( "拟稿" );
		okrWorkReportProcessLog.setArriveTime( new Date() );
		okrWorkReportProcessLog.setArriveTimeStr( dateOperation.getNowDateTime());
		okrWorkReportProcessLog.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
		okrWorkReportProcessLog.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
		okrWorkReportProcessLog.setDecision( "提交" );
		okrWorkReportProcessLog.setOpinion( "请审批" );
		okrWorkReportProcessLog.setProcessLevel( 0 );
		okrWorkReportProcessLog.setProcessTime( new Date() );
		okrWorkReportProcessLog.setProcessTimeStr(  dateOperation.getNowDateTime() );
		okrWorkReportProcessLog.setReportTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
		okrWorkReportProcessLog.setStayTime( 0L );
		okrWorkReportProcessLog.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
		okrWorkReportProcessLog.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
		okrWorkReportProcessLog.setProcessStatus( "已生效" );
		okrWorkReportProcessLog.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
		okrWorkReportProcessLog.setProcessorName( wrapInOkrWorkReportBaseInfo.getReporterName() );
		okrWorkReportProcessLog.setProcessorUnitName( wrapInOkrWorkReportBaseInfo.getReporterUnitName() );
		okrWorkReportProcessLog.setProcessorIdentity( wrapInOkrWorkReportBaseInfo.getReporterIdentity() );
		okrWorkReportProcessLog.setProcessorTopUnitName( wrapInOkrWorkReportBaseInfo.getReporterTopUnitName() );
		
		//保存数据到数据库中
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( OkrWorkReportBaseInfo.class );
			emc.beginTransaction( OkrWorkReportDetailInfo.class );
			emc.beginTransaction( OkrWorkReportPersonLink.class );
			emc.beginTransaction( OkrWorkReportProcessLog.class );
			emc.beginTransaction( OkrTaskHandled.class );
			emc.beginTransaction( OkrTask.class );
			
			//删除待办信息
			if( oldOkrTaskIds != null && !oldOkrTaskIds.isEmpty() ){
				for( String oldTaskId : oldOkrTaskIds ){
					oldOkrTask = emc.find( oldTaskId, OkrTask.class );
					emc.remove( oldOkrTask, CheckRemoveType.all );
				}
			}
			
			//保存汇报处理记录
			emc.persist( okrWorkReportProcessLog, CheckPersistType.all);
			
			//保存已办
			emc.persist( okrTaskHandled, CheckPersistType.all );
			
			//保存所有的处理人信息（汇报处理链）
			if( okrWorkReportPersonLinkList != null && okrWorkReportPersonLinkList.size() > 0 ){
				for( OkrWorkReportPersonLink _okrWorkReportPersonLink : okrWorkReportPersonLinkList ){
					emc.persist( _okrWorkReportPersonLink, CheckPersistType.all);
				}
			}
			
			//向下一步处理者发送待办
			if( taskList != null && taskList.size() > 0  ){
				for( OkrTask task : taskList ){
					//logger.debug( "准备保存一条待办信息：target:" + task.getTargetIdentity() + ", title=" + task.getTitle() );
					emc.persist( task, CheckPersistType.all );
				}
			}
			//保存汇报基础信息
			//logger.debug( "get report base info : wrapIn.getId()="+ wrapIn.getId() );
			okrWorkReportBaseInfo_tmp = emc.find( wrapInOkrWorkReportBaseInfo.getId(), OkrWorkReportBaseInfo.class );
			//logger.debug( "wrapIn.getNeedAdminAudit() = " + wrapIn.getNeedAdminAudit() );
			if( okrWorkReportBaseInfo_tmp == null ){
				okrWorkReportBaseInfo = new OkrWorkReportBaseInfo();
				wrapInOkrWorkReportBaseInfo.copyTo( okrWorkReportBaseInfo );
				if( okrWorkReportBaseInfo.getCreateTime() == null ) {
					okrWorkReportBaseInfo.setCreateTime( new Date() );
				}
				if( okrWorkReportBaseInfo.getUpdateTime() == null ) {
					okrWorkReportBaseInfo.setUpdateTime( okrWorkReportBaseInfo.getCreateTime() );
				}
				okrWorkReportBaseInfo.setId( wrapInOkrWorkReportBaseInfo.getId() );
				//logger.debug( "okrWorkReportBaseInfo.getNeedAdminAudit() = " + okrWorkReportBaseInfo.getNeedAdminAudit() );
				emc.persist( okrWorkReportBaseInfo, CheckPersistType.all);//保存汇报基础信息
			}else{
				List<String> attachments = okrWorkReportBaseInfo_tmp.getAttachmentList();
				wrapInOkrWorkReportBaseInfo.copyTo( okrWorkReportBaseInfo_tmp, JpaObject.FieldsUnmodify );
				okrWorkReportBaseInfo_tmp.setAttachmentList( attachments );
				
				if( okrWorkReportBaseInfo_tmp.getCreateTime() == null ) {
					okrWorkReportBaseInfo_tmp.setCreateTime( new Date() );
				}
				if( okrWorkReportBaseInfo_tmp.getUpdateTime() == null ) {
					okrWorkReportBaseInfo_tmp.setUpdateTime( okrWorkReportBaseInfo_tmp.getCreateTime() );
				}
				emc.check( okrWorkReportBaseInfo_tmp, CheckPersistType.all );
				//logger.debug( "okrWorkReportBaseInfo_tmp.getNeedAdminAudit() = " + okrWorkReportBaseInfo_tmp.getNeedAdminAudit() );
				okrWorkReportBaseInfo = okrWorkReportBaseInfo_tmp;
			}
			
			okrWorkReportDetailInfo = emc.find( okrWorkReportBaseInfo.getId(), OkrWorkReportDetailInfo.class );
			//保存汇报详细信息
			//logger.debug( "get report detail info wrapIn.getId()="+ wrapIn.getId() );
			if( okrWorkReportDetailInfo != null ){
				okrWorkReportDetailInfo.setId( wrapInOkrWorkReportBaseInfo.getId() );
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
				okrWorkReportDetailInfo.setId( wrapInOkrWorkReportBaseInfo.getId() );//使用参数传入的ID作为记录的ID
				okrWorkReportDetailInfo.setCenterId(okrWorkReportBaseInfo.getCenterId());
				okrWorkReportDetailInfo.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
				okrWorkReportDetailInfo.setTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportDetailInfo.setWorkId( okrWorkReportBaseInfo.getWorkId() );
				okrWorkReportDetailInfo.setStatus( "正常" );
				okrWorkReportDetailInfo.setWorkPlan( workPlan );
				okrWorkReportDetailInfo.setProgressDescription( progressDescription );
				okrWorkReportDetailInfo.setWorkPointAndRequirements( workPointAndRequirements );
				okrWorkReportDetailInfo.setMemo( memo );
				emc.persist( okrWorkReportDetailInfo, CheckPersistType.all );	
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
		
		if( taskList != null && taskList.size() > 0  ){
			for( OkrTask task : taskList ){
				List<String> workTypeList = new ArrayList<String>();
				workTypeList.add( task.getWorkType() );
				okrWorkReportTaskCollectService.checkReportCollectTask( task.getTargetIdentity(), workTypeList );
			}
		}
		
		return okrWorkReportBaseInfo;
	}

	private OkrWorkAuthorizeRecord getLastAuthorizeRecord( String workId ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkAuthorizeRecordFactory().getLastAuthorizeRecord( workId, null, "正常" );
		} catch ( Exception e ) {
			throw e;
		}
	}

	private List<OkrTask> addNewTask( OkrWorkReportBaseInfo okrWorkReportBaseInfo, List<OkrTask> taskList ) throws Exception {
		if( taskList == null ){
			taskList = new ArrayList<OkrTask>();
		}
		List<String> identities = okrWorkReportBaseInfo.getCurrentProcessorIdentityList();
		List<String> names = okrWorkReportBaseInfo.getCurrentProcessorNameList();
		List<String> unitNames = okrWorkReportBaseInfo.getCurrentProcessorUnitNameList();
		List<String> topUnitNames = okrWorkReportBaseInfo.getCurrentProcessorTopUnitNameList();
		
		OkrTask okrTask = null;
		if( ListTools.isNotEmpty( identities )) {
			for( int i = 0; i< identities.size() ;  i ++) {
				okrTask = new OkrTask();
				okrTask.setTitle( okrWorkReportBaseInfo.getTitle() );
				okrTask.setCenterId( okrWorkReportBaseInfo.getCenterId() );
				okrTask.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
				okrTask.setWorkId( okrWorkReportBaseInfo.getWorkId() );
				okrTask.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle() );
				okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
				if( identities != null && identities.size() > i ) {
					okrTask.setTargetIdentity( identities.get(i) );
				}
				if( names != null && names.size() > i ) {
					okrTask.setTargetName( names.get(i) );
				}
				if( unitNames != null && unitNames.size() > i ) {
					okrTask.setTargetUnitName( unitNames.get(i) );
				}
				if( topUnitNames != null && topUnitNames.size() > i ) {
					okrTask.setTargetTopUnitName( topUnitNames.get(i) );		
				}
				okrTask.setActivityName( okrWorkReportBaseInfo.getActivityName() );
				okrTask.setArriveDateTime( new Date() );
				okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate( new Date(), "yyyy-MM-dd HH:mm:ss" ) );						
				okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId() );
				okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle() );
				okrTask.setDynamicObjectType( "工作汇报" );
				okrTask.setProcessType( "TASK" );
				okrTask.setStatus( "正常" );	
				okrTask.setViewUrl( "" );
				//logger.debug( "准备新增一条待办信息：target:" + okrTask.getTargetIdentity() + ", title=" + okrTask.getTitle() );
				taskList.add( okrTask );
			}
		}
		return taskList;
	}

	/**
	 * 为汇报人添加一条汇报确认的待阅信息
	 * @param okrWorkReportBaseInfo
	 * @param opinion
	 * @param loginIdentity
	 * @throws Exception 
	 */
	public void addReportConfirmReader( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String loginIdentity, String name, String unitName, String topUnitName  ) throws Exception {
		if( okrWorkReportBaseInfo == null ){
			throw new Exception( "okrWorkReportBaseInfo is null!" );
		}
		if( loginIdentity == null ){
			throw new Exception( "loginIdentity is null!" );
		}
		OkrTask okrTask = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( OkrTask.class );	
			okrTask = new OkrTask();
			okrTask.setTitle( okrWorkReportBaseInfo.getTitle());
			okrTask.setCenterId( okrWorkReportBaseInfo.getCenterId());
			okrTask.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle());
			okrTask.setWorkId( okrWorkReportBaseInfo.getWorkId());
			okrTask.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle());
			okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType());
			okrTask.setTargetIdentity( loginIdentity );
			okrTask.setTargetName( name );
			okrTask.setTargetUnitName( unitName );
			okrTask.setTargetTopUnitName( topUnitName );
			okrTask.setActivityName( "汇报确认" );
			okrTask.setArriveDateTime( new Date() );
			okrTask.setArriveDateTimeStr( dateOperation.getNowDateTime());
			okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId());
			okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle());
			okrTask.setDynamicObjectType( "汇报确认" );
			okrTask.setProcessType( "READ" );
			okrTask.setStatus( "正常" );
			okrTask.setViewUrl( "" );
			emc.persist( okrTask, CheckPersistType.all );
			emc.commit();
		}catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 为汇报人添加一条汇报确认的待阅信息
	 * @param okrWorkReportBaseInfo
	 * @param opinion
	 * @param loginIdentity
	 * @throws Exception 
	 */
	public void addReportAuditReader( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String loginIdentity, String name, String unitName, String topUnitName ) throws Exception {
		if( okrWorkReportBaseInfo == null ){
			throw new Exception( "okrWorkReportBaseInfo is null!" );
		}
		if( loginIdentity == null ){
			throw new Exception( "loginIdentity is null!" );
		}
		OkrTask okrTask = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( OkrTask.class );	
			okrTask = new OkrTask();
			okrTask.setTitle( okrWorkReportBaseInfo.getTitle());
			okrTask.setCenterId( okrWorkReportBaseInfo.getCenterId());
			okrTask.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle());
			okrTask.setWorkId( okrWorkReportBaseInfo.getWorkId());
			okrTask.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle());
			okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType());
			okrTask.setTargetIdentity( loginIdentity );
			okrTask.setTargetName( name );
			okrTask.setTargetUnitName( unitName );
			okrTask.setTargetTopUnitName( topUnitName );
			okrTask.setActivityName( "领导批示" );
			okrTask.setArriveDateTime( new Date() );
			okrTask.setArriveDateTimeStr( dateOperation.getNowDateTime());
			okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId());
			okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle());
			okrTask.setDynamicObjectType( "工作汇报" );
			okrTask.setProcessType( "READ" );
			okrTask.setStatus( "正常" );
			okrTask.setViewUrl( "" );
			emc.persist( okrTask, CheckPersistType.all );
			emc.commit();
		}catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 将汇报信息调度到结束
	 * 1、汇报信息的信息状态修改为“结束”，详细信息里状态修改为“结束”
	 * 2、汇报信息的当前处理环节“结束”
	 * 3、汇报信息待办信息
	 * 4、汇报信息待办汇总信息
	 * 5、汇报信息处理记录里添加系统处理记录
	 * 6、PERSONLINK记录里的处理状态修改为“结束”
	 * @param id
	 * @throws Exception
	 */
	public void dispatchToOver( String id ) throws Exception {
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		OkrTask okrTask = null;
		List<OkrTask> okrTaskList = new ArrayList<>();
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
			
			okrWorkReportBaseInfo = emc.find( id, OkrWorkReportBaseInfo.class );
			if ( null != okrWorkReportBaseInfo ) {
				okrWorkReportBaseInfo.setActivityName( "结束" );
				okrWorkReportBaseInfo.setStatus( "结束" );
				okrWorkReportBaseInfo.setProcessStatus( "自动结束" );
				emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
			}
			
			ids = business.okrTaskFactory().listIdsByReportId( id );
			if( ids != null && !ids.isEmpty() ){
				for( String _id : ids ){
					okrTask = emc.find( _id, OkrTask.class );
					if ( null != okrTask ) {
						okrTaskList.add( okrTask );
						emc.remove( okrTask, CheckRemoveType.all );
					}
				}
			}
			
			//处理所有的汇报详情
			okrWorkReportDetailInfo = emc.find( id, OkrWorkReportDetailInfo.class );
			if ( null != okrWorkReportDetailInfo ) {
				okrWorkReportDetailInfo.setStatus( "结束" );
				emc.check( okrWorkReportDetailInfo,CheckPersistType.all );
			}
			
			ids = business.okrWorkReportPersonLinkFactory().listIdsByReportId( id );
			if( ids != null && !ids.isEmpty() ){
				for( String _id : ids ){
					okrWorkReportPersonLink = emc.find( _id, OkrWorkReportPersonLink.class );
					if ( null != okrWorkReportPersonLink ) {
						okrWorkReportPersonLink.setActivityName( "结束" );
						okrWorkReportPersonLink.setStatus( "结束" );
						okrWorkReportPersonLink.setProcessStatus( "自动结束" );
						emc.check( okrWorkReportPersonLink,CheckPersistType.all );
					}
				}
			}
			
			if ( null != okrWorkReportDetailInfo ) {
				Date now  = new Date();
				okrWorkReportProcessLog = new OkrWorkReportProcessLog(); 
				okrWorkReportProcessLog.setActivityName( "结束" );
				okrWorkReportProcessLog.setArriveTime( now );
				okrWorkReportProcessLog.setArriveTimeStr( new DateOperation().getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss") );
				okrWorkReportProcessLog.setCenterId( okrWorkReportBaseInfo.getCenterId() );
				okrWorkReportProcessLog.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
				okrWorkReportProcessLog.setDecision( "结束" );
				okrWorkReportProcessLog.setOpinion( "新汇报已经生成, 原有汇报自动结束" );
				okrWorkReportProcessLog.setProcessLevel( 0 );
				okrWorkReportProcessLog.setProcessTime( now );
				okrWorkReportProcessLog.setProcessTimeStr( new DateOperation().getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss") );
				okrWorkReportProcessLog.setReportTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportProcessLog.setStayTime( 0L );
				okrWorkReportProcessLog.setTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportProcessLog.setWorkId( okrWorkReportBaseInfo.getWorkId() );
				okrWorkReportProcessLog.setProcessStatus( "已生效" );
				okrWorkReportProcessLog.setWorkReportId( okrWorkReportBaseInfo.getId() );
				okrWorkReportProcessLog.setProcessorName( "system" );
				//保存处理记录
				emc.persist( okrWorkReportProcessLog, CheckPersistType.all );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
		
		if( okrTaskList != null && okrTaskList.size() > 0  ){
			for( OkrTask task : okrTaskList ){
				List<String> workTypeList = new ArrayList<String>();
				workTypeList.add( task.getWorkType() );
				okrWorkReportTaskCollectService.checkReportCollectTask( task.getTargetIdentity(), workTypeList );
			}
		}
	}
}
