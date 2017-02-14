package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapInFilter;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapInOkrWorkBaseInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkProblemInfo;
import com.x.okr.entity.OkrWorkProblemPersonLink;
import com.x.okr.entity.OkrWorkProblemProcessLog;
import com.x.okr.entity.OkrWorkProcessLink;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class OkrWorkBaseInfoService {

	private Logger logger = LoggerFactory.getLogger( OkrWorkBaseInfoService.class );
	private BeanCopyTools<WrapInOkrWorkBaseInfo, OkrWorkBaseInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkBaseInfo.class, OkrWorkBaseInfo.class, null, WrapInOkrWorkBaseInfo.Excludes );
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrNotifyService okrNotifyService = new OkrNotifyService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	private DateOperation dateOperation = new DateOperation();
	
	/**
	 * 根据指定的ID从数据库查询OkrWorkBaseInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkBaseInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			return emc.find( id, OkrWorkBaseInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据指定的ID列表查询具体工作信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listByIds( List<String> ids ) throws Exception{
		if( ids  == null || ids.size() == 0 ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {	
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().list(ids);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据工作ID，获取指定工作的所有下级工作ID列表
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<String> getSubNormalWorkBaseInfoIds( String workId ) throws Exception {
		if( workId  == null || workId.isEmpty() ){
			throw new Exception( "workId is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getSubNormalWorkBaseInfoIds( workId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 权限校验，判断用户是否有权限拆解工作
	 * @param workId  -- 被拆解工作的ID
	 * @param userName  -- 操作的用户姓名
	 * @return
	 * @throws Exception 
	 */
	public boolean canDismantlingWorkByIdentity( String workId, String userIdentity ) throws Exception{
		/**
		 * 1、判断用户是否是该工作的责任者，责任者可以进行工作拆解
		 */
		//先根据工作的ID，用户的姓名，身份（责任者），查询工作的干系人信息，如果有，则可以进行部署
		Business business = null;
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		statuses.add( "正常" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			ids = business.okrWorkPersonFactory().listByWorkAndIdentity( null, workId, userIdentity, "责任者", statuses );			
		}catch( Exception e ){
			throw e;
		}		
		if( ids != null && ids.size() > 0 ){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 向数据库保存OkrWorkBaseInfo对象, 第一次保存或者是继续拆解工作
	 * @param wrapIn
	 * @return 
	 */
	public OkrWorkBaseInfo save( WrapInOkrWorkBaseInfo wrapIn ) throws Exception {
		OkrWorkPerson okrWorkPerson_tmp = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<OkrWorkPerson> okrWorkPersonList = null;
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		Business business = null;
		int shortChartCount = 30;
		statuses.add( "正常" );
		
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > shortChartCount ){
				okrWorkBaseInfo =  emc.find( wrapIn.getId(), OkrWorkBaseInfo.class ); //查询基础信息
				okrWorkDetailInfo =  emc.find( wrapIn.getId(), OkrWorkDetailInfo.class ); //查询详细信息
			}
			emc.beginTransaction( OkrWorkBaseInfo.class );
			emc.beginTransaction( OkrWorkDetailInfo.class );
			emc.beginTransaction( OkrWorkPerson.class );
			//保存工作基础内容
			if( okrWorkBaseInfo == null ){
				okrWorkBaseInfo = new OkrWorkBaseInfo();
				wrapin_copier.copy( wrapIn, okrWorkBaseInfo );
				okrWorkBaseInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID	
				if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
					if( wrapIn.getWorkDetail() != null && wrapIn.getWorkDetail().length() > shortChartCount ){
						okrWorkBaseInfo.setTitle( wrapIn.getWorkDetail().substring( 0, shortChartCount ) );
					}else{
						okrWorkBaseInfo.setTitle( wrapIn.getWorkDetail() );
					}
				}
				if( wrapIn.getWorkDetail() != null && wrapIn.getWorkDetail().length() > shortChartCount ){
					okrWorkBaseInfo.setShortWorkDetail( wrapIn.getWorkDetail().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortWorkDetail( wrapIn.getWorkDetail() );
				}
				if( wrapIn.getDutyDescription() != null && wrapIn.getDutyDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortDutyDescription( wrapIn.getDutyDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortDutyDescription( wrapIn.getDutyDescription() );
				}				
				if( wrapIn.getLandmarkDescription() != null && wrapIn.getLandmarkDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortLandmarkDescription( wrapIn.getLandmarkDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortLandmarkDescription( wrapIn.getLandmarkDescription() );
				}				
				if( wrapIn.getMajorIssuesDescription() != null && wrapIn.getMajorIssuesDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortMajorIssuesDescription( wrapIn.getMajorIssuesDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortMajorIssuesDescription( wrapIn.getMajorIssuesDescription() );
				}				
				if( wrapIn.getProgressAction() != null && wrapIn.getProgressAction().length() > shortChartCount ){
					okrWorkBaseInfo.setShortProgressAction( wrapIn.getProgressAction().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortProgressAction( wrapIn.getProgressAction() );
				}				
				if( wrapIn.getProgressPlan() != null && wrapIn.getProgressPlan().length() > shortChartCount ){
					okrWorkBaseInfo.setShortProgressPlan( wrapIn.getProgressPlan().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortProgressPlan( wrapIn.getProgressPlan() );
				}				
				if( wrapIn.getResultDescription() != null && wrapIn.getResultDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortResultDescription( wrapIn.getResultDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortResultDescription( wrapIn.getResultDescription() );
				}
				emc.persist( okrWorkBaseInfo, CheckPersistType.all);
			}else{//更新				
				wrapin_copier.copy( wrapIn, okrWorkBaseInfo );
				if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
					if( wrapIn.getWorkDetail() != null && wrapIn.getWorkDetail().length() > shortChartCount ){
						okrWorkBaseInfo.setTitle( wrapIn.getWorkDetail().substring( 0, shortChartCount ) );
					}else{
						okrWorkBaseInfo.setTitle( wrapIn.getWorkDetail() );
					}
				}
				if( wrapIn.getWorkDetail() != null && wrapIn.getWorkDetail().length() > shortChartCount ){
					okrWorkBaseInfo.setShortWorkDetail( wrapIn.getWorkDetail().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortWorkDetail( wrapIn.getWorkDetail() );
				}				
				if( wrapIn.getDutyDescription() != null && wrapIn.getDutyDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortDutyDescription( wrapIn.getDutyDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortDutyDescription( wrapIn.getDutyDescription() );
				}				
				if( wrapIn.getLandmarkDescription() != null && wrapIn.getLandmarkDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortLandmarkDescription( wrapIn.getLandmarkDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortLandmarkDescription( wrapIn.getLandmarkDescription() );
				}				
				if( wrapIn.getMajorIssuesDescription() != null && wrapIn.getMajorIssuesDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortMajorIssuesDescription( wrapIn.getMajorIssuesDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortMajorIssuesDescription( wrapIn.getMajorIssuesDescription() );
				}				
				if( wrapIn.getProgressAction() != null && wrapIn.getProgressAction().length() > shortChartCount ){
					okrWorkBaseInfo.setShortProgressAction( wrapIn.getProgressAction().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortProgressAction( wrapIn.getProgressAction() );
				}				
				if( wrapIn.getProgressPlan() != null && wrapIn.getProgressPlan().length() > shortChartCount ){
					okrWorkBaseInfo.setShortProgressPlan( wrapIn.getProgressPlan().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortProgressPlan( wrapIn.getProgressPlan() );
				}				
				if( wrapIn.getResultDescription() != null && wrapIn.getResultDescription().length() > shortChartCount ){
					okrWorkBaseInfo.setShortResultDescription( wrapIn.getResultDescription().substring( 0, shortChartCount ) );
				}else{
					okrWorkBaseInfo.setShortResultDescription( wrapIn.getResultDescription() );
				}
				emc.check( okrWorkBaseInfo, CheckPersistType.all );	
			}
			//保存详细信息数据
			if( okrWorkDetailInfo == null ){
				okrWorkDetailInfo = new OkrWorkDetailInfo();
				okrWorkDetailInfo.setId( wrapIn.getId() ); //详细信息的ID与工作基础信息ID一致
				okrWorkDetailInfo.setCenterId( wrapIn.getCenterId() );
				okrWorkDetailInfo.setWorkDetail( wrapIn.getWorkDetail() );
				okrWorkDetailInfo.setDutyDescription( wrapIn.getDutyDescription() );
				okrWorkDetailInfo.setLandmarkDescription( wrapIn.getLandmarkDescription() );
				okrWorkDetailInfo.setMajorIssuesDescription( wrapIn.getMajorIssuesDescription() );
				okrWorkDetailInfo.setProgressAction( wrapIn.getProgressAction() );
				okrWorkDetailInfo.setProgressPlan( wrapIn.getProgressPlan() );
				okrWorkDetailInfo.setResultDescription( wrapIn.getResultDescription() );
				emc.persist( okrWorkDetailInfo, CheckPersistType.all);
			}else{//更新
				okrWorkDetailInfo.setCenterId( wrapIn.getCenterId() );
				okrWorkDetailInfo.setWorkDetail( wrapIn.getWorkDetail() );
				okrWorkDetailInfo.setDutyDescription( wrapIn.getDutyDescription() );
				okrWorkDetailInfo.setLandmarkDescription( wrapIn.getLandmarkDescription() );
				okrWorkDetailInfo.setMajorIssuesDescription( wrapIn.getMajorIssuesDescription() );
				okrWorkDetailInfo.setProgressAction( wrapIn.getProgressAction() );
				okrWorkDetailInfo.setProgressPlan( wrapIn.getProgressPlan() );
				okrWorkDetailInfo.setResultDescription( wrapIn.getResultDescription() );
				emc.check( okrWorkDetailInfo, CheckPersistType.all );	
			}
			//保存工作的干系人信息，先根据工作基础信息来获取工作所有的干系人对象信息
			okrWorkPersonList = okrWorkPersonService.getWorkPersonListByWorkBaseInfoForWorkSave( okrWorkBaseInfo );
			//logger.debug( ">>>>>>>>>>>>>>>>>>>>>>>>okrWorkPersonList:" + okrWorkPersonList.size() );
			if( okrWorkPersonList != null && okrWorkPersonList.size() > 0 ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					ids = business.okrWorkPersonFactory().listByWorkAndIdentity( okrWorkPerson.getCenterId(), okrWorkPerson.getWorkId(), okrWorkPerson.getEmployeeIdentity(), okrWorkPerson.getProcessIdentity(), statuses );
					if( ids != null && ids.size() > 0 ){
						for( String id : ids ){
							okrWorkPerson_tmp =  emc.find( id, OkrWorkPerson.class );
							if( okrWorkPerson_tmp != null ){
								emc.remove( okrWorkPerson_tmp );
							}
						}
					}
					emc.persist( okrWorkPerson, CheckPersistType.all );	
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.error( "OkrWorkBaseInfo update/save get a error!" );
			throw e;
		}
		return okrWorkBaseInfo;
	}

	/**
	 * 强制删除，不管有没有下级工作信息，根据ID从数据库中删除OkrWorkBaseInfo对象
	 * 递归删除
	 * @param id
	 * @throws Exception
	 */
	public void deleteForce( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		List<String> ids = getSubNormalWorkBaseInfoIds( id );
		if( ids != null && ids.size() > 0 ){
			for( String workid : ids ){
				deleteForce( workid );
				//deleteByWorkId( workid );
			}
		}else{
			//已经没有下级工作了，可以进行删除
			deleteByWorkId( id );
		}
	}
	
	/**
	 * 删除工作信息，如果有下级信息则无法进行删除
	 * @param id
	 * @throws Exception
	 */
	public void deleteByWorkId( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> subWorkIds = null;
		List<String> ids = null;
		List<String> ids_work = null;
		List<String> statuses = new ArrayList<String>();
		OkrTask okrTask = null;
		Business business = null;
		boolean excuteSuccess = true; //判断执行是否正常
		statuses.add( "正常" );
		
		subWorkIds = getSubNormalWorkBaseInfoIds( workId );
		if( subWorkIds != null && subWorkIds.size() > 0 ){
			throw new Exception( "该工作存在"+ subWorkIds.size() +"个下级工作，该工作暂无法删除。" );
		}else{//工作可以被删除
			//logger.debug( "开始删除工作......" );
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
				//删除时详细信息也一并删除，还有所有的下级工作，汇报，请示，人员，审批信息等等
				if( excuteSuccess ){
					try{
						okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system find okrWorkBaseInfo by workid got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						okrWorkDetailInfo = emc.find( workId, OkrWorkDetailInfo.class );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system find okrWorkBaseInfo by workid got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					//删除工作以及工作相关信息
					emc.beginTransaction( OkrWorkBaseInfo.class );
					emc.beginTransaction( OkrWorkDetailInfo.class );
					emc.beginTransaction( OkrWorkPerson.class );
					emc.beginTransaction( OkrWorkProcessLink.class );
					emc.beginTransaction( OkrWorkProblemInfo.class );
					emc.beginTransaction( OkrWorkProblemPersonLink.class );
					emc.beginTransaction( OkrWorkProblemProcessLog.class );
					emc.beginTransaction( OkrWorkReportBaseInfo.class );
					emc.beginTransaction( OkrWorkReportDetailInfo.class );
					emc.beginTransaction( OkrWorkReportPersonLink.class );
					emc.beginTransaction( OkrWorkReportProcessLog.class );
					emc.beginTransaction( OkrTask.class );
					emc.beginTransaction( OkrTaskHandled.class );
					emc.beginTransaction( OkrWorkDynamics.class );
					emc.beginTransaction( OkrWorkAuthorizeRecord.class );
					emc.beginTransaction( OkrWorkChat.class );
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkReportByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkReportByWorkId got an exception, workid:" + workId, e1 );
					}
				}

				if( excuteSuccess ){
					try{
						deleteWorkReportDetailByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkReportDetailByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkReportPersonByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkReportPersonByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkReportProcessLogByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkReportProcessLogByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkProblemInfoByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkProblemInfoByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkProblemPersonLinkByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkProblemPersonLinkByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkPersonByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkPersonByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkProcessLinkByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkProcessLinkByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteWorkAuthorizeRecordByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkAuthorizeRecordByWorkId got an exception, workid:" + workId, e1 );
					}
				}
								
				if( excuteSuccess ){
					try{
						deleteWorkChatByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteWorkChatByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteTaskByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteTaskByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				if( excuteSuccess ){
					try{
						deleteTaskHandledByWorkId( workId, emc, true );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system excute method deleteTaskHandledByWorkId got an exception, workid:" + workId, e1 );
					}
				}
				if( excuteSuccess ){
					try{
						emc.remove( okrWorkBaseInfo, CheckRemoveType.all );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system delete okrWorkBaseInfo By WorkId got an exception, workid:" + workId, e1 );
					}
				}
				if( excuteSuccess ){
					try{
						emc.remove( okrWorkDetailInfo, CheckRemoveType.all );
					}catch( Exception e1 ){
						excuteSuccess = false;
						logger.error( "system delete okrWorkDetailInfo By WorkId got an exception, workid:" + workId, e1 );
					}
				}
				
				//不应该删除
				//deleteWorkDynamicsByWorkId( workId, emc, realDelete );
				
				if( excuteSuccess ){
					emc.commit();
				}else{
					emc.rollback();
				}
				
				if( excuteSuccess ){
					emc.beginTransaction( OkrTask.class );
					emc.beginTransaction( OkrTaskHandled.class );
					
					//如果责任人在该中心工作下面没有其他需要负责的工作了，那么需要删除该责任人中心工作的待办
					if( okrWorkBaseInfo.getResponsibilityIdentity() != null ){
						String[] responsibilityIdentities = okrWorkBaseInfo.getResponsibilityIdentity().split(",");
						for( String responsibilityIdentitiy : responsibilityIdentities ){
							//logger.debug(">>>>>>>>>>>>>>>>>>>处理责任者["+responsibilityIdentitiy+"]的待办信息......");
							// 待办删除的功能先不管，用户可以主动提交后删除
							// 先看看该授权者是否仍存在该中心工作的待办，本来就没有待办信息，就不管了
							ids = business.okrTaskFactory().listIdsByTargetActivityAndObjId( "中心工作", okrWorkBaseInfo.getCenterId(), null, responsibilityIdentitiy );
							if ( ids != null && !ids.isEmpty() ) {
								// 判断该中心工作下是否仍有授权者需要部署和拆解的工作， workPerson表，有责任者是授权者记录
								//logger.debug(">>>>>>>>>>>>>>>>>>>查询工作干系人中是否仍有员工["+responsibilityIdentitiy+"]为责任者的信息......");
								ids_work = business.okrWorkPersonFactory().listWorkByCenterAndIdentity( 
										okrWorkBaseInfo.getCenterId(),
										responsibilityIdentitiy,
										"责任者",
										statuses
								);
								//logger.debug(">>>>>>>>>>>>>>>>>>>ids_work =" + ids_work );
								if ( ids_work == null || ids_work.isEmpty() ) {//已经没有需要部署的工作了，需要删除待办并且生成一条已办
									// 删除所有的待办信息
									for ( String _id : ids ) {
										okrTask = emc.find( _id, OkrTask.class );
										if ( okrTask != null ) {
											emc.remove(okrTask, CheckRemoveType.all);
										}
									}
								}
							}
						}
					}
				}
				
				if( excuteSuccess ){
					emc.commit();
				}else{
					emc.rollback();
				}

				if( excuteSuccess ){
					notityDeleteMessage( okrWorkBaseInfo );
				}
				
			} catch ( Exception e ) {
				throw e;
			}
		}
	}
	
	/**
	 * 根据工作信息ID删除所有汇报信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		List<String> ids = business.okrWorkReportBaseInfoFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().list(ids);
			for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : okrWorkReportBaseInfoList ){
				if( realDelete ){
					emc.remove( okrWorkReportBaseInfo, CheckRemoveType.all );
				}else{
					okrWorkReportBaseInfo.setStatus( "已删除" );
					emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
				}
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作问题请示处理记录
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkProblemProcessLogByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkProblemProcessLog> okrWorkProblemProcessLogList = null;
		List<String> ids = business.okrWorkProblemProcessLogFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkProblemProcessLogList = business.okrWorkProblemProcessLogFactory().list(ids);
			for( OkrWorkProblemProcessLog okrWorkProblemProcessLog : okrWorkProblemProcessLogList ){
				if( realDelete ){
					emc.remove( okrWorkProblemProcessLog, CheckRemoveType.all );
				}else{
					okrWorkProblemProcessLog.setStatus( "已删除" );
					emc.check( okrWorkProblemProcessLog, CheckPersistType.all );
				}
			}
		}	
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作动态信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkDynamicsByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkDynamics> okrWorkDynamicsList = null;
		List<String> ids = business.okrWorkDynamicsFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkDynamicsList = business.okrWorkDynamicsFactory().list(ids);
			for( OkrWorkDynamics okrWorkDynamics : okrWorkDynamicsList ){
				if( realDelete ){
					emc.remove( okrWorkDynamics, CheckRemoveType.all );
				}else{
					okrWorkDynamics.setStatus( "已删除" );
					emc.check( okrWorkDynamics, CheckPersistType.all );
				}						
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作交流信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkChatByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkChat> okrWorkChatList = null;
		List<String> ids = business.okrWorkChatFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkChatList = business.okrWorkChatFactory().list(ids);
			for( OkrWorkChat _okrWorkChat : okrWorkChatList ){
				emc.remove( _okrWorkChat, CheckRemoveType.all );
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作待办信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTaskByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrTask> okrTaskList = null;
		List<String> ids = business.okrTaskFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrTaskList = business.okrTaskFactory().list(ids);
			for( OkrTask _okrTask : okrTaskList ){
				emc.remove( _okrTask, CheckRemoveType.all );
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作已办办信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTaskHandledByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrTaskHandled> okrTaskHandledList = null;
		List<String> ids = business.okrTaskHandledFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrTaskHandledList = business.okrTaskHandledFactory().list(ids);
			for( OkrTaskHandled _okrTaskHandled : okrTaskHandledList ){
				emc.remove( _okrTaskHandled, CheckRemoveType.all );
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作授权信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkAuthorizeRecordByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<String> ids = business.okrWorkAuthorizeRecordFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkAuthorizeRecordList = business.okrWorkAuthorizeRecordFactory().list(ids);
			for( OkrWorkAuthorizeRecord okrWorkAuthorizeRecord : okrWorkAuthorizeRecordList ){
				if( realDelete ){
					emc.remove( okrWorkAuthorizeRecord, CheckRemoveType.all );
				}else{
					okrWorkAuthorizeRecord.setStatus( "已删除" );
					emc.check( okrWorkAuthorizeRecord, CheckPersistType.all );
				}
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作处理链信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkProcessLinkByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkProcessLink> okrWorkProcessLinkList = null;
		List<String> ids = business.okrWorkProcessLinkFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkProcessLinkList = business.okrWorkProcessLinkFactory().list(ids);
			for( OkrWorkProcessLink okrWorkProcessLink : okrWorkProcessLinkList ){
				if( realDelete ){
					emc.remove( okrWorkProcessLink, CheckRemoveType.all );
				}else{
					okrWorkProcessLink.setStatus( "已删除" );
					emc.check( okrWorkProcessLink, CheckPersistType.all );
				}
			}
		}	
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作干系人信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkPersonByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkPerson> okrWorkPersonList = null;
		List<String> ids = business.okrWorkPersonFactory().listByWorkId( workId, null );
		if( ids != null && ids.size() > 0 ){
			okrWorkPersonList = business.okrWorkPersonFactory().list(ids);
			for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
				if( realDelete ){
					emc.remove( okrWorkPerson, CheckRemoveType.all );
				}else{
					okrWorkPerson.setStatus( "已删除" );
					emc.check( okrWorkPerson, CheckPersistType.all );
				}
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作问题请示处理链
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkProblemPersonLinkByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkProblemPersonLink> okrWorkProblemPersonLinkList = null;
		List<String> ids = business.okrWorkProblemPersonLinkFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkProblemPersonLinkList = business.okrWorkProblemPersonLinkFactory().list(ids);
			for( OkrWorkProblemPersonLink okrWorkProblemPersonLink : okrWorkProblemPersonLinkList ){
				if( realDelete ){
					emc.remove( okrWorkProblemPersonLink, CheckRemoveType.all );
				}else{
					okrWorkProblemPersonLink.setStatus( "已删除" );
					emc.check( okrWorkProblemPersonLink, CheckPersistType.all );
				}
			}
		}
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有工作问题请示信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkProblemInfoByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkProblemInfo> okrWorkProblemInfoList = null;
		List<String> ids = business.okrWorkProblemInfoFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkProblemInfoList = business.okrWorkProblemInfoFactory().list(ids);
			for( OkrWorkProblemInfo okrWorkProblemInfo : okrWorkProblemInfoList ){
				if( realDelete ){
					emc.remove( okrWorkProblemInfo, CheckRemoveType.all );
				}else{
					okrWorkProblemInfo.setStatus( "已删除" );
					emc.check( okrWorkProblemInfo, CheckPersistType.all );
				}
			}
		}		
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有汇报处理日志信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportProcessLogByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		List<String> ids = business.okrWorkReportProcessLogFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkReportProcessLogList = business.okrWorkReportProcessLogFactory().list(ids);
			for( OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList ){
				if( realDelete ){
					emc.remove( okrWorkReportProcessLog, CheckRemoveType.all );
				}else{
					okrWorkReportProcessLog.setStatus( "已删除" );
					emc.check( okrWorkReportProcessLog, CheckPersistType.all );
				}
			}
		}		
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有汇报处理人信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportPersonByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList = null;
		List<String> ids = business.okrWorkReportPersonLinkFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);
			for( OkrWorkReportPersonLink okrWorkReportPersonLink : okrWorkReportPersonLinkList ){
				if( realDelete ){
					emc.remove( okrWorkReportPersonLink, CheckRemoveType.all );
				}else{
					okrWorkReportPersonLink.setStatus( "已删除" );
					emc.check( okrWorkReportPersonLink, CheckPersistType.all );
				}
			}
		}		
		return true;
	}
	
	/**
	 * 根据工作信息ID删除所有汇报详细信息
	 * @param workId 工作ID
	 * @param emc 数据源
	 * @param realDelete 是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportDetailByWorkId( String workId, EntityManagerContainer emc, boolean realDelete ) throws Exception {
		Business business = new Business( emc );
		List<OkrWorkReportDetailInfo> okrWorkReportDetailInfoList = null;
		List<String> ids = business.okrWorkReportDetailInfoFactory().listByWorkId( workId );
		if( ids != null && ids.size() > 0 ){
			okrWorkReportDetailInfoList = business.okrWorkReportDetailInfoFactory().list( ids );
			for( OkrWorkReportDetailInfo okrWorkReportDetailInfo : okrWorkReportDetailInfoList ){
				if( realDelete ){
					emc.remove( okrWorkReportDetailInfo, CheckRemoveType.all );
				}else{
					okrWorkReportDetailInfo.setStatus( "已删除" );
					emc.check( okrWorkReportDetailInfo, CheckPersistType.all );
				}
			}
		}		
		return true;
	}
	
	
	
	/**
	 * 根据中心工作ID和指定处理人查询是否存在未确认和未拆分并且状态正常的工作基础信息存在，查询ID列表
	 * @param centerId
	 * @param responsibilityEmployeeName
	 * @return
	 * @throws Exception 
	 */
	private List<String> listUnConfirmWorkIdsByCenterAndPerson( String centerId, String userIdentity ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not query!" );
		}
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception( "userNameString is null, system can not query!" );
		}
		Business business = null;
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//先查询中心工作ID和处理人查询与其有关的所有工作ID
			ids = business.okrWorkPersonFactory().listByCenterAndPerson( centerId, userIdentity, null, null );
			//在IDS范围内，查询所有状态正常并且待确认的工作
			ids = business.okrWorkBaseInfoFactory().listUnConfirmWorkIdInIds( ids );
			return ids;
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listNextWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkBaseInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			return business.okrWorkBaseInfoFactory().listNextWithFilter(id, count, sequence, wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listPrevWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkBaseInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			return business.okrWorkBaseInfoFactory().listPrevWithFilter(id, count, sequence, wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 查询符合条件的数据总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 正式部署工作
	 * @param id
	 * @param deployerName
	 * @throws Exception 
	 */
	public void deploy( String centerId, List<String> workIds, String deployerIdentity ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, can not deploy works。" );
		}
//		if( workIds == null || workIds.isEmpty() ){
//			throw new Exception( "workIds is null, can not deploy works。" );
//		}
		if( deployerIdentity == null || deployerIdentity.isEmpty() ){
			throw new Exception( "deployerIdentity is null, can not deploy works。" );
		}
		//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]接受工作部署请求，工作数量：" + workIds.size() );
		//需要维护工作干系人和工作审核链
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkBaseInfo parentWorkBaseInfo = null;
		OkrWorkPerson okrWorkPerson = null;
		List<String> ids = null;
		List<String> userIdentities = null;
		List<String> status = new ArrayList<String>();
		List<OkrWorkPerson> existsWorkPersonList = null;
		List<OkrWorkPerson> existsCenterWorkPersonList = null;
		List<OkrWorkPerson> workPersonList = null;
		List<OkrWorkPerson> centerWorkPersonList = null;
		Integer workProcessLevel = 1;
		String identity = null;//工作干系人处理身份
		String personSplitFlag = ",";
		String workProcessStatus = "执行中";
		String workAdminIdentity = null;
		Business business = null;
		
		status.add( "正常" );
		
		//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]系统获取数据库连接，开启数据库事务......" );
		workPersonList = new ArrayList<OkrWorkPerson>();
		centerWorkPersonList = new ArrayList<OkrWorkPerson>();
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );			
			okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );			
			if( okrCenterWorkInfo == null ){
				throw new Exception( "okrCenterWorkInfo is not exsits{'id':'"+centerId+"'}." );
			}
			
			//从系统设置中查询全局工作管理员身份
			workAdminIdentity = business.okrConfigSystemFactory().getValueWithConfigCode( "REPORT_SUPERVISOR" );
			
			//logger.debug( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
			if( workIds != null && !workIds.isEmpty() ){
				for( String id : workIds ){
					okrWorkBaseInfo = emc.find( id, OkrWorkBaseInfo.class );
					if( okrWorkBaseInfo != null ){					
						//查询该工作的所有干系人列表，供后续组织列表使用
						ids = business.okrWorkPersonFactory().listByWorkId( id, null );
						existsWorkPersonList = business.okrWorkPersonFactory().list( ids );
						
						//查询该‘中心工作’所有的干系人信息
						ids = business.okrWorkPersonFactory().listIdsForCenterWorkByCenterId( centerId, null, null );
						existsCenterWorkPersonList = business.okrWorkPersonFactory().list( ids );
						
						//先把所有的已存在的干系人信息置为“已删除”
						for( OkrWorkPerson tmp_okrWorkPerson : existsWorkPersonList ){
							tmp_okrWorkPerson.setStatus( "已删除" );
						}
						
//						for( OkrWorkPerson tmp_okrWorkPerson : existsCenterWorkPersonList ){
//							tmp_okrWorkPerson.setStatus( "已删除" );
//						}
						
						emc.beginTransaction( OkrCenterWorkInfo.class );
						emc.beginTransaction( OkrWorkBaseInfo.class );
						emc.beginTransaction( OkrWorkPerson.class );
						emc.beginTransaction( OkrWorkProcessLink.class );
						
						//logger.debug( "---------------------------------------------------------------------------------------" );
						//logger.debug( "---- Service:["+ new Date() +"], 部署工作：" + okrWorkBaseInfo.getTitle() );
						//logger.debug( "---------------------------------------------------------------------------------------" );
						
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：工作创建者......" );
						if( okrWorkBaseInfo.getCreatorIdentity() != null && !okrWorkBaseInfo.getCreatorIdentity().isEmpty() ){
							identity = "创建者";
							okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getCreatorIdentity(),  identity );
							if( okrWorkPerson != null ){
								okrWorkPerson.setWorkProcessStatus( workProcessStatus );
								addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
							}
							identity = "观察者";
							okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getCreatorIdentity(), identity );
							if( okrWorkPerson != null ){
								okrWorkPerson.setWorkProcessStatus( workProcessStatus );
								addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
							}
						}
						
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：工作部署者......" );
						if( okrWorkBaseInfo.getDeployerName() != null && !okrWorkBaseInfo.getDeployerName().isEmpty() ){
							identity = "部署者";
							okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getDeployerIdentity(), identity );
							if( okrWorkPerson != null ){
								okrWorkPerson.setWorkProcessStatus( workProcessStatus );
								addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
							}
							identity = "观察者";
							okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getDeployerIdentity(),  identity );
							if( okrWorkPerson != null ){
								okrWorkPerson.setWorkProcessStatus( workProcessStatus );
								addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
							}
						}
						
						String[] employeeIdentities = null;
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：工作责任者......" );
						if( okrWorkBaseInfo.getResponsibilityIdentity() != null && !okrWorkBaseInfo.getResponsibilityIdentity().isEmpty() ){
							//责任者多个值一般使用“,”分隔
							employeeIdentities = okrWorkBaseInfo.getResponsibilityIdentity().split( personSplitFlag );
							if( employeeIdentities != null && employeeIdentities.length > 0 ){
								for( String identityName : employeeIdentities ){
									identity = "责任者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
									identity = "观察者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
								}
							}
						}
						
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：工作协助者......" );
						if( okrWorkBaseInfo.getCooperateIdentity() != null && !okrWorkBaseInfo.getCooperateIdentity().isEmpty() ){
							//协助者多个值一般使用“,”分隔
							employeeIdentities = okrWorkBaseInfo.getCooperateIdentity().split( personSplitFlag );
							if( employeeIdentities != null && employeeIdentities.length > 0 ){
								for( String identityName : employeeIdentities ){
									identity = "协助者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
									identity = "观察者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
								}
							}
						}
						
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：工作阅知者......" );
						if( okrWorkBaseInfo.getReadLeaderIdentity() != null && !okrWorkBaseInfo.getReadLeaderIdentity().isEmpty() ){
							//阅知者多个值一般使用“,”分隔
							employeeIdentities = okrWorkBaseInfo.getReadLeaderIdentity().split( personSplitFlag );
							if( employeeIdentities != null && employeeIdentities.length > 0 ){
								for( String identityName : employeeIdentities ){
									identity = "阅知者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
									identity = "观察者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
								}
							}
						}

						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：工作管理员......" );
						if( workAdminIdentity != null && !workAdminIdentity.isEmpty() ){
							//工作管理员多个值一般使用“,”分隔
							employeeIdentities = workAdminIdentity.split( personSplitFlag );
							if( employeeIdentities != null && employeeIdentities.length > 0 ){
								for( String identityName : employeeIdentities ){
									identity = "观察者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
								}
							}
						}
						
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：工作汇报审批领导......" );
						if( okrCenterWorkInfo.getReportAuditLeaderIdentity() != null && !okrCenterWorkInfo.getReportAuditLeaderIdentity().isEmpty() ){
							//工作汇报审批领导多个值一般使用“,”分隔
							employeeIdentities = okrCenterWorkInfo.getReportAuditLeaderIdentity().split( personSplitFlag );
							if( employeeIdentities != null && employeeIdentities.length > 0 ){
								for( String identityName : employeeIdentities ){
									identity = "观察者";
									okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, identityName, identity );
									if( okrWorkPerson != null ){
										okrWorkPerson.setWorkProcessStatus( workProcessStatus );
										addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
									}
								}
							}
						}
							
						//根据上级工作的审核层级来确认本工作的审核层级，层级加一
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作信息：根据上级工作的审核层级来确认本工作的审核层级，层级加一......" );
						if( okrWorkBaseInfo.getParentWorkId() != null && !okrWorkBaseInfo.getParentWorkId().isEmpty() ){
							parentWorkBaseInfo = emc.find( okrWorkBaseInfo.getParentWorkId(), OkrWorkBaseInfo.class );
							if( parentWorkBaseInfo != null ){
								workProcessLevel = parentWorkBaseInfo.getWorkAuditLevel() + 1;
								okrWorkBaseInfo.setWorkAuditLevel( workProcessLevel );
								// 上级工作的观察者作为本级工作的观察者
								userIdentities = business.okrWorkPersonFactory().listUserIndentityByWorkId( okrWorkBaseInfo.getCenterId(), okrWorkPerson.getParentWorkId(), "观察者", status );
								//logger.debug( ">>>>>>>>>>>>>>>>>上级工作的观察者作为本级工作的观察者["+okrWorkBaseInfo.getTitle()+"]：" );
							}
						}else{
							//logger.debug( ">>>>>>>>>>>>>>>>>无上级工作，中心工作的部署者要作为本级工作的观察者["+okrWorkBaseInfo.getTitle()+"]：" );
							// 无上级工作，中心工作的部署者要作为本级工作的观察者
							// 查询中心工作所有观察者
							userIdentities = business.okrWorkPersonFactory().listUserIdentityForCenterWork( okrWorkBaseInfo.getCenterId(), "部署者", status );
						}						
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护工作干系人信息：上级工作的观察者作为本级工作的观察者......" );
						// 上级工作的观察者作为本级工作的观察者，如果无上级工作，则中心工作的部署者要作为本级工作的观察者
						if ( userIdentities != null && !userIdentities.isEmpty() ) {
							//logger.debug( userIdentities );
							// 判断每一条是否已经在当前工作的观察者，如果不是，则添加为当前工作的观察者
							for ( String userIdentity : userIdentities ) {
								okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo( okrWorkBaseInfo, userIdentity, "观察者" );
								if( okrWorkPerson != null ){
									okrWorkPerson.setWorkProcessStatus( workProcessStatus );
									addOkrWorkPersonToList( okrWorkPerson, workPersonList, existsWorkPersonList );
								}
							}
						}
						
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护中心工作所有的干系人信息：查询是否所有的干系人都已经是‘中心工作’的观察者，执行完成！" );
						//维护中心工作所有的干系人信息，该中心工作下所有的干系人都应该是这个中心工作的观察者
						//如果在已经存在的干系人信息中，仍有已删除的信息，那么说明信息需要删除
						for( OkrWorkPerson _okrWorkPerson : existsWorkPersonList ){
							if( _okrWorkPerson.getStatus().equals( "已删除" )){
								emc.remove( _okrWorkPerson, CheckRemoveType.all );
							}else{
								emc.check( _okrWorkPerson, CheckPersistType.all );
								//创建一个中心工作的观察者信息
								okrWorkPerson = okrWorkPersonService.createWorkPersonByCenterInfo( 
										okrCenterWorkInfo, 
										_okrWorkPerson.getEmployeeName(), 
										_okrWorkPerson.getEmployeeIdentity(), 
										_okrWorkPerson.getOrganizationName(), 
										_okrWorkPerson.getCompanyName(), 
										"观察者", 
										_okrWorkPerson.getCreateTime() 
								);
								okrWorkPerson.setWorkProcessStatus(workProcessStatus);
								addOkrWorkPersonToList( okrWorkPerson, centerWorkPersonList, existsCenterWorkPersonList );
							}
						}

						//对需要存储数据列表里的干系人信息进行存储
						for( OkrWorkPerson forsave_okrWorkPerson : workPersonList ){
							emc.persist( forsave_okrWorkPerson, CheckPersistType.all );
							//将需要存储的工作的干系人都尝试创建中心工作观察者
							okrWorkPerson = okrWorkPersonService.createWorkPersonByCenterInfo( 
									okrCenterWorkInfo, 
									forsave_okrWorkPerson.getEmployeeName(),
									forsave_okrWorkPerson.getEmployeeIdentity(),
									forsave_okrWorkPerson.getOrganizationName(),
									forsave_okrWorkPerson.getCompanyName(),
									"观察者", 
									forsave_okrWorkPerson.getCreateTime()
							);
							okrWorkPerson.setWorkProcessStatus( workProcessStatus );
							addOkrWorkPersonToList( okrWorkPerson, centerWorkPersonList, existsCenterWorkPersonList );
						}
						
						for( OkrWorkPerson fordelete_okrWorkPerson : existsCenterWorkPersonList ){
							if( fordelete_okrWorkPerson.getStatus().equals( "已删除" )){
								emc.remove( fordelete_okrWorkPerson, CheckRemoveType.all );
							}else{
								fordelete_okrWorkPerson.setWorkProcessStatus( workProcessStatus );
								emc.check( fordelete_okrWorkPerson, CheckPersistType.all );
							}
						}
						
						for( OkrWorkPerson forsave_okrWorkPerson : centerWorkPersonList ){
							forsave_okrWorkPerson.setWorkProcessStatus( workProcessStatus );
							emc.persist( forsave_okrWorkPerson, CheckPersistType.all );
						}
						//logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]维护中心工作所有的干系人信息：查询是否所有的干系人都已经是‘中心工作’的观察者......" );
						
						//部署完成后是待员工执行的工作
						okrWorkBaseInfo.setWorkAuditLevel( workProcessLevel );
						okrWorkBaseInfo.setWorkProcessStatus( workProcessStatus );
						okrCenterWorkInfo.setProcessStatus( workProcessStatus );
						
						emc.check( okrCenterWorkInfo, CheckPersistType.all );
						emc.check( okrWorkBaseInfo, CheckPersistType.all );
						emc.commit();
						logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]工作部署数据库事务结束，准备发送所有的系统通知......" );
						notifyWorkDeployMessage( okrWorkBaseInfo );
						logger.debug( ">>>>>>>>>>>>>>>>>>>Service:["+ new Date() +"]所有的工作部署系统通知发送完成，工作部署完成。" );
					}
				}
			}
			logger.debug( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 将工作干系人信息添加到需要添加到数据库的干系人列表里去
	 * @param okrWorkPerson
	 * @param workPersonList
	 */
	private void addOkrWorkPersonToList( OkrWorkPerson okrWorkPerson, List<OkrWorkPerson> needSaveWorkPersonList, List<OkrWorkPerson> existsWorkPersonList ) {
		
		boolean breakout = false;
		
		//看看干系人信息是否已经存在了，如果已经存在则不需要再添加了
		for( OkrWorkPerson _okrWorkPerson : existsWorkPersonList ){
			if( okrWorkPerson.getWorkId() == null || okrWorkPerson.getWorkId().isEmpty() ){
				if( _okrWorkPerson.getCenterId().equalsIgnoreCase( okrWorkPerson.getCenterId() )
				 && _okrWorkPerson.getEmployeeIdentity().equalsIgnoreCase( okrWorkPerson.getEmployeeIdentity() )
				 && _okrWorkPerson.getProcessIdentity().equalsIgnoreCase( okrWorkPerson.getProcessIdentity() )
				 && ( okrWorkPerson.getWorkId() == null || okrWorkPerson.getWorkId().isEmpty() )
				){
					_okrWorkPerson.setStatus( "正常" );
					_okrWorkPerson.setWorkProcessStatus( okrWorkPerson.getWorkProcessStatus() );
					breakout = true;
					break;
				}
			}else{
				if( _okrWorkPerson.getCenterId().equalsIgnoreCase( okrWorkPerson.getCenterId() )
				 && _okrWorkPerson.getEmployeeIdentity().equalsIgnoreCase( okrWorkPerson.getEmployeeIdentity() )
				 && _okrWorkPerson.getProcessIdentity().equalsIgnoreCase( okrWorkPerson.getProcessIdentity() )
				 && _okrWorkPerson.getWorkId() != null 
				 && !_okrWorkPerson.getWorkId().isEmpty() 
				 && _okrWorkPerson.getWorkId().equalsIgnoreCase( okrWorkPerson.getWorkId() )
				){
					_okrWorkPerson.setStatus( "正常" );
					_okrWorkPerson.setWorkProcessStatus( okrWorkPerson.getWorkProcessStatus() );
					breakout = true;
					break;
				}
			}
		}
		
		if( breakout ){
			return;
		}
		
		//再比对是否已经在需要添加保存的干系人信息列表里
		for( OkrWorkPerson _okrWorkPerson : needSaveWorkPersonList ){
			if( okrWorkPerson.getWorkId() == null || okrWorkPerson.getWorkId().isEmpty() ){
				if( _okrWorkPerson.getCenterId().equalsIgnoreCase( okrWorkPerson.getCenterId() )
				 && _okrWorkPerson.getEmployeeIdentity().equalsIgnoreCase( okrWorkPerson.getEmployeeIdentity() )
				 && _okrWorkPerson.getProcessIdentity().equalsIgnoreCase( okrWorkPerson.getProcessIdentity() )
				 && ( okrWorkPerson.getWorkId() == null || okrWorkPerson.getWorkId().isEmpty() )
				){
					breakout = true;
					break;
				}
			}else{
				if( _okrWorkPerson.getCenterId().equalsIgnoreCase( okrWorkPerson.getCenterId() )
				 && _okrWorkPerson.getEmployeeIdentity().equalsIgnoreCase( okrWorkPerson.getEmployeeIdentity() )
				 && _okrWorkPerson.getProcessIdentity().equalsIgnoreCase( okrWorkPerson.getProcessIdentity() )
				 && _okrWorkPerson.getWorkId() != null 
				 && !_okrWorkPerson.getWorkId().isEmpty() 
				 && _okrWorkPerson.getWorkId().equalsIgnoreCase( okrWorkPerson.getWorkId() )
				){
					breakout = true;
					break;
				}
			}
		}
		if( !breakout ){
			needSaveWorkPersonList.add( okrWorkPerson );
		}
	}

	/**
	 * 生成待办信息
	 * @param okrWorkBaseInfo
	 */
	public void createTasks( String centerId,  List<String> workIds, String userIdentity ) throws Exception {
		String splitFlag = ",";
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<String> taskUserIdentityList = new ArrayList<String>();
		List<String> readUserIdentityList = new ArrayList<String>();
		String[] targetIdentityArray = null;
		String targetIdentities = null;
		
		if( workIds != null && !workIds.isEmpty() ){
			okrWorkBaseInfoList = listByIds( workIds );
		}
		
		//生成新的待办信息
		if( okrWorkBaseInfoList != null && okrWorkBaseInfoList.size() > 0 ){			
			for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
				centerId = okrWorkBaseInfo.getCenterId();
				//责任者，需要生成待办，有可能多人
				targetIdentities = okrWorkBaseInfo.getResponsibilityIdentity();
				if( targetIdentities != null && !targetIdentities.isEmpty() ){
					targetIdentityArray = targetIdentities.split( splitFlag );
					if( targetIdentityArray != null && targetIdentityArray.length > 0 ){
						for( String identity : targetIdentityArray ){
							if( !taskUserIdentityList.contains( identity )){
								taskUserIdentityList.add(identity);
							}
						}
					}
				}else{
					throw new Exception( "getResponsibilityIdentity is null, can not create tasks!" );
				}
			}			
		}

		OkrCenterWorkInfo okrCenterWorkInfo = null;
		
		if( centerId != null ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
			}catch( Exception e ){
				logger.error( "okrCenterWorkInfo{'id':'"+centerId+"'} is not exsits!", e);
				throw e;
			}
			if( okrCenterWorkInfo != null ){
				//删除当前处理人的待办信息，并且创建已办信息
				okrTaskService.deleteTask( okrCenterWorkInfo, userIdentity );
				
				//为责任者生成工作确认的待办信息
				if( taskUserIdentityList !=null && !taskUserIdentityList.isEmpty()){
					//logger.debug( "责任者:" + taskUserNameList );
					okrTaskService.createTaskProcessors( okrCenterWorkInfo, taskUserIdentityList );
				}
			}
		}
	}

	/**
	 * 判断一个字符串是否在一个List里已经存在
	 * @param taskUserNameList
	 * @param string
	 * @return
	 */
	private boolean existsInList( List<String> stringList, String string ) {
		if( stringList == null || stringList.size() == 0){
			return false;
		}
		if( string == null || string.isEmpty() ){
			return false;
		}
		for( String value : stringList){
			if( string.equalsIgnoreCase( value )){
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据部署日期，完成时限，汇报周期，汇报日期计算在工作执行期间所有的汇报日期列表
	 * 
	 * @param deployDateStr
	 * @param completeDateLimitStr
	 * @param reportCycle
	 * @param reportDayInCycle
	 * @return
	 * @throws Exception 
	 */
	public String getReportTimeQue( Date deployDate, Date completeDateLimit, String reportCycle, Integer reportDayInCycle, String createTime ) throws Exception {
		List<String> dateStringList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		Date _tmp_date = null;
		if( reportCycle != null && reportCycle.trim().equals( "每月汇报" )){
			int reportDay = 0;
			int dayMaxNumber = 0;
			calendar.setTime( deployDate );
			_tmp_date = calendar.getTime();
			do{
				dayMaxNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); 
				if( dayMaxNumber < reportDayInCycle ){
					reportDay = dayMaxNumber;
				}else{
					reportDay = reportDayInCycle;
				}
				calendar.set( Calendar.DAY_OF_MONTH, reportDay );				
				//判断是否周末
				while( dateOperation.isWeekend( calendar.getTime() ) ){
					calendar.add( Calendar.DATE, 1 );
				}
				if( calendar.getTime().after( deployDate )){
					dateStringList.add( dateOperation.getDateStringFromDate( calendar.getTime(), "yyyy-MM-dd" ) + " " + createTime );
				}
				//判断是否节假日	
				calendar.add( Calendar.MONTH, 1);
				_tmp_date = calendar.getTime();
			}while( _tmp_date.before(completeDateLimit));
		}else if( reportCycle != null && reportCycle.trim().equals( "每周汇报" )){
			int reportDay = 0;
			int dayMaxNumber = 7; //1-SUNDAY, 2-MONDAY, 3-TUESDAY, 4-WENDSDAY, 5-THURSDAY, 6-FRIDAY, 7-SATURDAY
			calendar.setTime( deployDate );
			_tmp_date = calendar.getTime();
			do{
				if( dayMaxNumber < reportDayInCycle ){
					reportDay = dayMaxNumber;
				}else{
					reportDay = reportDayInCycle;
				}
				calendar.set( Calendar.DAY_OF_WEEK, reportDay );				
				//判断是否周末
				while( dateOperation.isWeekend( calendar.getTime() ) ){
					calendar.add( Calendar.DATE, 1 );
				}
				if( calendar.getTime().after( deployDate )){
					dateStringList.add( dateOperation.getDateStringFromDate( calendar.getTime(), "yyyy-MM-dd" ) + " " + createTime );
				}
				//判断是否节假日	
				calendar.add( Calendar.WEEK_OF_YEAR, 1);
				_tmp_date = calendar.getTime();
			}while( _tmp_date.before(completeDateLimit));
		}
		if( dateStringList != null && dateStringList.size() > 0 ){
			for( String dateString : dateStringList ){
				if( sb.toString().trim().length() > 0 ){
					sb.append( ";" + dateString );
				}else{
					sb.append( dateString );
				}
			}
		}		
		return sb.toString();
	}
	
	
	public static void main(String[] args){
		OkrWorkBaseInfoService OkrWorkBaseInfoService = new OkrWorkBaseInfoService();
		DateOperation dateOperation = new DateOperation();
		try {
			String result = OkrWorkBaseInfoService.getReportTimeQue(
					dateOperation.getDateFromString( "2016-01-01" ), 
					dateOperation.getDateFromString( "2016-08-24" ), 
					"每周汇报", 
					7,
					"10:00:00"
			);
			Date nextReportTime = OkrWorkBaseInfoService.getNextReportTime( result , null );
		//	System.out.println( nextReportTime );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据定期汇报时间序列和上一次汇报时间来获取下一次汇报时间
	 * @param reportTimeQue
	 * @param lastReportTime
	 * @return
	 * @throws Exception
	 */
	public Date getNextReportTime( String reportTimeQue, Date lastReportTime ) throws Exception {
		String[] timeArray = null;
		if( reportTimeQue != null && reportTimeQue.trim().length() > 0  ){
			timeArray = reportTimeQue.split( ";" );
			if( timeArray != null && timeArray.length > 0 ){
				for( String reportTime : timeArray ){
					//在现在之后，并且在上一次汇报时间之后
					if( dateOperation.getDateFromString( reportTime ).after( new Date()) ){
						if( lastReportTime == null ){
							return dateOperation.getDateFromString( reportTime );
						}else{
							if(dateOperation.getDateFromString( reportTime ).after( lastReportTime )){
								return dateOperation.getDateFromString( reportTime );
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 强制撤回，不管有没有下级工作信息，根据ID从撤回所有的工作以及全部下级工作
	 * @param id
	 * @throws Exception
	 */
	public void recycleWorkForce( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not recycle any object." );
		}
		List<String> ids = getSubNormalWorkBaseInfoIds( id );
		if( ids != null && ids.size() > 0 ){
			for( String workid : ids ){
				recycleWorkForce( workid );//处理这个工作的下级工作，完成后再处理该工作
				recycleWork( workid );
			}
		}else{
			//已经没有下级工作了，可以进行撤回
			recycleWork( id );
		}
	}
	
	/**
	 * 收回已经部署的工作，如果要收回的工作已经被拆解到下级工作，则不允许收回
	 * 收回工作，其实就是将工作置为已撤回，汇报信息，以及问题请示都不需要变更
	 * @param id
	 * @throws Exception 
	 */
	public void recycleWork( String workId ) throws Exception {
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> subWorkIds = null;
		List<String> taskIds = null;
		List<String> ids = null;
		String[] userIdentityArray = null;
		OkrTask okrTask = null;
		
		if( workId == null || workId.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		subWorkIds = getSubNormalWorkBaseInfoIds( workId );
		if( subWorkIds != null && subWorkIds.size() > 0 ){
			throw new Exception( "该工作存在"+ subWorkIds.size() +"个下级工作，该工作暂无法收回。" );
		}else{//工作可以被撤消
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
				okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
				okrWorkDetailInfo = emc.find( workId, OkrWorkDetailInfo.class );
				
				emc.beginTransaction( OkrWorkBaseInfo.class );
				emc.beginTransaction( OkrWorkDetailInfo.class );
				
				if ( okrWorkBaseInfo != null ) {
					okrWorkBaseInfo.setStatus( "已撤回" );
					emc.check( okrWorkBaseInfo, CheckRemoveType.all );
				}else{
					logger.error( "can not recycle work, okrWorkBaseInfo is not exist {'id':'"+ workId +"'}" );
				}
				
				if ( okrWorkDetailInfo != null ) {
					okrWorkDetailInfo.setStatus( "已撤回" );
					emc.check( okrWorkDetailInfo, CheckRemoveType.all );
				}else{
					logger.error( "can not recycle work, okrWorkDetailInfo is not exist {'id':'"+ workId +"'}" );
				}
				
				if( okrWorkBaseInfo.getResponsibilityEmployeeName() != null && !okrWorkBaseInfo.getResponsibilityEmployeeName().isEmpty() ){
					userIdentityArray = okrWorkBaseInfo.getResponsibilityEmployeeName().split( "," );
					for( String identity : userIdentityArray ){
						//对待办数据进行处理
						taskIds = okrTaskService.listIdsByCenterAndPerson( okrWorkBaseInfo.getCenterId(), identity, "中心工作" );
						//查询该工作的负责人是否有待办信息
						if( taskIds != null && taskIds.size() > 0 ){
							//是否在此中心工作下仍有需要确认和部署的工作
							ids = listUnConfirmWorkIdsByCenterAndPerson( okrWorkBaseInfo.getCenterId(), identity );
							if( ids == null || ids.size() == 0 ){
								//删除待办信息
								for( String taskId : taskIds ){
									okrTask = emc.find( taskId, OkrTask.class );
									if( okrTask != null ){
										emc.remove( okrTask );
									}
								}
							}
						}
					}
				}
				
				if( okrWorkBaseInfo.getCooperateEmployeeName() != null && !okrWorkBaseInfo.getCooperateEmployeeName().isEmpty() ){
					userIdentityArray = okrWorkBaseInfo.getCooperateEmployeeName().split( "," );
					for( String identity : userIdentityArray ){
						//对待办数据进行处理
						taskIds = okrTaskService.listIdsByCenterAndPerson( okrWorkBaseInfo.getCenterId(), identity, "中心工作" );
						//查询该工作的协助人是否有待阅信息
						if( taskIds != null && taskIds.size() > 0 ){
							//是否在此中心工作下仍有需要确认和部署的工作
							ids = listUnConfirmWorkIdsByCenterAndPerson( okrWorkBaseInfo.getCenterId(), identity );
							if( ids == null || ids.size() == 0 ){
								//删除待办信息
								for( String taskId : taskIds ){
									okrTask = emc.find( taskId, OkrTask.class );
									if( okrTask != null ){
										emc.remove( okrTask );
									}
								}
							}
						}
					}
				}
				
				if( okrWorkBaseInfo.getReadLeaderName() != null && !okrWorkBaseInfo.getReadLeaderName().isEmpty() ){
					userIdentityArray = okrWorkBaseInfo.getReadLeaderName().split( "," );
					for( String identity : userIdentityArray ){
						//对待办数据进行处理
						taskIds = okrTaskService.listIdsByCenterAndPerson( okrWorkBaseInfo.getCenterId(), identity, "中心工作" );
						//查询该工作的协助人是否有待阅信息
						if( taskIds != null && taskIds.size() > 0 ){
							//是否在此中心工作下仍有需要确认和部署的工作
							ids = listUnConfirmWorkIdsByCenterAndPerson( okrWorkBaseInfo.getCenterId(), identity );
							if( ids == null || ids.size() == 0 ){
								//删除待办信息
								for( String taskId : taskIds ){
									okrTask = emc.find( taskId, OkrTask.class );
									if( okrTask != null ){
										emc.remove( okrTask );
									}
								}
							}
						}
					}
				}
				
				emc.commit();
				//向工作相关干系人发送消息
				notityRecycleMessage(okrWorkBaseInfo);
				
			} catch ( Exception e ) {
				throw e;
			}
		}
	}

	/**
	 * 发送消息通知
	 * @param okrWorkBaseInfo
	 */
	private void notityRecycleMessage(OkrWorkBaseInfo okrWorkBaseInfo) {
//		try {
//			okrNotifyService.notifyCooperaterForWorkRecycled(okrWorkBaseInfo);
//		} catch (Exception e) {
//			logger.error( "工作撤消成功，向协助者发送消息通知发生异常！", e );
//		}
//		try {
//			okrNotifyService.notifyDeployerForWorkRecycleSuccess(okrWorkBaseInfo);
//		} catch (Exception e) {
//			logger.error( "工作撤消成功，向部署者发送消息通知发生异常！", e );
//		}
//		try {
//			okrNotifyService.notifyResponsibilityForWorkRecycled(okrWorkBaseInfo);
//		} catch (Exception e) {
//			logger.error( "工作撤消成功，向责任者发送消息通知发生异常！", e );
//		}
	}
	
	/**
	 * 发送消息通知
	 * @param okrWorkBaseInfo
	 */
	private void notityDeleteMessage( OkrWorkBaseInfo okrWorkBaseInfo ) {
		try {
			okrNotifyService.notifyCooperaterForWorkDeleted(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.error( "工作删除成功，向协助者发送消息通知发生异常！", e );
		}
		try {
			okrNotifyService.notifyDeployerForWorkDeletedSuccess(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.error( "工作删除成功，向部署者发送消息通知发生异常！", e );
		}
		try {
			okrNotifyService.notifyResponsibilityForWorkDeleted(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.error( "工作删除成功，向责任者发送消息通知发生异常！", e );
		}
	}
	
	/**
	 * 工作部署成功，进行工作消息通知
	 * @param okrWorkBaseInfo
	 */
	private void notifyWorkDeployMessage(OkrWorkBaseInfo okrWorkBaseInfo) {
		//工作部署成功，通知部署者
		try{
			okrNotifyService.notifyDeployerForWorkDeploySuccess( okrWorkBaseInfo );
		}catch(Exception e){
			logger.error( "工作["+ okrWorkBaseInfo.getTitle() +"]部署成功，通知部署者发生异常！" , e );
		}						
		//收到一个新工作，通知责任者
		try{
			okrNotifyService.notifyResponsibilityForGetWork(okrWorkBaseInfo);
		}catch(Exception e){
			logger.error( "工作["+ okrWorkBaseInfo.getTitle() +"]部署成功，通知责任者发生异常！" , e );
		}
		//收到一个新工作，通知协助者
		try{
			okrNotifyService.notifyCooperaterForGetWork(okrWorkBaseInfo);
		}catch(Exception e){
			logger.error( "工作["+ okrWorkBaseInfo.getTitle() +"]部署成功，通知协助者发生异常！" , e );
		}
	}

	/**
	 * 根据用户名称和中心工作ID列示所有工作信息
	 * @param centerId
	 * @param statuses 需要显示的信息状态：正常|已删除
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkBaseInfo> listWorkInCenter( String centerId, List<String> statuses) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		Business business = null;		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listWorkByCenterId( centerId, null, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据用户名称和中心工作ID列示所有与用户有关的工作信息
	 * @param userIdentity
	 * @param centerId
	 * @param statuses 需要显示的信息状态：正常|已删除
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkBaseInfo> listWorkInCenterByIdentity( String userIdentity, String centerId, List<String> statuses) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		Business business = null;
		List<String> ids =  okrWorkPersonService.listDistinctWorkIdsByIdentity( userIdentity, centerId, statuses );		
		if( ids != null && !ids.isEmpty()){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				return business.okrWorkBaseInfoFactory().list(ids);
			} catch ( Exception e ) {
				throw e;
			}
		}
		return null;
	}
	
	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listWorkNextWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "WORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkBaseInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listNextWithFilter(id, count, sequence, wrapIn);
			if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					okrWorkBaseInfo = emc.find( okrWorkPerson.getWorkId(), OkrWorkBaseInfo.class );
					if( okrWorkBaseInfo != null && !okrWorkBaseInfoList.contains( okrWorkBaseInfo )){
						okrWorkBaseInfoList.add( okrWorkBaseInfo );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkBaseInfoList;
	}
	
	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkBaseInfo> listWorkPrevWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "WORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase( id, HttpAttribute.x_empty_symbol )) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkBaseInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listPrevWithFilter(id, count, sequence, wrapIn);
			if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					okrWorkBaseInfo = emc.find( okrWorkPerson.getCenterId(), OkrWorkBaseInfo.class );
					if( okrWorkBaseInfo != null && !okrWorkBaseInfoList.contains( okrWorkBaseInfo )){
						okrWorkBaseInfoList.add( okrWorkBaseInfo );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkBaseInfoList;
	}
	
	/**
	 * 查询符合条件的数据总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getWorkCountWithFilter( com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "WORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 查询需要立即进行汇报的工作ID
	 * @return
	 * @throws Exception 
	 */
	public List<String> listNeedReportWorkIds() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listNeedReportWorkIds();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByParentId( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listByParentId( id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据分析时间来查询需要进行进展分析的工作ID列表
	 * @param report_progress
	 * @param count
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdsForNeedProgressAnalyse( String report_progress, int count ) throws Exception {
		if( report_progress == null || report_progress.isEmpty() ){
			throw new Exception( "report_progress is null." );
		}
		if( count == 0 ){
			throw new Exception( "count is 0." );
		}
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listIdsForNeedProgressAnalyse( report_progress, count );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据指定的工作ID，进度汇报设置和进度分析时间来进行工作分析，并且更新进度、完成情况以及分析时间信息
	 * @param id
	 * @param report_progress
	 * @param analyse_time_flag
	 * @throws Exception 
	 */
	public void analyseWorkProgress( String id, String report_progress, String nowDateTime ) throws Exception {
		logger.debug( "Okr system analyse work{'id':'"+id+"', 'report_progress':'"+report_progress+"', 'time_flag':'"+nowDateTime+"' }" );
		if( "OPEN".equals( report_progress.toUpperCase() )){
			//汇报时需要进行工作进度和是否已完成的汇报。
			analyseWorkProgressFromReports( id, nowDateTime );
		}else{
			//根据工作的部署时间，完成时限和当前时间来进行工作完成度的计算
			analyseWorkProgressFromProcessTimeLimit( id, nowDateTime );
		}
	}
	
	/**
	 * 汇报时需要进行工作进度和是否已完成的汇报。
	 * @param workId
	 * @param analyse_time_flag
	 * @throws Exception 
	 */
	private void analyseWorkProgressFromReports( String workId, String analyse_time_flag ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null." );
		}
		if( analyse_time_flag == null || analyse_time_flag.isEmpty() ){
			throw new Exception( "analyse_time_flag is null." );
		}
		//取到该工作最后一次，并且已经提交的汇报的内容
		//根据汇报内容来确定该工作的进度情况。
		Business business = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkPerson okrWorkPerson = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		statuses.add( "正常" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//查询工作对象
			okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
			if( okrWorkBaseInfo != null ){
				okrWorkReportBaseInfo = business.okrWorkReportBaseInfoFactory().getLastSubmitReport( workId );
			}
			if( okrWorkReportBaseInfo != null ){
				okrWorkBaseInfo.setIsCompleted( okrWorkReportBaseInfo.getIsWorkCompleted() );
				if( okrWorkReportBaseInfo.getIsWorkCompleted() ){
					//已经完成
					okrWorkBaseInfo.setOverallProgress( 1.0 );
					//修改所有干系人信息状态，从执行中修改为已完成，已删除的不要修改
					ids = business.okrWorkPersonFactory().listByWorkId( workId, statuses );
					if( ids != null && !ids.isEmpty() ){
						for( String id : ids ){
							okrWorkPerson = emc.find( id, OkrWorkPerson.class );
							okrWorkPerson.setWorkProcessStatus( "已完成" );
							okrWorkPerson.setIsCompleted( true );
							emc.check( okrWorkPerson, CheckPersistType.all );
						}
					}
				}else{
					okrWorkBaseInfo.setOverallProgress( okrWorkReportBaseInfo.getProgressPercent() );
					//判断是否已经超时
					if( okrWorkBaseInfo.getCompleteDateLimit().before( new Date() )){
						okrWorkBaseInfo.setIsOverTime( true );
						//修改所有干系人信息状态，从执行中修改为已超时，已删除的不要修改
						ids = business.okrWorkPersonFactory().listByWorkId( workId, statuses );
						if( ids != null && !ids.isEmpty() ){
							for( String id : ids ){
								okrWorkPerson = emc.find( id, OkrWorkPerson.class );
								okrWorkPerson.setWorkProcessStatus( "执行中" );
								okrWorkPerson.setIsCompleted( false );
								okrWorkPerson.setIsOverTime( true );
								emc.check( okrWorkPerson, CheckPersistType.all );
							}
						}
					}
				}
			}else{
				//还没有开始汇报
				okrWorkBaseInfo.setIsCompleted( false );
				okrWorkBaseInfo.setOverallProgress( 0.0 );
				//判断是否已经超时
				if( okrWorkBaseInfo.getCompleteDateLimit().before( new Date() )){
					okrWorkBaseInfo.setIsOverTime( true );
					//修改所有干系人信息状态，从执行中修改为已超时，已删除的不要修改
					ids = business.okrWorkPersonFactory().listByWorkId( workId, statuses );
					if( ids != null && !ids.isEmpty() ){
						for( String id : ids ){
							okrWorkPerson = emc.find( id, OkrWorkPerson.class );
							okrWorkPerson.setWorkProcessStatus( "执行中" );
							okrWorkPerson.setIsCompleted( false );
							okrWorkPerson.setIsOverTime( true );
							emc.check( okrWorkPerson, CheckPersistType.all );
						}
					}
				}
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据工作的部署时间，完成时限和当前时间来进行工作完成度的计算
	 * @param workId
	 * @param analyse_time_flag
	 * @throws Exception 
	 */
	private void analyseWorkProgressFromProcessTimeLimit( String workId, String analyse_time_flag ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null." );
		}
		if( analyse_time_flag == null || analyse_time_flag.isEmpty() ){
			throw new Exception( "analyse_time_flag is null." );
		}
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkPerson okrWorkPerson = null;
		Business business = null;
		Double completePercent = 0.0;
		String deployDateString = null;
		Date startDateTime = null, processDateLimit = null, nowDate = new Date();
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		statuses.add( "正常" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//查询工作对象
			okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
			if( okrWorkBaseInfo != null ){
				emc.beginTransaction( OkrWorkBaseInfo.class );
				emc.beginTransaction( OkrWorkPerson.class );
				
				processDateLimit = okrWorkBaseInfo.getCompleteDateLimit();
				if( processDateLimit == null ){
					throw new Exception( "work process date limit is null, system can not analyse work progress from process time limit." );
				}
				
				deployDateString = okrWorkBaseInfo.getDeployDateStr();
				if( deployDateString == null || deployDateString.isEmpty() ){
					throw new Exception( "work deploy date string is null, system can not analyse work progress from process time limit." );
				}
				try{
					startDateTime = dateOperation.getDateFromString( deployDateString );
				}catch(Exception e ){
					logger.error( "work deploy date string is not date style[deployDateString="+ deployDateString +"], system can not analyse work progress from process time limit." );
					throw e;
				}
				//根据部署时间，当前时间和结束时间进行进度计算
				if( processDateLimit.before( nowDate )){
					//处理时间已经耗尽，工作已经完成
					okrWorkBaseInfo.setIsCompleted( true );
					okrWorkBaseInfo.setOverallProgress( 1.0 );
					//修改所有干系人信息状态，从执行中修改为已完成，已删除的不要修改
					ids = business.okrWorkPersonFactory().listByWorkId( workId, statuses );
					if( ids != null && !ids.isEmpty() ){
						for( String id : ids ){
							okrWorkPerson = emc.find( id, OkrWorkPerson.class );
							okrWorkPerson.setWorkProcessStatus( "已完成" );
							okrWorkPerson.setIsCompleted( true );
							emc.check( okrWorkPerson, CheckPersistType.all );
						}
					}
				}else{
					//计算完成百分比
				//	logger.debug( "计算式： ((double)( " +nowDate.getTime()+" - " +startDateTime.getTime()+") /(double)( "+processDateLimit.getTime()+" - "+startDateTime.getTime()+"));" );
					completePercent =  ((double)( nowDate.getTime() - startDateTime.getTime()) /(double)( processDateLimit.getTime() - startDateTime.getTime()));
				//	logger.debug( "计算结果：completePercent=" + completePercent );
					okrWorkBaseInfo.setIsCompleted( false );
					okrWorkBaseInfo.setOverallProgress( completePercent );
				}
				okrWorkBaseInfo.setProgressAnalyseTime( analyse_time_flag );
				emc.check( okrWorkBaseInfo, CheckPersistType.all );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getWorkTotalByCenterId( String centerId, List<String> status) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getWorkTotalByCenterId( centerId, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getProcessingWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getProcessingWorkCountByCenterId( centerId, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getCompletedWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getCompletedWorkCountByCenterId( centerId, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getOvertimeWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getOvertimeWorkCountByCenterId( centerId, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getDraftWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getDraftWorkCountByCenterId( centerId, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 判断用户是否存在未提交的汇报数据
	 * @param workId
	 * @param activityName
	 * @param processStatus
	 * @param processIdentity
	 * @return
	 * @throws Exception
	 */
	public Boolean hasNoneSubmitReport( String workId, String activityName, String processStatus, String processIdentity ) throws Exception {
		Business business = null;	
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.okrWorkReportBaseInfoFactory().listByWorkId( workId, activityName, processStatus, processIdentity );
			if( ids != null && !ids.isEmpty() ){
				return true;
			}else{
				return false;
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 查询所有未完成工作列表
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkBaseInfo> listAllProcessingWorks() throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listAllProcessingWorks();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrWorkBaseInfo> listAllWorks( String centerId, String status ) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			return business.okrWorkBaseInfoFactory().listAllWorks( centerId, status );
		} catch ( Exception e ) {
			throw e;
		}
	}
}