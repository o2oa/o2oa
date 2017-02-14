package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.project.server.StorageMapping;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilter;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInOkrCenterWorkInfo;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrCenterWorkReportStatistic;
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

public class OkrCenterWorkInfoService {
	
	private Logger logger = LoggerFactory.getLogger( OkrCenterWorkInfoService.class );
	private BeanCopyTools<WrapInOkrCenterWorkInfo, OkrCenterWorkInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrCenterWorkInfo.class, OkrCenterWorkInfo.class, null, WrapInOkrCenterWorkInfo.Excludes );
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	/**
	 * 根据传入的ID从数据库查询OkrCenterWorkInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrCenterWorkInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			return emc.find( id, OkrCenterWorkInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 向数据库保存OkrCenterWorkInfo对象
	 * @param wrapIn
	 */
	public OkrCenterWorkInfo save( WrapInOkrCenterWorkInfo wrapIn ) throws Exception {
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrWorkPerson> okrWorkPersonList = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		List<String> ids = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
				okrCenterWorkInfo =  emc.find( wrapIn.getId(), OkrCenterWorkInfo.class );
			}
			emc.beginTransaction( OkrWorkPerson.class );
			emc.beginTransaction( OkrCenterWorkInfo.class );
			emc.beginTransaction( OkrWorkBaseInfo.class );
			//保存中心工作内容
			if( okrCenterWorkInfo == null ){//保存新的中心工作信息
				okrCenterWorkInfo = new OkrCenterWorkInfo();
				wrapin_copier.copy( wrapIn, okrCenterWorkInfo );
				okrCenterWorkInfo.setId( wrapIn.getId() ); //使用参数传入的ID作为记录的ID
				emc.persist( okrCenterWorkInfo, CheckPersistType.all);
			}else{//更新中心工作信息	
				//如果当前的标题和原来的标题不一致，那么需要修改所有具体工作项目的中心工作标题
				if( !wrapIn.getTitle().equals( okrCenterWorkInfo.getTitle() )){
					ids = business.okrWorkBaseInfoFactory().listByCenterWorkId( okrCenterWorkInfo.getId(), null );
					if( ids != null && ids.size() > 0 ){
						for( String id : ids ){
							okrWorkBaseInfo = emc.find( id, OkrWorkBaseInfo.class );
							if( okrWorkBaseInfo != null ){
								okrWorkBaseInfo.setCenterTitle( wrapIn.getTitle() );
								emc.check( okrWorkBaseInfo, CheckPersistType.all );	
							}
						}
					}
				}
				wrapin_copier.copy( wrapIn, okrCenterWorkInfo );
				emc.check( okrCenterWorkInfo, CheckPersistType.all );	
			}
			//删除原先所有的干系人信息，先查询该中心工作信息注册的所有干系人信息列表
			ids = okrWorkPersonService.listIdsForCenterWorkByCenterId( okrCenterWorkInfo.getId(), null );
			okrWorkPersonList = business.okrWorkPersonFactory().list( ids );
			if( okrWorkPersonList != null && okrWorkPersonList.size() > 0 ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					emc.remove( okrWorkPerson, CheckRemoveType.all );
				}
			}			
			//保存中心工作的干系人信息，先根据中心工作信息来获取工作所有的干系人对象信息
			okrWorkPersonList = okrWorkPersonService.getWorkPersonListByCenterWorkInfo( okrCenterWorkInfo );
			if( okrWorkPersonList != null && okrWorkPersonList.size() > 0 ){				
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					okrWorkPerson.setWorkProcessStatus( "草稿" );
					emc.persist( okrWorkPerson, CheckPersistType.all);	
				}
			}			
			emc.commit();
		}catch( Exception e ){
			logger.error( "OkrCenterWorkInfo update/ get a error!" );
			throw e;
		}		
		return okrCenterWorkInfo;
	}

	/**
	 * 根据ID从数据库中删除OkrCenterWorkInfo对象
	 * 同时删除所有的下级工作以及工作的相关汇报，请示等等
	 * @param id
	 * @throws Exception
	 */
	public void delete( String centerId ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			logger.error( "centerId is null, system can not delete any object." );
		}
		List<String> ids = null;
		List<String> del_attachmentIds = new ArrayList<String>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrAttachmentFileInfo attachment = null;
		StorageMapping mapping = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<OkrWorkDetailInfo> okrWorkDetailInfoList = null;
		List<OkrWorkPerson> okrWorkPersonList = null;
		List<OkrWorkProcessLink> okrWorkProcessLinkList = null;
		List<OkrWorkProblemInfo> okrWorkProblemInfoList  = null;
		List<OkrWorkProblemPersonLink> okrWorkProblemPersonLinkList = null;
		List<OkrWorkProblemProcessLog> okrWorkProblemProcessLogList  = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList  = null;
		List<OkrWorkReportDetailInfo> okrWorkReportDetailInfoList = null;
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList  = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		List<OkrWorkDynamics> okrWorkDynamicsList = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<OkrTaskHandled> okrTaskHandledList = null;
		List<OkrTask> okrTaskList = null;
		List<OkrWorkChat> okrWorkChatList = null;
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic = null;
		Business business = null;
		//开始删除一个中心工作信息
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
			emc.beginTransaction( OkrAttachmentFileInfo.class );
			emc.beginTransaction( OkrCenterWorkInfo.class );
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
			emc.beginTransaction( OkrWorkChat.class );
			emc.beginTransaction( OkrWorkDynamics.class );
			emc.beginTransaction( OkrWorkAuthorizeRecord.class );
			emc.beginTransaction( OkrCenterWorkReportStatistic.class );
			
			if ( null != okrCenterWorkInfo ) {
				if( okrCenterWorkInfo.getAttachmentList() != null && !okrCenterWorkInfo.getAttachmentList().isEmpty() ){
					for( String id : okrCenterWorkInfo.getAttachmentList() ){
						del_attachmentIds.add( id );
					}
				}
				emc.remove( okrCenterWorkInfo, CheckRemoveType.all );
			}	
			
			//删除所有与中心工作有关的待办信息，已办信息
			ids = business.okrTaskFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrTaskList = business.okrTaskFactory().list(ids);
				for( OkrTask okrTask : okrTaskList ){
					if( okrTask != null ){
						emc.remove( okrTask, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrTaskHandledFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrTaskHandledList = business.okrTaskHandledFactory().list(ids);
				for( OkrTaskHandled okrTaskHandled : okrTaskHandledList ){
					if( okrTaskHandled != null ){
						emc.remove( okrTaskHandled, CheckRemoveType.all );
					}
				}
			}
			//删除所有与中心工作有关的问题请示信息
			ids = business.okrWorkProblemProcessLogFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProblemProcessLogList = business.okrWorkProblemProcessLogFactory().list(ids);
				for( OkrWorkProblemProcessLog okrWorkProblemProcessLog : okrWorkProblemProcessLogList ){
					if( okrWorkProblemProcessLog != null ){
						emc.remove( okrWorkProblemProcessLog, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkProblemPersonLinkFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProblemPersonLinkList = business.okrWorkProblemPersonLinkFactory().list(ids);
				for( OkrWorkProblemPersonLink okrWorkProblemPersonLink : okrWorkProblemPersonLinkList ){
					if( okrWorkProblemPersonLink != null ){
						emc.remove( okrWorkProblemPersonLink, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkProblemInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProblemInfoList = business.okrWorkProblemInfoFactory().list(ids);
				for( OkrWorkProblemInfo okrWorkProblemInfo : okrWorkProblemInfoList ){
					if( okrWorkProblemInfo != null ){
						emc.remove( okrWorkProblemInfo, CheckRemoveType.all );
					}
				}
			}
			//删除所有与中心工作有关的工作汇报信息
			ids = business.okrWorkReportProcessLogFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportProcessLogList = business.okrWorkReportProcessLogFactory().list(ids);
				for( OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList ){
					if( okrWorkReportProcessLog != null ){
						emc.remove( okrWorkReportProcessLog, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkReportPersonLinkFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);
				for( OkrWorkReportPersonLink okrWorkReportPersonLink : okrWorkReportPersonLinkList ){
					if( okrWorkReportPersonLink != null ){
						emc.remove( okrWorkReportPersonLink, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkReportDetailInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportDetailInfoList = business.okrWorkReportDetailInfoFactory().list(ids);
				for( OkrWorkReportDetailInfo okrWorkReportDetailInfo : okrWorkReportDetailInfoList ){
					if( okrWorkReportDetailInfo != null ){
						emc.remove( okrWorkReportDetailInfo, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkReportBaseInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().list(ids);
				for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : okrWorkReportBaseInfoList ){
					if( okrWorkReportBaseInfo != null ){
						if( okrWorkReportBaseInfo.getAttachmentList() != null && !okrWorkReportBaseInfo.getAttachmentList().isEmpty() ){
							for( String id : okrWorkReportBaseInfo.getAttachmentList() ){
								del_attachmentIds.add( id );
							}
						}
						emc.remove( okrWorkReportBaseInfo, CheckRemoveType.all );
					}
				}
			}
			//删除所有与中心工作有关的工作信息
			ids = business.okrWorkPersonFactory().listByCenterWorkId( centerId, null );
			if( ids != null && ids.size() > 0 ){
				okrWorkPersonList = business.okrWorkPersonFactory().list(ids);
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					if( okrWorkPerson != null ){
						emc.remove( okrWorkPerson, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkProcessLinkFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProcessLinkList = business.okrWorkProcessLinkFactory().list(ids);
				for( OkrWorkProcessLink okrWorkProcessLink : okrWorkProcessLinkList ){
					if( okrWorkProcessLink != null ){
						emc.remove( okrWorkProcessLink, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkDetailInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkDetailInfoList = business.okrWorkDetailInfoFactory().list(ids);
				for( OkrWorkDetailInfo okrWorkDetailInfo : okrWorkDetailInfoList ){
					if( okrWorkDetailInfo != null ){
						emc.remove( okrWorkDetailInfo, CheckRemoveType.all );
					}
				}
			}
			ids = business.okrWorkBaseInfoFactory().listByCenterWorkId( centerId, null );
			if( ids != null && ids.size() > 0 ){
				okrWorkBaseInfoList = business.okrWorkBaseInfoFactory().list(ids);
				for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
					if( okrWorkBaseInfo != null ){
						if( okrWorkBaseInfo.getAttachmentList() != null && !okrWorkBaseInfo.getAttachmentList().isEmpty() ){
							for( String id : okrWorkBaseInfo.getAttachmentList() ){
								del_attachmentIds.add( id );
							}
						}
						emc.remove( okrWorkBaseInfo, CheckRemoveType.all );
					}
				}
			}
			//删除所有与中心工作有关的工作授权记录
			ids = business.okrWorkAuthorizeRecordFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkAuthorizeRecordList = business.okrWorkAuthorizeRecordFactory().list(ids);
				for( OkrWorkAuthorizeRecord okrWorkAuthorizeRecord : okrWorkAuthorizeRecordList ){
					if( okrWorkAuthorizeRecord != null ){
						emc.remove( okrWorkAuthorizeRecord, CheckRemoveType.all );
					}
				}
			}
			//删除所有与中心工作有关的工作动态信息
			ids = business.okrWorkDynamicsFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkDynamicsList = business.okrWorkDynamicsFactory().list(ids);
				for( OkrWorkDynamics okrWorkDynamics : okrWorkDynamicsList ){
					if( okrWorkDynamics != null ){
						emc.remove( okrWorkDynamics, CheckRemoveType.all );
					}
				}
			}
			//删除所有与中心工作有关的工作交流信息
			ids = business.okrWorkChatFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkChatList = business.okrWorkChatFactory().list(ids);
				for( OkrWorkChat okrWorkChat : okrWorkChatList ){
					if( okrWorkChat != null ){
						emc.remove( okrWorkChat, CheckRemoveType.all );
					}
				}
			}
			//删除所有与中心工作有关的汇报统计信息
			ids = business.okrCenterWorkReportStatisticFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrCenterWorkReportStatistic = emc.find( id, OkrCenterWorkReportStatistic.class );
					if( okrCenterWorkReportStatistic != null ){
						emc.remove( okrCenterWorkReportStatistic, CheckRemoveType.all );
					}
				}
			}
			if( del_attachmentIds != null && !del_attachmentIds.isEmpty() ){
				for( String id : del_attachmentIds ){
					attachment = emc.find( id, OkrAttachmentFileInfo.class );
					if( attachment != null ){
						mapping = ThisApplication.storageMappings.get( StorageType.okr, attachment.getStorage() );
						attachment.deleteContent( mapping );
						emc.remove( attachment, CheckRemoveType.all );
					}
				}
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据ID从归档OkrCenterWorkInfo对象
	 * 同时归档所有的下级工作以及工作的相关汇报，请示等等
	 * 并且删除所有待办
	 * @param id
	 * @throws Exception
	 */
	public void archive( String centerId ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			logger.error( "centerId is null, system can not archive any object." );
		}
		List<String> ids = null;
		List<String> del_attachmentIds = new ArrayList<String>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrAttachmentFileInfo attachment = null;
		StorageMapping mapping = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<OkrWorkDetailInfo> okrWorkDetailInfoList = null;
		List<OkrWorkPerson> okrWorkPersonList = null;
		List<OkrWorkProcessLink> okrWorkProcessLinkList = null;
		List<OkrWorkProblemInfo> okrWorkProblemInfoList  = null;
		List<OkrWorkProblemPersonLink> okrWorkProblemPersonLinkList = null;
		List<OkrWorkProblemProcessLog> okrWorkProblemProcessLogList  = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList  = null;
		List<OkrWorkReportDetailInfo> okrWorkReportDetailInfoList = null;
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList  = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		List<OkrWorkDynamics> okrWorkDynamicsList = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<OkrTask> okrTaskList = null;
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic = null;
		Business business = null;
		//开始归档一个中心工作信息
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
			emc.beginTransaction( OkrAttachmentFileInfo.class );
			emc.beginTransaction( OkrCenterWorkInfo.class );
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
			emc.beginTransaction( OkrWorkChat.class );
			emc.beginTransaction( OkrWorkDynamics.class );
			emc.beginTransaction( OkrWorkAuthorizeRecord.class );
			emc.beginTransaction( OkrCenterWorkReportStatistic.class );
			if ( null != okrCenterWorkInfo ) {
				if( "草稿".equals( okrCenterWorkInfo.getProcessStatus() )){
					if( okrCenterWorkInfo.getAttachmentList() != null && !okrCenterWorkInfo.getAttachmentList().isEmpty() ){
						for( String id : okrCenterWorkInfo.getAttachmentList() ){
							del_attachmentIds.add( id );
						}
					}
					emc.remove( okrCenterWorkInfo, CheckRemoveType.all );
				}else{
					okrCenterWorkInfo.setStatus( "已归档" );
					emc.check( okrCenterWorkInfo, CheckPersistType.all );
				}
			}
			ids = business.okrWorkProblemInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProblemInfoList = business.okrWorkProblemInfoFactory().list(ids);
				for( OkrWorkProblemInfo okrWorkProblemInfo : okrWorkProblemInfoList ){
					if( okrWorkProblemInfo != null ){
						if( "草稿".equals( okrWorkProblemInfo.getProcessStatus() )){
							emc.remove( okrWorkProblemInfo, CheckRemoveType.all );
						}else{
							okrWorkProblemInfo.setStatus( "已归档" );
							emc.check( okrWorkProblemInfo, CheckPersistType.all );
						}
					}
				}
			}
			ids = business.okrWorkReportPersonLinkFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);
				for( OkrWorkReportPersonLink okrWorkReportPersonLink : okrWorkReportPersonLinkList ){
					if( okrWorkReportPersonLink != null ){
						if( "草稿".equals( okrWorkReportPersonLink.getProcessStatus() )){
							emc.remove( okrWorkReportPersonLink, CheckRemoveType.all );
						}else{
							okrWorkReportPersonLink.setStatus( "已归档" );
							emc.check( okrWorkReportPersonLink, CheckPersistType.all );
						}
					}
				}
			}
			ids = business.okrWorkReportBaseInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().list(ids);
				for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : okrWorkReportBaseInfoList ){
					if( okrWorkReportBaseInfo != null ){
						if( "草稿".equals( okrWorkReportBaseInfo.getProcessStatus() )){
							if( okrWorkReportBaseInfo.getAttachmentList() != null && !okrWorkReportBaseInfo.getAttachmentList().isEmpty() ){
								for( String id : okrWorkReportBaseInfo.getAttachmentList() ){
									del_attachmentIds.add( id );
								}
							}
							emc.remove( okrWorkReportBaseInfo, CheckRemoveType.all );
						}else{
							okrWorkReportBaseInfo.setStatus( "已归档" );
							emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
						}
					}
				}
			}
			ids = business.okrWorkBaseInfoFactory().listByCenterWorkId( centerId, null );
			if( ids != null && ids.size() > 0 ){
				okrWorkBaseInfoList = business.okrWorkBaseInfoFactory().list(ids);
				for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
					if( okrWorkBaseInfo != null ){
						if( "草稿".equals( okrWorkBaseInfo.getWorkProcessStatus() )){
							if( okrWorkBaseInfo.getAttachmentList() != null && !okrWorkBaseInfo.getAttachmentList().isEmpty() ){
								for( String id : okrWorkBaseInfo.getAttachmentList() ){
									del_attachmentIds.add( id );
								}
							}
							emc.remove( okrWorkBaseInfo, CheckRemoveType.all );
						}else{
							okrWorkBaseInfo.setStatus( "已归档" );
							emc.check( okrWorkBaseInfo, CheckPersistType.all );
						}
					}
				}
			}
			ids = business.okrWorkProblemProcessLogFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProblemProcessLogList = business.okrWorkProblemProcessLogFactory().list(ids);
				for( OkrWorkProblemProcessLog okrWorkProblemProcessLog : okrWorkProblemProcessLogList ){
					if( okrWorkProblemProcessLog != null ){
						okrWorkProblemProcessLog.setStatus( "已归档" );
						emc.check( okrWorkProblemProcessLog, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkProblemPersonLinkFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProblemPersonLinkList = business.okrWorkProblemPersonLinkFactory().list(ids);
				for( OkrWorkProblemPersonLink okrWorkProblemPersonLink : okrWorkProblemPersonLinkList ){
					if( okrWorkProblemPersonLink != null ){
						okrWorkProblemPersonLink.setStatus( "已归档" );
						emc.check( okrWorkProblemPersonLink, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkReportProcessLogFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportProcessLogList = business.okrWorkReportProcessLogFactory().list(ids);
				for( OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList ){
					if( okrWorkReportProcessLog != null ){
						okrWorkReportProcessLog.setStatus( "已归档" );
						emc.check( okrWorkReportProcessLog, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkReportDetailInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkReportDetailInfoList = business.okrWorkReportDetailInfoFactory().list(ids);
				for( OkrWorkReportDetailInfo okrWorkReportDetailInfo : okrWorkReportDetailInfoList ){
					if( okrWorkReportDetailInfo != null ){
						okrWorkReportDetailInfo.setStatus( "已归档" );
						emc.check( okrWorkReportDetailInfo, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkPersonFactory().listByCenterWorkId( centerId, null );
			if( ids != null && ids.size() > 0 ){
				okrWorkPersonList = business.okrWorkPersonFactory().list(ids);
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					if( okrWorkPerson != null ){
						okrWorkPerson.setStatus( "已归档" );
						emc.check( okrWorkPerson, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkProcessLinkFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkProcessLinkList = business.okrWorkProcessLinkFactory().list(ids);
				for( OkrWorkProcessLink okrWorkProcessLink : okrWorkProcessLinkList ){
					if( okrWorkProcessLink != null ){
						okrWorkProcessLink.setStatus( "已归档" );
						emc.check( okrWorkProcessLink, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkDetailInfoFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkDetailInfoList = business.okrWorkDetailInfoFactory().list(ids);
				for( OkrWorkDetailInfo okrWorkDetailInfo : okrWorkDetailInfoList ){
					if( okrWorkDetailInfo != null ){
						okrWorkDetailInfo.setStatus( "已归档" );
						emc.check( okrWorkDetailInfo, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkAuthorizeRecordFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkAuthorizeRecordList = business.okrWorkAuthorizeRecordFactory().list(ids);
				for( OkrWorkAuthorizeRecord okrWorkAuthorizeRecord : okrWorkAuthorizeRecordList ){
					if( okrWorkAuthorizeRecord != null ){
						okrWorkAuthorizeRecord.setStatus( "已归档" );
						emc.check( okrWorkAuthorizeRecord, CheckPersistType.all );
					}
				}
			}
			ids = business.okrWorkDynamicsFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrWorkDynamicsList = business.okrWorkDynamicsFactory().list(ids);
				for( OkrWorkDynamics okrWorkDynamics : okrWorkDynamicsList ){
					if( okrWorkDynamics != null ){
						okrWorkDynamics.setStatus( "已归档" );
						emc.check( okrWorkDynamics, CheckPersistType.all );
					}
				}
			}
			ids = business.okrCenterWorkReportStatisticFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrCenterWorkReportStatistic = emc.find( id, OkrCenterWorkReportStatistic.class );
					if( okrCenterWorkReportStatistic != null ){
						okrCenterWorkReportStatistic.setStatus( "已归档" );
						emc.check( okrCenterWorkReportStatistic, CheckPersistType.all );
					}
				}
			}
			ids = business.okrTaskFactory().listByCenterWorkId( centerId );
			if( ids != null && ids.size() > 0 ){
				okrTaskList = business.okrTaskFactory().list(ids);
				for( OkrTask okrTask : okrTaskList ){
					if( okrTask != null ){
						emc.remove( okrTask, CheckRemoveType.all );
					}
				}
			}
			if( del_attachmentIds != null && !del_attachmentIds.isEmpty() ){
				for( String id : del_attachmentIds ){
					attachment = emc.find( id, OkrAttachmentFileInfo.class );
					if( attachment != null ){
						mapping = ThisApplication.storageMappings.get( StorageType.okr, attachment.getStorage() );
						attachment.deleteContent( mapping );
						emc.remove( attachment, CheckRemoveType.all );
					}
				}
			}
			emc.commit();
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
	public List<OkrCenterWorkInfo> listNextWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if ( !StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			return business.okrCenterWorkInfoFactory().listNextWithFilter(id, count, sequence, wrapIn);
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
	public List<OkrCenterWorkInfo> listPrevWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			return business.okrCenterWorkInfoFactory().listPrevWithFilter(id, count, sequence, wrapIn);
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
			return business.okrCenterWorkInfoFactory().getCountWithFilter(wrapIn);
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
	public List<OkrCenterWorkInfo> listCenterNextWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = new ArrayList<OkrCenterWorkInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "CENTERWORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listNextWithFilter(id, count, sequence, wrapIn);
			if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					okrCenterWorkInfo = emc.find( okrWorkPerson.getCenterId(), OkrCenterWorkInfo.class );
					if( okrCenterWorkInfo != null && !okrCenterWorkInfoList.contains( okrCenterWorkInfo )){
						okrCenterWorkInfoList.add( okrCenterWorkInfo );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return okrCenterWorkInfoList;
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
	public List<OkrCenterWorkInfo> listCenterPrevWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = new ArrayList<OkrCenterWorkInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "CENTERWORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase( id, HttpAttribute.x_empty_symbol )) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class, ExceptionWhen.not_found), "sequence" );
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listPrevWithFilter(id, count, sequence, wrapIn);
			if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					okrCenterWorkInfo = emc.find( okrWorkPerson.getCenterId(), OkrCenterWorkInfo.class );
					if( okrCenterWorkInfo != null && !okrCenterWorkInfoList.contains( okrCenterWorkInfo )){
						okrCenterWorkInfoList.add( okrCenterWorkInfo );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return okrCenterWorkInfoList;
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
	public Long getCenterCountWithFilter( com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "CENTERWORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 部署中心工作，只需要将中心工作的状态修改为[执行中]即可
	 * 维护中心工作干系人
	 * @param id
	 * @throws Exception 
	 */
	public void deploy( String id ) throws Exception {
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, can not deploy any center work!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			okrCenterWorkInfo = emc.find( id, OkrCenterWorkInfo.class );
			if( okrCenterWorkInfo != null ){
				//根据中心工作信息维护中心工作干系人信息
				okrWorkPersonService.saveWorkPersonByCenterWork(okrCenterWorkInfo);
				emc.beginTransaction( OkrCenterWorkInfo.class );
				okrCenterWorkInfo.setProcessStatus( "执行中" );
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}

	public List<String> listAllProcessingCenterWorkIds( List<String> processStatus, List<String> status ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrCenterWorkInfoFactory().listAllProcessingCenterWorkIds( processStatus, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void countWorkWithCenterId(String centerId, List<String> status) {
		Long workTotal = 0L;
		Long processingWorkCount = 0L;
		Long completedWorkCount = 0L;
		Long overtimeWorkCount = 0L;
		Long draftWorkCount = 0L;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		try{
			workTotal = okrWorkBaseInfoService.getWorkTotalByCenterId( centerId, status );
			processingWorkCount = okrWorkBaseInfoService.getProcessingWorkCountByCenterId( centerId, status );
			completedWorkCount = okrWorkBaseInfoService.getCompletedWorkCountByCenterId( centerId, status );
			overtimeWorkCount = okrWorkBaseInfoService.getOvertimeWorkCountByCenterId( centerId, status );
			draftWorkCount = okrWorkBaseInfoService.getDraftWorkCountByCenterId( centerId, status );
			
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
				if( okrCenterWorkInfo != null ){
					emc.beginTransaction(OkrCenterWorkInfo.class );
					okrCenterWorkInfo.setWorkTotal(workTotal);
					okrCenterWorkInfo.setProcessingWorkCount(processingWorkCount);
					okrCenterWorkInfo.setCompletedWorkCount(completedWorkCount);
					okrCenterWorkInfo.setOvertimeWorkCount(overtimeWorkCount);
					okrCenterWorkInfo.setDraftWorkCount(draftWorkCount);				
					if( workTotal > 0 && workTotal == completedWorkCount ){
						okrCenterWorkInfo.setProcessStatus("已完成");
					}	
					emc.check( okrCenterWorkInfo, CheckPersistType.all );
					emc.commit();
				}else{
					throw new Exception("okrCenterWorkInfo{'id':'"+centerId+"'} not exists.");
				}
			} catch ( Exception e ) {
				throw e;
			}		
		}catch(Exception e){
			logger.error( "system count work info by center info got an exception.", e );
		}
	}

	public List<OkrCenterWorkInfo> listAllCenterWorks( String status ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrCenterWorkInfoFactory().listAllCenterWorks( status );
		} catch ( Exception e ) {
			throw e;
		}
	}
}