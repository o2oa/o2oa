package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
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

public class OkrCenterWorkExcuteDelete {
	
	private OkrStatisticReportStatusService okrStatisticReportStatusService = new OkrStatisticReportStatusService();
	
	/**
	 * 根据ID从数据库中删除OkrCenterWorkInfo对象
	 * 同时删除所有的下级工作以及工作的相关汇报，请示等等
	 * @param id
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String centerId ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not delete any object." );
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
		List<OkrTaskHandled> okrTaskHandledList = null;
		List<OkrTask> okrTaskList = null;
		List<OkrWorkChat> okrWorkChatList = null;
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
		if( ids != null && ids.size() > 0 ){
			for( String id : ids ){
				
				statisticIds = business.okrStatisticReportContentFactory().list( id, null, null, null, null );
				if (statisticIds != null && statisticIds.size() > 0) {
					for ( String statisticId : statisticIds) {
						okrStatisticReportContent = emc.find( statisticId, OkrStatisticReportContent.class);
						if ( okrStatisticReportContent != null) {
							emc.remove( okrStatisticReportContent, CheckRemoveType.all);
						}
					}
				}
				
				statisticList = okrStatisticReportStatusService.list( null, null, id, null, null, null, null );
				if( statisticList != null && !statisticList.isEmpty() ){
					for( OkrStatisticReportStatus okrStatisticReportStatus : statisticList ){
						emc.remove( okrStatisticReportStatus, CheckRemoveType.all);
					}
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
		
		if( del_attachmentIds != null && !del_attachmentIds.isEmpty() ){
			for( String id : del_attachmentIds ){
				attachment = emc.find( id, OkrAttachmentFileInfo.class );
				if( attachment != null ){
					mapping = ThisApplication.context().storageMappings().get( OkrAttachmentFileInfo.class, attachment.getStorage() );
					attachment.deleteContent( mapping );
					emc.remove( attachment, CheckRemoveType.all );
				}
			}
		}
		emc.commit();
	}
}