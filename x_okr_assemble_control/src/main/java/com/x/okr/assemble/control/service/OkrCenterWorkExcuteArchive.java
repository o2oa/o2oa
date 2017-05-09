package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.server.StorageMapping;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class OkrCenterWorkExcuteArchive {

	private OkrStatisticReportStatusService okrStatisticReportStatusService = new OkrStatisticReportStatusService();
	
	/**
	 * 根据ID从归档OkrCenterWorkInfo对象
	 * 同时归档所有的下级工作以及工作的相关汇报，请示等等
	 * 并且删除所有待办
	 * @param id
	 * @throws Exception
	 */
	public void archive( EntityManagerContainer emc, String centerId ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not archive any object." );
		}
		List<String> ids = null;
		List<String> statisticIds = null;
		List<String> del_attachmentIds = new ArrayList<String>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrAttachmentFileInfo attachment = null;
		StorageMapping mapping = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<OkrWorkDetailInfo> okrWorkDetailInfoList = null;
		List<OkrWorkPerson> okrWorkPersonList = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList  = null;
		List<OkrWorkReportDetailInfo> okrWorkReportDetailInfoList = null;
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList  = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		List<OkrWorkDynamics> okrWorkDynamicsList = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<OkrTask> okrTaskList = null;
		List<OkrStatisticReportStatus> statisticList = null;
		OkrStatisticReportContent okrStatisticReportContent = null;
		Business business = new Business(emc);
		okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
		emc.beginTransaction( OkrAttachmentFileInfo.class );
		emc.beginTransaction( OkrCenterWorkInfo.class );
		emc.beginTransaction( OkrWorkBaseInfo.class );
		emc.beginTransaction( OkrWorkDetailInfo.class );
		emc.beginTransaction( OkrWorkPerson.class );
		emc.beginTransaction( OkrWorkReportBaseInfo.class );
		emc.beginTransaction( OkrWorkReportDetailInfo.class );
		emc.beginTransaction( OkrWorkReportPersonLink.class );
		emc.beginTransaction( OkrWorkReportProcessLog.class );
		emc.beginTransaction( OkrTask.class );
		emc.beginTransaction( OkrTaskHandled.class );
		emc.beginTransaction( OkrWorkChat.class );
		emc.beginTransaction( OkrWorkDynamics.class );
		emc.beginTransaction( OkrWorkAuthorizeRecord.class );
		emc.beginTransaction( OkrStatisticReportContent.class );
		emc.beginTransaction( OkrStatisticReportStatus.class );
		
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
						okrWorkBaseInfo.setArchiveDate( new Date() );
						emc.check( okrWorkBaseInfo, CheckPersistType.all );
					}
				}
			}			
		}
		//归档统计数据
		if( ids != null && ids.size() > 0 ){
			for( String id : ids ){
				statisticIds = business.okrStatisticReportContentFactory().list( id, null, null, null, null );
				if (statisticIds != null && statisticIds.size() > 0) {
					for ( String statisticId : statisticIds) {
						okrStatisticReportContent = emc.find( statisticId, OkrStatisticReportContent.class);
						if ( okrStatisticReportContent != null) {
							okrStatisticReportContent.setStatus("已归档");
							emc.check( okrStatisticReportContent, CheckPersistType.all);
						}
					}
				}
				statisticList = okrStatisticReportStatusService.list( null, null, id, null, null, null, null );
				if( statisticList != null && !statisticList.isEmpty() ){
					for( OkrStatisticReportStatus statistic : statisticList ){
						statistic.setStatus( "已归档" );
						emc.check( statistic, CheckPersistType.all );
					}
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
					mapping = ThisApplication.context().storageMappings().get(OkrAttachmentFileInfo.class, attachment.getStorage() );
					attachment.deleteContent( mapping );
					emc.remove( attachment, CheckRemoveType.all );
				}
			}
		}
		emc.commit();
	}
}