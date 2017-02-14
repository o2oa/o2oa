package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.HttpAttribute;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapInFilter;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapInOkrWorkReportBaseInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;
import com.x.organization.core.express.wrap.WrapPerson;

/**
 * 类   名：OkrWorkReportBaseInfoService<br/>
 * 实体类：OkrWorkReportBaseInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportBaseInfoService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkReportBaseInfoService.class );
	private BeanCopyTools<WrapInOkrWorkReportBaseInfo, OkrWorkReportBaseInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkReportBaseInfo.class, OkrWorkReportBaseInfo.class, null, WrapInOkrWorkReportBaseInfo.Excludes );	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrNotifyService okrNotifyService = new OkrNotifyService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	private DateOperation dateOperation = new DateOperation();
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	private OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	/**
	 * 根据传入的ID从数据库查询OkrWorkReportBaseInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkReportBaseInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkReportBaseInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkReportBaseInfo对象
	 * @param wrapIn
	 */
	public OkrWorkReportBaseInfo save( WrapInOkrWorkReportBaseInfo wrapIn ) throws Exception {
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
					//wrapin_copier.copy( wrapIn, okrWorkReportBaseInfo );
					//emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
				}else{//信息不存在，创建一个新的记录
					okrWorkReportBaseInfo = new OkrWorkReportBaseInfo();
					wrapin_copier.copy( wrapIn, okrWorkReportBaseInfo );
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
					okrWorkReportDetailInfo.setWorkPlan( wrapIn.getWorkPlan() );
					okrWorkReportDetailInfo.setProgressDescription( wrapIn.getProgressDescription() );
					okrWorkReportDetailInfo.setWorkPointAndRequirements( wrapIn.getWorkPointAndRequirements() );
					okrWorkReportDetailInfo.setMemo( wrapIn.getMemo() );
					emc.check( okrWorkReportDetailInfo, CheckPersistType.all );
				}else{
					okrWorkReportDetailInfo = new OkrWorkReportDetailInfo();
					okrWorkReportDetailInfo.setId( okrWorkReportBaseInfo.getId() );//使用参数传入的ID作为记录的ID
					okrWorkReportDetailInfo.setCenterId(okrWorkReportBaseInfo.getCenterId());
					okrWorkReportDetailInfo.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
					okrWorkReportDetailInfo.setTitle( okrWorkReportBaseInfo.getTitle() );
					okrWorkReportDetailInfo.setWorkId( okrWorkReportBaseInfo.getWorkId() );
					okrWorkReportDetailInfo.setStatus( "正常" );
					okrWorkReportDetailInfo.setWorkPlan( wrapIn.getWorkPlan() );
					okrWorkReportDetailInfo.setProgressDescription( wrapIn.getProgressDescription() );
					okrWorkReportDetailInfo.setWorkPointAndRequirements( wrapIn.getWorkPointAndRequirements() );
					okrWorkReportDetailInfo.setMemo( wrapIn.getMemo() );
					emc.persist( okrWorkReportDetailInfo, CheckPersistType.all);	
				}
				//判断该汇报创建者的待办是否存在，如果不存在就创建一个新的待办
				ids = business.okrTaskFactory().listIdsByTargetActivityAndObjId( "工作汇报拟稿", okrWorkReportBaseInfo.getId(), "拟稿", okrWorkReportBaseInfo.getReporterIdentity() );
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
					okrTask.setTargetOrganizationName( okrWorkReportBaseInfo.getReporterOrganizationName() );
					okrTask.setTargetCompanyName( okrWorkReportBaseInfo.getReporterCompanyName() );			
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
				logger.error( "OkrWorkReportBaseInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				emc.beginTransaction( OkrWorkReportDetailInfo.class );
				okrWorkReportBaseInfo = new OkrWorkReportBaseInfo();
				wrapin_copier.copy( wrapIn, okrWorkReportBaseInfo );
				
				okrWorkReportDetailInfo = new OkrWorkReportDetailInfo();
				okrWorkReportDetailInfo.setId( okrWorkReportBaseInfo.getId() );//使用参数传入的ID作为记录的ID
				okrWorkReportDetailInfo.setCenterId(okrWorkReportBaseInfo.getCenterId());
				okrWorkReportDetailInfo.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
				okrWorkReportDetailInfo.setTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportDetailInfo.setWorkId( okrWorkReportBaseInfo.getWorkId() );
				okrWorkReportDetailInfo.setStatus( "正常" );
				okrWorkReportDetailInfo.setWorkPlan( wrapIn.getWorkPlan() );
				okrWorkReportDetailInfo.setProgressDescription( wrapIn.getProgressDescription() );
				okrWorkReportDetailInfo.setWorkPointAndRequirements( wrapIn.getWorkPointAndRequirements() );
				okrWorkReportDetailInfo.setMemo( wrapIn.getMemo() );
				emc.persist( okrWorkReportDetailInfo, CheckPersistType.all);
				emc.persist( okrWorkReportBaseInfo, CheckPersistType.all);	
				//判断该汇报创建者的待办是否存在，如果不存在就创建一个新的待办
				//工作汇报拟稿
				ids = business.okrTaskFactory().listIdsByTargetActivityAndObjId( "工作汇报", okrWorkReportBaseInfo.getId(), "拟稿", okrWorkReportBaseInfo.getReporterIdentity() );
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
					okrTask.setTargetOrganizationName( okrWorkReportBaseInfo.getReporterOrganizationName() );
					okrTask.setTargetCompanyName( okrWorkReportBaseInfo.getReporterCompanyName() );			
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
				logger.error( "OkrWorkReportBaseInfo create got a error!", e);
				throw e;
			}
		}
		
		try{
			List<String> workTypeList = new ArrayList<String>();
			workTypeList.add( wrapIn.getWorkType() );
			okrWorkReportTaskCollectService.checkReportCollectTask( okrWorkReportBaseInfo.getReporterIdentity(), workTypeList );
		}catch( Exception e ){
			logger.error( "汇报信息保存成功，但对汇报者进行汇报待办汇总发生异常。", e );
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
			logger.error( "id is null, system can not delete any object." );
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
		List<OkrTask> taskList = new ArrayList<OkrTask>();
		List<OkrWorkReportPersonLink> next_okrWorkReportPersonLinkList = null;
		
		WrapPerson  processor = okrUserManagerService.getUserNameByIdentity(userIdentity);
		
		if( processor != null ){			
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
			//一、记录督办意见和处理日志
			okrWorkReportProcessLog = new OkrWorkReportProcessLog(); 
			okrWorkReportProcessLog.setActivityName( okrWorkReportBaseInfo.getActivityName() );
			okrWorkReportProcessLog.setArriveTime( new Date() );
			okrWorkReportProcessLog.setArriveTimeStr( dateOperation.getNowDateTime());
			okrWorkReportProcessLog.setCenterId( okrWorkReportBaseInfo.getCenterId() );
			okrWorkReportProcessLog.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
			okrWorkReportProcessLog.setDecision( "提交" );
			okrWorkReportProcessLog.setOpinion( "请审批" );
			okrWorkReportProcessLog.setProcessLevel( 0 );
			okrWorkReportProcessLog.setProcessTime( new Date() );
			okrWorkReportProcessLog.setProcessTimeStr(  dateOperation.getNowDateTime() );
			okrWorkReportProcessLog.setReportTitle( okrWorkReportBaseInfo.getTitle() );
			okrWorkReportProcessLog.setStayTime( 0L );
			okrWorkReportProcessLog.setTitle( okrWorkReportBaseInfo.getTitle() );
			okrWorkReportProcessLog.setWorkId( okrWorkReportBaseInfo.getWorkId() );
			okrWorkReportProcessLog.setProcessStatus( "已生效" );
			okrWorkReportProcessLog.setWorkReportId( okrWorkReportBaseInfo.getId() );
			okrWorkReportProcessLog.setProcessorName( processor.getName() );
			okrWorkReportProcessLog.setProcessorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity(userIdentity) );
			okrWorkReportProcessLog.setProcessorIdentity( userIdentity );
			okrWorkReportProcessLog.setProcessorCompanyName( okrUserManagerService.getCompanyNameByIdentity(userIdentity) );
		
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business( emc );
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrTaskHandled.class );
				//保存处理记录
				emc.persist( okrWorkReportProcessLog, CheckPersistType.all );
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
							okrTaskHandled.setTargetCompanyName( _okrTask.getTargetCompanyName() );
							okrTaskHandled.setTargetIdentity( _okrTask.getTargetIdentity() );
							okrTaskHandled.setTargetName( _okrTask.getTargetName() );
							okrTaskHandled.setTargetOrganizationName( _okrTask.getTargetOrganizationName() );
							okrTaskHandled.setTitle( _okrTask.getTitle() );
							okrTaskHandled.setWorkType( _okrTask.getWorkType() );
							okrTaskHandled.setViewUrl( "" );
							okrTaskHandled.setWorkId( _okrTask.getWorkId() );
							okrTaskHandled.setWorkTitle( _okrTask.getWorkTitle() );	
							//保存已办
							emc.persist( okrTaskHandled, CheckPersistType.all );
							//删除待办
							emc.remove( _okrTask, CheckRemoveType.all );
							taskList.add( _okrTask );
						}
					}
				}
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
				
				////////三、查询本层是否还有用户未处理，如果没有就准备发送下一个级别的待办
				ids = business.okrWorkReportPersonLinkFactory().getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, null, "处理中", "正常" );
				if( ids == null || ids.isEmpty() ){
					//该环节已经没有处理人了，准备处理下一层级
					//根据当前环节查询汇报的下一批处理人，可能是一个，也可能是多个
					//根据汇报当前的处理级别来计算下一个级别代号
					//下一个处理级别是 prcessLevel+1
					processLevel++;
					//查询下一个层级的所有处理人
					do{
						//根据汇报ID和需要的处理级别查询所有的处理人信息
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
					if( next_okrWorkReportPersonLinkList != null && !next_okrWorkReportPersonLinkList.isEmpty() ){
						
						okrWorkReportBaseInfo.setCurrentProcessorName( null );
						okrWorkReportBaseInfo.setCurrentProcessorIdentity( null );
						okrWorkReportBaseInfo.setCurrentProcessorOrganizationName( null );
						okrWorkReportBaseInfo.setCurrentProcessorCompanyName( null );
						
						for( OkrWorkReportPersonLink okrWorkReportPersonLink : next_okrWorkReportPersonLinkList ){
							 
							okrWorkReportBaseInfo.setActivityName( okrWorkReportPersonLink.getActivityName() );
							okrWorkReportBaseInfo.setProcessStatus( okrWorkReportPersonLink.getActivityName() );
							
							if( okrWorkReportBaseInfo.getCurrentProcessorName() == null || okrWorkReportBaseInfo.getCurrentProcessorName().isEmpty() ){
								okrWorkReportBaseInfo.setCurrentProcessorName( okrWorkReportPersonLink.getProcessorName() );
							}else{
								okrWorkReportBaseInfo.setCurrentProcessorName( okrWorkReportBaseInfo.getCurrentProcessorName() + "," + okrWorkReportPersonLink.getProcessorName() );
							}
							if( okrWorkReportBaseInfo.getCurrentProcessorIdentity() == null || okrWorkReportBaseInfo.getCurrentProcessorIdentity().isEmpty() ){
								okrWorkReportBaseInfo.setCurrentProcessorIdentity( okrWorkReportPersonLink.getProcessorIdentity() );
							}else{
								okrWorkReportBaseInfo.setCurrentProcessorIdentity( okrWorkReportBaseInfo.getCurrentProcessorIdentity() + "," + okrWorkReportPersonLink.getProcessorIdentity() );
							}
							if( okrWorkReportBaseInfo.getCurrentProcessorOrganizationName() == null || okrWorkReportBaseInfo.getCurrentProcessorOrganizationName().isEmpty() ){
								okrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrWorkReportPersonLink.getProcessorOrganizationName() );
							}else{
								okrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrWorkReportBaseInfo.getCurrentProcessorOrganizationName() + "," + okrWorkReportPersonLink.getProcessorOrganizationName() );
							}
							if( okrWorkReportBaseInfo.getCurrentProcessorCompanyName() == null || okrWorkReportBaseInfo.getCurrentProcessorCompanyName().isEmpty() ){
								okrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrWorkReportPersonLink.getProcessorCompanyName() );
							}else{
								okrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrWorkReportBaseInfo.getCurrentProcessorCompanyName() + "," + okrWorkReportPersonLink.getProcessorCompanyName() );
							}
							
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
							okrTask.setTargetOrganizationName( okrWorkReportPersonLink.getProcessorOrganizationName() );
							okrTask.setTargetCompanyName( okrWorkReportPersonLink.getProcessorCompanyName() );			
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
						okrWorkReportBaseInfo.setCurrentProcessorCompanyName( null );
						okrWorkReportBaseInfo.setCurrentProcessorIdentity( null );
						okrWorkReportBaseInfo.setCurrentProcessorName( null );
						okrWorkReportBaseInfo.setCurrentProcessorOrganizationName( null );
						
						//汇报审批已经完成，给汇报人发送待阅信息
						okrTask = new OkrTask();
						okrTask.setTitle(okrWorkReportBaseInfo.getTitle());
						okrTask.setCenterId(okrWorkReportBaseInfo.getCenterId());
						okrTask.setCenterTitle(okrWorkReportBaseInfo.getCenterTitle());
						okrTask.setWorkId(okrWorkReportBaseInfo.getWorkId());
						okrTask.setWorkTitle(okrWorkReportBaseInfo.getWorkTitle());
						okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
						okrTask.setTargetIdentity(okrWorkReportBaseInfo.getReporterIdentity());
						okrTask.setTargetName(okrWorkReportBaseInfo.getReporterName());
						okrTask.setTargetOrganizationName(okrWorkReportBaseInfo.getReporterOrganizationName());
						okrTask.setTargetCompanyName(okrWorkReportBaseInfo.getReporterCompanyName());
						okrTask.setActivityName( "汇报确认" );
						okrTask.setArriveDateTime( new Date() );
						okrTask.setArriveDateTimeStr(dateOperation.getNowDateTime());
						okrTask.setDynamicObjectId(okrWorkReportBaseInfo.getId());
						okrTask.setDynamicObjectTitle(okrWorkReportBaseInfo.getTitle());
						okrTask.setDynamicObjectType( "汇报确认" );
						okrTask.setProcessType( "READ" );
						okrTask.setStatus( "正常" );
						okrTask.setViewUrl( "" );
						emc.persist(okrTask, CheckPersistType.all);
						taskList.add( okrTask );
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
		Integer processLevel = 0;
		Integer maxProcessLevel = 0;
		OkrTask okrTask = null;
		OkrTaskHandled okrTaskHandled = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		List<String> ids = null;
		List<String> log_ids = null;
		List<String> taskIds = null;
		List<OkrTask> taskList = new ArrayList<OkrTask>();
		List<OkrWorkReportPersonLink> current_okrWorkReportPersonLinkList = null;
		List<OkrWorkReportPersonLink> next_okrWorkReportPersonLinkList = null;
		
		//logger.debug( ">>>>>>>>>>>>>  userIdentity=" + userIdentity );
		WrapPerson  processor = okrUserManagerService.getUserNameByIdentity( userIdentity );
		
		if( processor != null ){	
			//logger.debug( ">>>>>>>>>>>>>  processor is not null." );
			//二、处理本层级的处理人处理信息以及待办信息删除，新增已办信息
			//获取汇报信息当前的处理级别
			processLevel = okrWorkReportBaseInfo.getCurrentProcessLevel();
			//logger.debug( ">>>>>>>>>>>>>  processLevel=" + processLevel );
			//查询本处理层级的处理人信息，更新处理状态为已处理
			ids = okrWorkReportPersonLinkService.getProcessPersonLinkInfoByReportAndLevel( okrWorkReportBaseInfo.getId(), processLevel, userIdentity, "处理中", "正常" );		
			//logger.debug( ">>>>>>>>>>>>>  ids=" + ids.size() );
			//查询该次处理的待办信息
			taskIds = okrTaskService.listIdsByTargetActivityAndObjId( "工作汇报", okrWorkReportBaseInfo.getId(), okrWorkReportBaseInfo.getActivityName(), userIdentity );
			//logger.debug( ">>>>>>>>>>>>>  taskIds=" + taskIds.size() );
			
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
					okrWorkReportProcessLog.setArriveTime( new Date() );
					okrWorkReportProcessLog.setArriveTimeStr( dateOperation.getNowDateTime());
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
					okrWorkReportProcessLog.setProcessorName( processor.getName() );
					okrWorkReportProcessLog.setProcessorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity(userIdentity) );
					okrWorkReportProcessLog.setProcessorIdentity( userIdentity );
					okrWorkReportProcessLog.setProcessorCompanyName( okrUserManagerService.getCompanyNameByIdentity(userIdentity) );
					
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

				if( taskIds != null && !taskIds.isEmpty() ){
					for( String id : taskIds ){
						okrTask = emc.find( id, OkrTask.class );
						if( okrTask != null ){
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
							okrTaskHandled.setTargetCompanyName( okrTask.getTargetCompanyName() );
							okrTaskHandled.setTargetIdentity( okrTask.getTargetIdentity() );
							okrTaskHandled.setTargetName( okrTask.getTargetName() );
							okrTaskHandled.setTargetOrganizationName( okrTask.getTargetOrganizationName() );
							okrTaskHandled.setTitle( okrTask.getTitle() );
							okrTaskHandled.setViewUrl( "" );
							okrTaskHandled.setWorkId( okrTask.getWorkId() );
							okrTaskHandled.setWorkTitle( okrTask.getWorkTitle() );
							okrTaskHandled.setWorkType( okrTask.getWorkType() );
							//保存已办
							//logger.debug( ">>>>>>>>>>>>>  save okrTaskHandled : " + okrTaskHandled.getId() );
							emc.persist( okrTaskHandled, CheckPersistType.all );
							//删除待办
							//logger.debug( ">>>>>>>>>>>>>  delete okrTask : " + id );
							emc.remove( okrTask, CheckRemoveType.all );
							taskList.add( okrTask );
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
						ids = business.okrWorkReportPersonLinkFactory().getProcessPersonLinkInfoByReportAndLevel(
								okrWorkReportBaseInfo.getId(), processLevel, null, "待处理", "正常" );
						if (ids != null && !ids.isEmpty()) {
							break;
						} else {
							processLevel++;
						}
					} while (processLevel < maxProcessLevel || (ids != null && !ids.isEmpty()));

					okrWorkReportBaseInfo.setCurrentProcessLevel(processLevel);
					// 根据ID列表获取所有的处理人信息，更新处理状态为处理中
					next_okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);
					
					if (next_okrWorkReportPersonLinkList != null && !next_okrWorkReportPersonLinkList.isEmpty()) {
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
							okrTask.setTargetOrganizationName(okrWorkReportPersonLink.getProcessorOrganizationName());
							okrTask.setTargetCompanyName(okrWorkReportPersonLink.getProcessorCompanyName());
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
						okrWorkReportBaseInfo.setCurrentProcessorCompanyName(null);
						okrWorkReportBaseInfo.setCurrentProcessorIdentity(null);
						okrWorkReportBaseInfo.setCurrentProcessorName(null);
						okrWorkReportBaseInfo.setCurrentProcessorOrganizationName(null);
						//汇报审批已经完成，给汇报人发送待阅信息
						okrTask = new OkrTask();
						okrTask.setTitle(okrWorkReportBaseInfo.getTitle());
						okrTask.setCenterId(okrWorkReportBaseInfo.getCenterId());
						okrTask.setCenterTitle(okrWorkReportBaseInfo.getCenterTitle());
						okrTask.setWorkId(okrWorkReportBaseInfo.getWorkId());
						okrTask.setWorkTitle(okrWorkReportBaseInfo.getWorkTitle());
						okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
						okrTask.setTargetIdentity(okrWorkReportBaseInfo.getReporterIdentity());
						okrTask.setTargetName(okrWorkReportBaseInfo.getReporterName());
						okrTask.setTargetOrganizationName(okrWorkReportBaseInfo.getReporterOrganizationName());
						okrTask.setTargetCompanyName(okrWorkReportBaseInfo.getReporterCompanyName());
						okrTask.setActivityName( "汇报确认" );
						okrTask.setArriveDateTime( new Date() );
						okrTask.setArriveDateTimeStr(dateOperation.getNowDateTime());
						okrTask.setDynamicObjectId(okrWorkReportBaseInfo.getId());
						okrTask.setDynamicObjectTitle(okrWorkReportBaseInfo.getTitle());
						okrTask.setDynamicObjectType( "汇报确认" );
						okrTask.setProcessType( "READ" );
						okrTask.setStatus( "正常" );
						okrTask.setViewUrl( "" );
						emc.persist( okrTask, CheckPersistType.all );
						taskList.add( okrTask );
					}
				} else {
					//logger.debug( "本等级还有其他人员未完成处理，不需要处理下一审批层级的信息。 ids.size=" + ids.size() );
					// 需要把汇报的当前处理人重新组织
					okrWorkReportBaseInfo.setCurrentProcessorCompanyName(null);
					okrWorkReportBaseInfo.setCurrentProcessorIdentity(null);
					okrWorkReportBaseInfo.setCurrentProcessorName(null);
					okrWorkReportBaseInfo.setCurrentProcessorOrganizationName(null);

					next_okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);

					if (next_okrWorkReportPersonLinkList != null && !next_okrWorkReportPersonLinkList.isEmpty()) {
						for (OkrWorkReportPersonLink okrWorkReportPersonLink : next_okrWorkReportPersonLinkList) {
							if (okrWorkReportBaseInfo.getCurrentProcessorName() == null || okrWorkReportBaseInfo.getCurrentProcessorName().isEmpty()) {
								okrWorkReportBaseInfo.setCurrentProcessorName(okrWorkReportPersonLink.getProcessorName());
							} else {
								okrWorkReportBaseInfo.setCurrentProcessorName(okrWorkReportBaseInfo.getCurrentProcessorName() + "," + okrWorkReportPersonLink.getProcessorName());
							}
							if (okrWorkReportBaseInfo.getCurrentProcessorIdentity() == null || okrWorkReportBaseInfo.getCurrentProcessorIdentity().isEmpty()) {
								okrWorkReportBaseInfo.setCurrentProcessorIdentity(okrWorkReportPersonLink.getProcessorIdentity());
							} else {
								okrWorkReportBaseInfo.setCurrentProcessorIdentity(okrWorkReportBaseInfo.getCurrentProcessorIdentity() + "," + okrWorkReportPersonLink.getProcessorIdentity());
							}
							if (okrWorkReportBaseInfo.getCurrentProcessorOrganizationName() == null || okrWorkReportBaseInfo.getCurrentProcessorOrganizationName().isEmpty()) {
								okrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrWorkReportPersonLink.getProcessorOrganizationName());
							} else {
								okrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrWorkReportBaseInfo.getCurrentProcessorOrganizationName() + "," + okrWorkReportPersonLink.getProcessorOrganizationName());
							}
							if (okrWorkReportBaseInfo.getCurrentProcessorCompanyName() == null || okrWorkReportBaseInfo.getCurrentProcessorCompanyName().isEmpty()) {
								okrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrWorkReportPersonLink.getProcessorCompanyName());
							} else {
								okrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrWorkReportBaseInfo.getCurrentProcessorCompanyName() + "," + okrWorkReportPersonLink.getProcessorCompanyName());
							}
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
		}
		
		return okrWorkReportBaseInfo;
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
	public OkrWorkReportBaseInfo createReportDraft( OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
		if( okrWorkBaseInfo == null ){
			throw new Exception( "okrWorkBaseInfo is null, can not create report." );
		}
		Integer maxReportCount = 0;
		Date nextReportTime = null;
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
		okrWorkReportBaseInfo.setProgressPercent(0.00);
		okrWorkReportBaseInfo.setIsWorkCompleted(false);
		
		//创建者是系统创建
		okrWorkReportBaseInfo.setCreatorCompanyName( "SYSTEM" );
		okrWorkReportBaseInfo.setCreatorIdentity( "SYSTEM" );
		okrWorkReportBaseInfo.setCreatorName( "SYSTEM" );
		okrWorkReportBaseInfo.setCreatorOrganizationName( "SYSTEM" );
		
		//汇报是工作的责任者
		okrWorkReportBaseInfo.setReporterCompanyName( okrWorkBaseInfo.getResponsibilityCompanyName() );
		okrWorkReportBaseInfo.setReporterIdentity( okrWorkBaseInfo.getResponsibilityIdentity() );
		okrWorkReportBaseInfo.setReporterName( okrWorkBaseInfo.getResponsibilityEmployeeName() );
		okrWorkReportBaseInfo.setReporterOrganizationName( okrWorkBaseInfo.getResponsibilityOrganizationName() );
		
		//当前处理者是工作的责任者，汇报还是草稿状态
		okrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrWorkBaseInfo.getResponsibilityCompanyName() );
		okrWorkReportBaseInfo.setCurrentProcessorIdentity( okrWorkBaseInfo.getResponsibilityIdentity() );
		okrWorkReportBaseInfo.setCurrentProcessorName( okrWorkBaseInfo.getResponsibilityEmployeeName() );
		okrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrWorkBaseInfo.getResponsibilityOrganizationName() );
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
		okrTask.setTargetOrganizationName( okrWorkReportBaseInfo.getReporterOrganizationName() );
		okrTask.setTargetCompanyName( okrWorkReportBaseInfo.getReporterCompanyName() );			
		okrTask.setActivityName( "拟稿" );
		okrTask.setArriveDateTime( new Date() );
		okrTask.setArriveDateTimeStr( dateOperation.getNowDateTime() );						
		okrTask.setDynamicObjectId( okrWorkReportBaseInfo.getId() );
		okrTask.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle() );
		okrTask.setDynamicObjectType( "工作汇报" );//工作汇报拟稿
		okrTask.setProcessType( "TASK" );
		okrTask.setStatus( "正常" );	
		okrTask.setViewUrl( "" );
		
		nextReportTime = getNextReportTime( okrWorkBaseInfo.getReportTimeQue(), okrTask.getArriveDateTime() );
		//logger.debug( "nextReportTime:" + nextReportTime );
		//保存数据到数据库中
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrWorkBaseInfo = emc.find( okrWorkBaseInfo.getId(), OkrWorkBaseInfo.class );
			if( okrWorkBaseInfo != null ){
				emc.beginTransaction(OkrWorkReportBaseInfo.class);
				emc.beginTransaction(OkrWorkBaseInfo.class);
				emc.beginTransaction(OkrTask.class);
				// 保存汇报基础信息
				emc.persist(okrWorkReportBaseInfo, CheckPersistType.all);
				// 保存汇报待办
				emc.persist( okrTask, CheckPersistType.all );
				//还要修改工作的下一次汇报信息
				okrWorkBaseInfo.setNextReportTime( nextReportTime );
				okrWorkBaseInfo.setLastReportTime( okrTask.getArriveDateTime() );
				okrWorkBaseInfo.setReportCount( okrWorkReportBaseInfo.getReportCount() );
				emc.check( okrWorkBaseInfo, CheckPersistType.all );
				emc.commit();
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
	/**
	 * 根据工作汇报时间序列和当前时间获取下一次汇报时间
	 * @param reportTimeQue
	 * @return
	 * @throws Exception 
	 */
	private Date getNextReportTime(String reportTimeQue, Date lastReportTime ) throws Exception {
		String[] reportTimeArray = reportTimeQue.split( ";" );
		Date date = null;
		List<Date> dateList = new ArrayList<Date>();
		if( reportTimeArray != null && reportTimeArray.length > 0 ){
			for( String time : reportTimeArray ){
				//找出最早的，在lastReportTime的时间
				try{
					date = dateOperation.getDateFromString( time );
					dateList.add( date );
				}catch(Exception e ){
					throw new Exception( "reportTimeQue is invalid." );
				}
			}
			//对dateList进行排序
			dateList.sort(new Comparator<Date>(){
				public int compare(Date date1, Date date2){
					if( date1.before(date2)){
						return -1;
					}else if( date2.before(date1) ){
						return 1;
					}
					return 0;
				}
			});
			//找出晚于lastReportTime的第一个
			for( Date resultDate : dateList ){
				//logger.debug(  "lastReportTime:"+ lastReportTime + ", resultDate:" + resultDate.toString() );
				if( resultDate.after( lastReportTime )){
					return resultDate;
				}
			}
		}
		return null;
	}

	/**
	 * 根据传入的参数来保存工作汇报信息记录
	 * 一般来说，汇报的审核人就是工作的部署者，部分公司需要根据要求增加环节：
	 * 模式一： 拟稿 - 公司工作管理员 - 工作部署者
	 * 模式二： 拟稿 - 工作部署者 
	 * 将需要处理的人员记录到OkrWorkReportPersonLink，处理审核时，按这个LINK来
	 * @param wrapIn
	 * @param okrWorkBaseInfo 
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo submitReportInfo( WrapInOkrWorkReportBaseInfo wrapInOkrWorkReportBaseInfo, OkrCenterWorkInfo okrCenterWorkInfo, OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
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
		WrapPerson wrapPerson  = null;
		String[] identityArray = null;
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
		
		okrWorkReportPersonLink = new OkrWorkReportPersonLink();
		okrWorkReportPersonLink.setActivityName( "草稿" );
		okrWorkReportPersonLink.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
		okrWorkReportPersonLink.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
		okrWorkReportPersonLink.setProcessLevel( processLevel );
		okrWorkReportPersonLink.setProcessorIdentity( wrapInOkrWorkReportBaseInfo.getReporterIdentity() );
		okrWorkReportPersonLink.setProcessorName( wrapInOkrWorkReportBaseInfo.getReporterName() );
		okrWorkReportPersonLink.setProcessorOrganizationName( wrapInOkrWorkReportBaseInfo.getReporterOrganizationName() );
		okrWorkReportPersonLink.setProcessorCompanyName( wrapInOkrWorkReportBaseInfo.getReporterCompanyName() );			
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
		okrTaskHandled.setTargetCompanyName( wrapInOkrWorkReportBaseInfo.getReporterCompanyName() );
		okrTaskHandled.setTargetIdentity( wrapInOkrWorkReportBaseInfo.getReporterIdentity() );
		okrTaskHandled.setTargetName( wrapInOkrWorkReportBaseInfo.getReporterName() );
		okrTaskHandled.setTargetOrganizationName( wrapInOkrWorkReportBaseInfo.getReporterOrganizationName() );
		okrTaskHandled.setProcessDateTime( new Date() );
		okrTaskHandled.setProcessDateTimeStr( dateOperation.getNowDateTime() );
		//okrTaskHandled.setDuration(duration);
		okrTaskHandled.setViewUrl( "" );
		
		//获取系统配置 - 汇报工作流方式（两种选择）
		try {
			reportWorkflowType = okrConfigSystemService.getValueWithConfigCode( "REPORT_WORKFLOW_TYPE" );
			wrapInOkrWorkReportBaseInfo.setReportWorkflowType(reportWorkflowType);
		} catch (Exception e) {
			logger.error( "system get system config 'REPORT_WORKFLOW_TYPE' got an exception", e );
			throw e;
		}
		//从系统设置中查询全局工作管理员身份
		try {
			workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
		} catch (Exception e) {
			logger.error( "system get system config 'REPORT_SUPERVISOR' got an exception", e );
			throw e;
		}
		
		//从系统设置中查询全局工作管理员身份
		try {
			report_audit_control_level_str = okrConfigSystemService.getValueWithConfigCode( "REPORT_AUDIT_LEVEL" );
			report_audit_control_level = Integer.parseInt( report_audit_control_level_str );
		} catch (Exception e) {
			logger.error( "system get system config 'REPORT_AUDIT_LEVEL' got an exception", e );
			report_audit_control_level = 0;
		}
		
		//从系统设置中查询是否需要给工作委托人发送待办或者待阅
		try {
			report_author_notice = okrConfigSystemService.getValueWithConfigCode("REPORT_AUTHOR_NOTICE");
		} catch (Exception e) {
			logger.error("system get system config 'REPORT_AUTHOR_NOTICE' got an exception", e);
			report_audit_control_level = 0;
		}

		//首先判断该工作是否有授权，授权者是谁
		try {
			okrWorkAuthorizeRecord = getLastAuthorizeRecord( wrapInOkrWorkReportBaseInfo.getWorkId() );
		} catch (Exception e) {
			logger.error("system get okrWorkAuthorizeRecord with work id got an exception", e);
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
				okrWorkReportPersonLink.setProcessorOrganizationName( okrWorkAuthorizeRecord.getDelegatorOrganizationName() );
				okrWorkReportPersonLink.setProcessorCompanyName( okrWorkAuthorizeRecord.getDelegatorCompanyName() );			
				okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
				okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
				okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
				okrWorkReportPersonLink.setProcessStatus( "待处理" );
				okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );	
			}else if( "READ".equalsIgnoreCase( report_author_notice ) ){
				//直接发待办消息
				addReportAuditReader( wrapInOkrWorkReportBaseInfo, okrWorkAuthorizeRecord.getDelegatorIdentity(), okrWorkAuthorizeRecord.getDelegatorName(),
						okrWorkAuthorizeRecord.getDelegatorOrganizationName(), okrWorkAuthorizeRecord.getDelegatorCompanyName() );
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
				//logger.debug( "reportWorkflowType is ADMIN_AND_ALLLEADER!" );
				wrapInOkrWorkReportBaseInfo.setReportWorkflowType( "ADMIN_AND_ALLLEADER" );
				//判断系统设置中是否有设置工作管理员
				if( workAdminIdentity != null && !workAdminIdentity.isEmpty() ){
					wrapInOkrWorkReportBaseInfo.setWorkAdminIdentity( workAdminIdentity );
					wrapInOkrWorkReportBaseInfo.setWorkAdminName( "" );
					wrapInOkrWorkReportBaseInfo.setNeedAdminAudit( true );
				}else{
					//系统未设置全局的工作管理员
					wrapInOkrWorkReportBaseInfo.setDescription( "系统设置中未配置全局工作管理员，尝试将汇报提交给中心工作阅知领导。" );
				}
				//继续判断工作所属的中心工作中是否设置了汇报审阅领导
				if( okrCenterWorkInfo.getReportAuditLeaderIdentity() != null && !okrCenterWorkInfo.getReportAuditLeaderIdentity().isEmpty() ){
					//中心工作已经配置了阅知领导
					wrapInOkrWorkReportBaseInfo.setReadLeadersName( "" );
					wrapInOkrWorkReportBaseInfo.setReadLeadersIdentity( okrCenterWorkInfo.getReportAuditLeaderIdentity() );
					wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( true );
				}else{
					wrapInOkrWorkReportBaseInfo.setDescription( "工作汇报所在的中心工作未设置汇报阅知领导。" );
				}
				
				//如果管理员和汇报领导都没有设置，那么将方式设置为DEPLOYER
				if( ( workAdminIdentity == null || workAdminIdentity.isEmpty() )
					&& ( okrCenterWorkInfo.getReportAuditLeaderIdentity() == null || okrCenterWorkInfo.getReportAuditLeaderIdentity().isEmpty())
				){
					wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( false );
					wrapInOkrWorkReportBaseInfo.setNeedAdminAudit(false);
					wrapInOkrWorkReportBaseInfo.setReportWorkflowType( "DEPLOYER" );
					wrapInOkrWorkReportBaseInfo.setDescription( "中心工作未设置全局工作管理员和阅知领导，将汇报工作流方式修改为部署者阅知(DEPLOYER)。" );
				}
			}else{
				//logger.debug( "reportWorkflowType is not ADMIN_AND_ALLLEADER!" );
			}
			
			if( "DEPLOYER".equalsIgnoreCase( wrapInOkrWorkReportBaseInfo.getReportWorkflowType() ) ){
				//logger.debug( "reportWorkflowType is DEPLOYER!" );
				//如果直接汇报给工作部署者
				wrapInOkrWorkReportBaseInfo.setNeedAdminAudit( false );
				wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( false );
			}else{
				//logger.debug( "reportWorkflowType is not DEPLOYER!" );
			}
		}else{
			//直接汇报给工作部署者
			wrapInOkrWorkReportBaseInfo.setReportWorkflowType( "DEPLOYER" );
			wrapInOkrWorkReportBaseInfo.setNeedAdminAudit( false );
			wrapInOkrWorkReportBaseInfo.setNeedLeaderRead( false );
			wrapInOkrWorkReportBaseInfo.setDescription( "汇报审核层级控制未控制到本级工作。" );
		}
		
		/**
		 * 根据系统设置以及中心工作设置来记录工作汇报的处理过程
		 */
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorName( null );
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorIdentity( null );
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorOrganizationName( null );
		wrapInOkrWorkReportBaseInfo.setCurrentProcessorCompanyName( null );
		
		//logger.debug( "set report processors. okrWorkReportBaseInfo.getReportWorkflowType()="+wrapIn.getReportWorkflowType());
		if( "ADMIN_AND_ALLLEADER".equalsIgnoreCase( wrapInOkrWorkReportBaseInfo.getReportWorkflowType() )){
			//logger.debug( "reportWorkflowType is ADMIN_AND_ALLLEADER!" );
			if( wrapInOkrWorkReportBaseInfo.getNeedAdminAudit() ){
				//logger.debug( "okrWorkReportBaseInfo.getNeedAdminAudit() is true " );
				processLevel ++;
				workAdminIdentity = wrapInOkrWorkReportBaseInfo.getWorkAdminIdentity();
				//logger.debug( "workAdminIdentity = " + workAdminIdentity );
				if( workAdminIdentity != null && !workAdminIdentity.isEmpty() ){					
					wrapPerson = okrUserManagerService.getUserNameByIdentity( workAdminIdentity );
					if( wrapPerson != null ){
						okrWorkReportPersonLink = new OkrWorkReportPersonLink();
						if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity() == null ){
							//logger.debug( "add work admin to current processor and send task" );
							wrapInOkrWorkReportBaseInfo.setProcessStatus( "管理员督办" );
							wrapInOkrWorkReportBaseInfo.setActivityName( "管理员督办" );
							wrapInOkrWorkReportBaseInfo.setProcessType( "审批" );
							
							if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorName() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorName().isEmpty() ){
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorName( wrapPerson.getName() );
							}else{
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorName( wrapInOkrWorkReportBaseInfo.getCurrentProcessorName() + "," + wrapPerson.getName() );
							}
							if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity().isEmpty() ){
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorIdentity( workAdminIdentity );
							}else{
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorIdentity( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity() + "," + workAdminIdentity );
							}
							if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorOrganizationName() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorOrganizationName().isEmpty() ){
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( workAdminIdentity ) );
							}else{
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorOrganizationName( wrapInOkrWorkReportBaseInfo.getCurrentProcessorOrganizationName() + "," + okrUserManagerService.getDepartmentNameByIdentity( workAdminIdentity ) );
							}
							if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorCompanyName() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorCompanyName().isEmpty() ){
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrUserManagerService.getCompanyNameByIdentity( workAdminIdentity ) );
							}else{
								wrapInOkrWorkReportBaseInfo.setCurrentProcessorCompanyName( wrapInOkrWorkReportBaseInfo.getCurrentProcessorCompanyName() + "," + okrUserManagerService.getCompanyNameByIdentity( workAdminIdentity ) );
							}
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
						okrWorkReportPersonLink.setProcessorName( wrapPerson.getName() );
						okrWorkReportPersonLink.setProcessorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( workAdminIdentity ) );
						okrWorkReportPersonLink.setProcessorCompanyName( okrUserManagerService.getCompanyNameByIdentity( workAdminIdentity ) );			
						okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
						okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
						okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
						
						okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );
					}
				}
			}
			
			boolean hasAdminProcessor = false;
			if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity() != null &&  !wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity().isEmpty() ){
				hasAdminProcessor = true;
			}
			
			//为审阅领导添加okrWorkReportPersonLink
			if( wrapInOkrWorkReportBaseInfo.getNeedLeaderRead() ){
				//logger.debug( "okrWorkReportBaseInfo.getNeedLeaderRead() is true " );
				if( okrCenterWorkInfo.getReportAuditLeaderIdentity() != null && !okrCenterWorkInfo.getReportAuditLeaderIdentity().isEmpty() ){
					identityArray = okrCenterWorkInfo.getReportAuditLeaderIdentity().split( "," );
					if( identityArray != null && identityArray.length > 0 ){
						processLevel ++;
						for( String identity : identityArray ){
							wrapPerson = okrUserManagerService.getUserNameByIdentity( identity );
							if( wrapPerson != null ){
								okrWorkReportPersonLink = new OkrWorkReportPersonLink();
								if( !hasAdminProcessor ){
									wrapInOkrWorkReportBaseInfo.setProcessStatus( "领导批示" );
									wrapInOkrWorkReportBaseInfo.setActivityName( "领导批示" );
									wrapInOkrWorkReportBaseInfo.setProcessType( "审阅" );
									if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorName() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorName().isEmpty() ){
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorName( wrapPerson.getName() );
									}else{
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorName( wrapInOkrWorkReportBaseInfo.getCurrentProcessorName() + "," + wrapPerson.getName() );
									}
									if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity().isEmpty() ){
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorIdentity( identity );
									}else{
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorIdentity( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity() + "," + identity );
									}
									if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorOrganizationName() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorOrganizationName().isEmpty() ){
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( identity ) );
									}else{
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorOrganizationName( wrapInOkrWorkReportBaseInfo.getCurrentProcessorOrganizationName() + "," + okrUserManagerService.getDepartmentNameByIdentity( identity ) );
									}
									if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorCompanyName() == null || wrapInOkrWorkReportBaseInfo.getCurrentProcessorCompanyName().isEmpty() ){
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrUserManagerService.getCompanyNameByIdentity( identity ) );
									}else{
										wrapInOkrWorkReportBaseInfo.setCurrentProcessorCompanyName( wrapInOkrWorkReportBaseInfo.getCurrentProcessorCompanyName() + "," + okrUserManagerService.getCompanyNameByIdentity( identity ) );
									}
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
								okrWorkReportPersonLink.setProcessorName( wrapPerson.getName() );
								okrWorkReportPersonLink.setProcessorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( identity ) );
								okrWorkReportPersonLink.setProcessorCompanyName( okrUserManagerService.getCompanyNameByIdentity( identity ) );			
								okrWorkReportPersonLink.setTitle( wrapInOkrWorkReportBaseInfo.getTitle() );
								okrWorkReportPersonLink.setWorkId( wrapInOkrWorkReportBaseInfo.getWorkId() );
								okrWorkReportPersonLink.setWorkReportId( wrapInOkrWorkReportBaseInfo.getId() );
								okrWorkReportPersonLink.setProcessStatus( "待处理" );
								okrWorkReportPersonLinkList.add( okrWorkReportPersonLink );
							}
						}
					}
				}
			}
		}else{
			//logger.debug( "reportWorkflowType is not ADMIN_AND_ALLLEADER!" );
			//直接汇报给工作部署者
			processLevel ++;
			okrWorkReportPersonLink = new OkrWorkReportPersonLink();
			if( wrapInOkrWorkReportBaseInfo.getCurrentProcessorIdentity() == null ){
				wrapInOkrWorkReportBaseInfo.setProcessStatus( "领导批示" );
				wrapInOkrWorkReportBaseInfo.setActivityName( "领导批示" );
				wrapInOkrWorkReportBaseInfo.setProcessType( "审阅" );
				wrapInOkrWorkReportBaseInfo.setCurrentProcessorName( okrWorkBaseInfo.getDeployerName() );
				wrapInOkrWorkReportBaseInfo.setCurrentProcessorIdentity( okrWorkBaseInfo.getDeployerIdentity() );
				wrapInOkrWorkReportBaseInfo.setCurrentProcessorOrganizationName( okrWorkBaseInfo.getDeployerOrganizationName() );
				wrapInOkrWorkReportBaseInfo.setCurrentProcessorCompanyName( okrWorkBaseInfo.getDeployerCompanyName() );
				okrWorkReportPersonLink.setProcessStatus( "处理中" );				
				taskList = addNewTask( wrapInOkrWorkReportBaseInfo, taskList );
			} else{
				okrWorkReportPersonLink.setProcessStatus( "待处理" );
			}					
			okrWorkReportPersonLink.setActivityName( "领导批示" );
			okrWorkReportPersonLink.setCenterId( wrapInOkrWorkReportBaseInfo.getCenterId() );
			okrWorkReportPersonLink.setCenterTitle( wrapInOkrWorkReportBaseInfo.getCenterTitle() );
			okrWorkReportPersonLink.setProcessLevel( processLevel );
			okrWorkReportPersonLink.setProcessorIdentity( okrWorkBaseInfo.getDeployerIdentity() );
			okrWorkReportPersonLink.setProcessorName( okrWorkBaseInfo.getDeployerName() );
			okrWorkReportPersonLink.setProcessorOrganizationName( okrWorkBaseInfo.getDeployerOrganizationName() );
			okrWorkReportPersonLink.setProcessorCompanyName( okrWorkBaseInfo.getDeployerCompanyName() );			
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
		okrWorkReportProcessLog.setProcessorOrganizationName( wrapInOkrWorkReportBaseInfo.getReporterOrganizationName() );
		okrWorkReportProcessLog.setProcessorIdentity( wrapInOkrWorkReportBaseInfo.getReporterIdentity() );
		okrWorkReportProcessLog.setProcessorCompanyName( wrapInOkrWorkReportBaseInfo.getReporterCompanyName() );
		
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
					//logger.debug( ">>>>>>>>>>>>>>>>>>>>保存数据, id=" + _okrWorkReportPersonLink.getId() + ", status:" + _okrWorkReportPersonLink.getProcessStatus() );
					emc.persist( _okrWorkReportPersonLink, CheckPersistType.all);
					//logger.debug( ">>>>>>>>>>>>>>>>>>>>保存数据, over." );
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
				okrWorkReportBaseInfo = wrapin_copier.copy( wrapInOkrWorkReportBaseInfo, okrWorkReportBaseInfo );
				okrWorkReportBaseInfo.setId( wrapInOkrWorkReportBaseInfo.getId() );
				//logger.debug( "okrWorkReportBaseInfo.getNeedAdminAudit() = " + okrWorkReportBaseInfo.getNeedAdminAudit() );
				emc.persist( okrWorkReportBaseInfo, CheckPersistType.all);//保存汇报基础信息
			}else{
				wrapin_copier.copy( wrapInOkrWorkReportBaseInfo, okrWorkReportBaseInfo_tmp );
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
				okrWorkReportDetailInfo.setWorkPlan( wrapInOkrWorkReportBaseInfo.getWorkPlan() );
				okrWorkReportDetailInfo.setProgressDescription( wrapInOkrWorkReportBaseInfo.getProgressDescription() );
				okrWorkReportDetailInfo.setWorkPointAndRequirements( wrapInOkrWorkReportBaseInfo.getWorkPointAndRequirements() );
				okrWorkReportDetailInfo.setMemo( wrapInOkrWorkReportBaseInfo.getMemo() );
				emc.check( okrWorkReportDetailInfo, CheckPersistType.all );
			}else{
				okrWorkReportDetailInfo = new OkrWorkReportDetailInfo();
				okrWorkReportDetailInfo.setId( wrapInOkrWorkReportBaseInfo.getId() );//使用参数传入的ID作为记录的ID
				okrWorkReportDetailInfo.setCenterId(okrWorkReportBaseInfo.getCenterId());
				okrWorkReportDetailInfo.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
				okrWorkReportDetailInfo.setTitle( okrWorkReportBaseInfo.getTitle() );
				okrWorkReportDetailInfo.setWorkId( okrWorkReportBaseInfo.getWorkId() );
				okrWorkReportDetailInfo.setStatus( "正常" );
				okrWorkReportDetailInfo.setWorkPlan( wrapInOkrWorkReportBaseInfo.getWorkPlan() );
				okrWorkReportDetailInfo.setProgressDescription( wrapInOkrWorkReportBaseInfo.getProgressDescription() );
				okrWorkReportDetailInfo.setWorkPointAndRequirements( wrapInOkrWorkReportBaseInfo.getWorkPointAndRequirements() );
				okrWorkReportDetailInfo.setMemo( wrapInOkrWorkReportBaseInfo.getMemo() );
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
			return business.okrWorkAuthorizeRecordFactory().getLastAuthorizeRecord( workId, null);
		} catch ( Exception e ) {
			throw e;
		}
	}

	private List<OkrTask> addNewTask( OkrWorkReportBaseInfo okrWorkReportBaseInfo, List<OkrTask> taskList ) throws Exception {
		if( taskList == null ){
			taskList = new ArrayList<OkrTask>();
		}
		OkrTask okrTask = new OkrTask();
		okrTask.setTitle( okrWorkReportBaseInfo.getTitle() );
		okrTask.setCenterId( okrWorkReportBaseInfo.getCenterId() );
		okrTask.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
		okrTask.setWorkId( okrWorkReportBaseInfo.getWorkId() );
		okrTask.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle() );
		okrTask.setWorkType( okrWorkReportBaseInfo.getWorkType() );
		okrTask.setTargetIdentity( okrWorkReportBaseInfo.getCurrentProcessorIdentity() );
		okrTask.setTargetName( okrWorkReportBaseInfo.getCurrentProcessorName() );
		okrTask.setTargetOrganizationName( okrWorkReportBaseInfo.getCurrentProcessorOrganizationName() );
		okrTask.setTargetCompanyName( okrWorkReportBaseInfo.getCurrentProcessorCompanyName() );				
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
		return taskList;
	}

	/**
	 * 根据工作ID查询所有汇报信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listByWorkId( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().listByWorkId( workId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据汇报信息ID列表，查询汇报信息对象
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportBaseInfo> listByIds(List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().list(ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 下一页
	 * @param id
	 * @param count
	 * @param wrapIn
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportBaseInfo> listNextWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = new ArrayList<OkrWorkReportBaseInfo>();
		if( count == null ){
			count = 20;
		}
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkReportBaseInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().listNextWithFilter( id, count, sequence, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkReportBaseInfoList;
	}
	
	/**
	 * 上一页
	 * @param id
	 * @param count
	 * @param wrapIn
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportBaseInfo> listPrevWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = new ArrayList<OkrWorkReportBaseInfo>();
		if( count == null ){
			count = 20;
		}
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkReportBaseInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().listPrevWithFilter( id, count, sequence, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkReportBaseInfoList;
	}

	public Long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
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
				//logger.debug( ">>>>>>>>>>>>>>>>>>>>ids is not null, ids.size()=" + ids.size() );
				//有数据需要更新
				for( String id : ids ){
					okrWorkReportProcessLog = emc.find( id, OkrWorkReportProcessLog.class );
					if( okrWorkReportProcessLog != null ){
						okrWorkReportProcessLog.setOpinion( opinion );
						emc.check( okrWorkReportProcessLog, CheckPersistType.all );
					}
				}
			}else{
				//logger.debug( ">>>>>>>>>>>>>>>>>>>>ids is null, ids.size()=0, create a new log......" );
				//没有数据，需要创建新的日志
				WrapPerson wrapPerson  = okrUserManagerService.getUserNameByIdentity( processorIdentity );
				if( wrapPerson != null ){
					//logger.debug( ">>>>>>>>>>>>>>>>>>>>wrapPerson exsist, processorIdentity=" + processorIdentity );
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
					okrWorkReportProcessLog.setProcessorName( wrapPerson.getName() );
					okrWorkReportProcessLog.setProcessorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( processorIdentity ) );
					okrWorkReportProcessLog.setProcessorIdentity( processorIdentity );
					okrWorkReportProcessLog.setProcessorCompanyName( okrUserManagerService.getCompanyNameByIdentity( processorIdentity ) );
					
					emc.persist( okrWorkReportProcessLog, CheckPersistType.all );
				}
			}
			
			emc.commit();
		} catch ( Exception e ) {
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
	public void addReportConfirmReader( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String loginIdentity, String name, String organizationName, String companyName  ) throws Exception {
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
			okrTask.setTargetOrganizationName( organizationName );
			okrTask.setTargetCompanyName( companyName );
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
	public void addReportAuditReader( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String loginIdentity, String name, String organizationName, String companyName ) throws Exception {
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
			okrTask.setTargetOrganizationName( organizationName );
			okrTask.setTargetCompanyName( companyName );
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
	 * 根据工作ID获取该工作最后一次工作汇报
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo getLastReportBaseInfo(String workId) throws Exception {
		Business business = null;
		if( workId == null || workId.isEmpty() ){
			logger.error( "workId is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().getLastReportBaseInfo( workId );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
