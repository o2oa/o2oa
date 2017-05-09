package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkViewInfo;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.CenterWorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkNotExistsException;
import com.x.okr.assemble.control.service.OkrAttachmentFileInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteViewWork extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteViewWork.class );
	private BeanCopyTools<OkrAttachmentFileInfo, WrapOutOkrAttachmentFileInfo> wrapout_copier_attachment = BeanCopyToolsBuilder.create( OkrAttachmentFileInfo.class, WrapOutOkrAttachmentFileInfo.class, null, WrapOutOkrAttachmentFileInfo.Excludes);
	private BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseViewInfo> wrapout_copier_work = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseViewInfo.class, null, WrapOutOkrWorkBaseViewInfo.Excludes);
	private BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseSimpleInfo> wrapout_copier_worksimple = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseSimpleInfo.class, null, WrapOutOkrWorkBaseSimpleInfo.Excludes);
	private BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseSimpleInfo> wrapout_copier_report = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseSimpleInfo.class, null, WrapOutOkrWorkReportBaseSimpleInfo.Excludes);
	private BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkViewInfo> wrapout_copier_center = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkViewInfo.class, null, WrapOutOkrCenterWorkViewInfo.Excludes);
	private OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	private OkrAttachmentFileInfoService okrAttachmentFileInfoService = new OkrAttachmentFileInfoService();
	
	/**
	 * 展示工作的详细信息
	 * 1, 工作基础信息
	 * 2, 工作详细信息内容
	 * 3, 工作的附件列表
	 * 4, 工作的部署过程(加上授权过程)
	 * 5, 工作的汇报基础信息列表
	 * 6, 工作的问题请示基础信息列表
	 * 7, 工作的交流信息列表
	 * 8, 工作所在的中心工作信息内容 
	 * 
	 * @param effectivePerson
	 * @param id
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<WrapOutOkrWorkBaseViewInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrWorkBaseViewInfo> result = new ActionResult<>();
		
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		List<OkrWorkBaseInfo> subWorkBaseInfoList = null;
		List<OkrWorkReportBaseInfo>  okrWorkReportBaseInfoList = null;
		List<OkrAttachmentFileInfo> attachmentList = null;
		
		WrapOutOkrWorkBaseViewInfo wrap = null;
		WrapOutOkrCenterWorkViewInfo wrapOutOkrCenterWorkViewInfo  = null;
		WrapOutOkrWorkDeployAuthorizeRecord wrapOutOkrWorkDeployAuthorizeRecord = null;
		WrapOutOkrWorkDeployAuthorizeRecord _wrapOutOkrWorkDeployAuthorizeRecord = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<WrapOutOkrWorkBaseSimpleInfo> wrapOutSubWorkBaseInfoList = null;
		List<WrapOutOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfos = null;
		List<WrapOutOkrWorkReportBaseSimpleInfo> wrapOutOkrWorkReportBaseSimpleInfoList = null;
		List<WrapOutOkrWorkDeployAuthorizeRecord> wrapOutOkrWorkDeployAuthorizeRecordList = new ArrayList<>();
		
		List<String> ids = null;
		List<String> subIds = null;
		Date deployDate = null;
		String dateTime = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
		}
		
		//1, 工作基础信息
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
				if( okrWorkBaseInfo != null ){
					if( okrWorkBaseInfo.getDeployDateStr() != null && !okrWorkBaseInfo.getDeployDateStr().isEmpty() ){
						deployDate = dateOperation.getDateFromString( okrWorkBaseInfo.getDeployDateStr() );
					}
					wrap = wrapout_copier_work.copy( okrWorkBaseInfo );
				}else{
					check = false;
					Exception exception = new WorkNotExistsException( id  );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的具体工作信息时发生异常。ID：" + id  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//2, 工作所在的中心工作信息内容 
		if (check) {
			if ( okrWorkBaseInfo != null && okrWorkBaseInfo.getCenterId() != null ) {
				try {
					okrCenterWorkInfo = okrCenterWorkInfoService.get( okrWorkBaseInfo.getCenterId() );
					if (okrCenterWorkInfo != null) {
						wrapOutOkrCenterWorkViewInfo = wrapout_copier_center.copy( okrCenterWorkInfo );
						if ( wrapOutOkrCenterWorkViewInfo != null ) {
							wrap.setCenterWorkInfo( wrapOutOkrCenterWorkViewInfo );
						}
					}else{
						check = false;
						Exception exception = new CenterWorkNotExistsException( okrWorkBaseInfo.getCenterId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的中心工作信息时发生异常。ID：" + okrWorkBaseInfo.getCenterId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		//3, 工作详细信息内容
		if(check){
			try {
				okrWorkDetailInfo = okrWorkDetailInfoService.get( id );
				if( okrWorkDetailInfo != null ){
					wrap.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
					wrap.setDutyDescription( okrWorkDetailInfo.getDutyDescription() );
					wrap.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
					wrap.setMajorIssuesDescription( okrWorkDetailInfo.getMajorIssuesDescription() );
					wrap.setProgressAction( okrWorkDetailInfo.getProgressAction() );
					wrap.setProgressPlan( okrWorkDetailInfo.getProgressPlan() );
					wrap.setResultDescription( okrWorkDetailInfo.getResultDescription() );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的工作详细信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		//4, 工作的附件列表
		if(check){
			if( okrWorkBaseInfo != null && okrWorkBaseInfo.getAttachmentList() != null && !okrWorkBaseInfo.getAttachmentList().isEmpty() ){
				try {
					attachmentList = okrAttachmentFileInfoService.list( okrWorkBaseInfo.getAttachmentList() );
					if( attachmentList != null ){
						wrapOutOkrAttachmentFileInfos = wrapout_copier_attachment.copy( attachmentList );
						wrap.setWorkAttachments(wrapOutOkrAttachmentFileInfos);
					}
				} catch ( Exception e ) {
					check = false;
					Exception exception = new WorkBaseInfoProcessException( e, "根据工作ID获取工作附件列表发生异常，ID:"+id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
		}
		//5, 工作的汇报基础信息列表
		if (check) {
			try {
				ids = okrWorkReportQueryService.listByWorkId( id );
				if (ids != null && !ids.isEmpty()) {
					okrWorkReportBaseInfoList = okrWorkReportQueryService.listByIds(ids);
					if (okrWorkReportBaseInfoList != null && !okrWorkReportBaseInfoList.isEmpty()) {
						wrapOutOkrWorkReportBaseSimpleInfoList = wrapout_copier_report.copy( okrWorkReportBaseInfoList );
						wrap.setWorkReports(wrapOutOkrWorkReportBaseSimpleInfoList);
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "系统根据工作ID查询所有工作汇报ID列表发生异常. ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//6, 当前用户最后一次对该工作的授权信息,如果有的话
		if(check){
			try {
				//获取该工作和当前责任人相关的授权信息
				okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( id, okrUserCache.getLoginIdentityName(), "正常"  );
				if( okrWorkAuthorizeRecord != null ){
					wrap.setOkrWorkAuthorizeRecord( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecord ) );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。Person: "+ okrUserCache.getLoginIdentityName() +", ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		//7, 递归查询所有的下级工作信息
		if(check){
			subIds = okrWorkBaseInfoService.listByParentId( id );
			if( subIds != null && !subIds.isEmpty() ){
				subWorkBaseInfoList = okrWorkBaseInfoService.listByIds( subIds );
				if( subWorkBaseInfoList != null && !subWorkBaseInfoList.isEmpty() ){
					wrapOutSubWorkBaseInfoList = wrapout_copier_worksimple.copy( subWorkBaseInfoList );
					wrap.setSubWorks( wrapOutSubWorkBaseInfoList );
				}
			}
		}
		
		//6, 工作的问题请示基础信息列表
		//7, 工作的交流信息列表(另外的请求取, 不做)
		if(check){
			try {
				//获取该工作所有的授权信息
				ids = okrWorkAuthorizeRecordService.listByWorkId( id );
				if( ids != null && !ids.isEmpty() ){
					okrWorkAuthorizeRecordList = okrWorkAuthorizeRecordService.list( ids );
					if( okrWorkAuthorizeRecordList != null ){
						for( OkrWorkAuthorizeRecord _okrWorkAuthorizeRecord : okrWorkAuthorizeRecordList ){
							if( deployDate.before( _okrWorkAuthorizeRecord.getDelegateDateTime() )){
								deployDate = _okrWorkAuthorizeRecord.getDelegateDateTime();
							}
							wrapOutOkrWorkDeployAuthorizeRecord = new WrapOutOkrWorkDeployAuthorizeRecord();
							wrapOutOkrWorkDeployAuthorizeRecord.setWorkId( _okrWorkAuthorizeRecord.getWorkId() );
							wrapOutOkrWorkDeployAuthorizeRecord.setWorkTitle( _okrWorkAuthorizeRecord.getTitle());
							wrapOutOkrWorkDeployAuthorizeRecord.setSource( _okrWorkAuthorizeRecord.getDelegatorIdentity() );
							wrapOutOkrWorkDeployAuthorizeRecord.setOperationTypeCN( "工作授权" );
							wrapOutOkrWorkDeployAuthorizeRecord.setOperationType( "AUTHORIZE" );
							wrapOutOkrWorkDeployAuthorizeRecord.setOperationTime( _okrWorkAuthorizeRecord.getDelegateDateTimeStr() );
							wrapOutOkrWorkDeployAuthorizeRecord.setTarget( _okrWorkAuthorizeRecord.getTargetIdentity() );
							wrapOutOkrWorkDeployAuthorizeRecord.setOpinion( _okrWorkAuthorizeRecord.getDelegateOpinion() );
							wrapOutOkrWorkDeployAuthorizeRecord.setDescription( "信息来自于授权记录" );
							wrapOutOkrWorkDeployAuthorizeRecordList.add( wrapOutOkrWorkDeployAuthorizeRecord );
							if( _okrWorkAuthorizeRecord.getTakebackDateTime() != null && "已收回".equals( _okrWorkAuthorizeRecord.getStatus() )){
								if( deployDate.before( _okrWorkAuthorizeRecord.getTakebackDateTime() )){
									deployDate = _okrWorkAuthorizeRecord.getTakebackDateTime();
								}
								try{
									dateTime = dateOperation.getDate( _okrWorkAuthorizeRecord.getTakebackDateTime(), "yyyy-MM-dd HH:mm:ss");
									wrapOutOkrWorkDeployAuthorizeRecord = new WrapOutOkrWorkDeployAuthorizeRecord();
									wrapOutOkrWorkDeployAuthorizeRecord.setWorkId( _okrWorkAuthorizeRecord.getWorkId() );
									wrapOutOkrWorkDeployAuthorizeRecord.setWorkTitle( _okrWorkAuthorizeRecord.getTitle());
									wrapOutOkrWorkDeployAuthorizeRecord.setSource( _okrWorkAuthorizeRecord.getDelegatorIdentity() );
									wrapOutOkrWorkDeployAuthorizeRecord.setOperationTypeCN( "授权收回" );
									wrapOutOkrWorkDeployAuthorizeRecord.setOperationType( "TACKBACK" );
									wrapOutOkrWorkDeployAuthorizeRecord.setOperationTime( dateTime );
									wrapOutOkrWorkDeployAuthorizeRecord.setTarget( "" );
									wrapOutOkrWorkDeployAuthorizeRecord.setOpinion( "收回" );
									wrapOutOkrWorkDeployAuthorizeRecord.setDescription( "信息来自于授权记录" );
									wrapOutOkrWorkDeployAuthorizeRecordList.add( wrapOutOkrWorkDeployAuthorizeRecord );
								}catch( Exception e ){
									logger.warn( "授权收回时间格式化异常, tackbacktime:" + _okrWorkAuthorizeRecord.getTakebackDateTime() );
									logger.error( e );
								}
							}
						}
					}
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if(check){
			//获取到第一次授权, 要增加部署者到第一次授权者的部署过程,如果没有授权, 那么就是部署者到负责者的部署过程
			if( wrapOutOkrWorkDeployAuthorizeRecordList != null && !wrapOutOkrWorkDeployAuthorizeRecordList.isEmpty() ){
				SortTools.asc( wrapOutOkrWorkDeployAuthorizeRecordList, "operationTime" );
				_wrapOutOkrWorkDeployAuthorizeRecord = wrapOutOkrWorkDeployAuthorizeRecordList.get( 0 );
				if( _wrapOutOkrWorkDeployAuthorizeRecord != null ){
					wrapOutOkrWorkDeployAuthorizeRecord = new WrapOutOkrWorkDeployAuthorizeRecord();
					wrapOutOkrWorkDeployAuthorizeRecord.setWorkId( okrWorkBaseInfo.getId() );
					wrapOutOkrWorkDeployAuthorizeRecord.setWorkTitle( okrWorkBaseInfo.getTitle());
					wrapOutOkrWorkDeployAuthorizeRecord.setSource( okrWorkBaseInfo.getDeployerIdentity() );
					wrapOutOkrWorkDeployAuthorizeRecord.setOperationTypeCN( "工作部署" );
					wrapOutOkrWorkDeployAuthorizeRecord.setOperationType( "DEPLOY" );
					wrapOutOkrWorkDeployAuthorizeRecord.setOperationTime( okrWorkBaseInfo.getDeployDateStr() );
					wrapOutOkrWorkDeployAuthorizeRecord.setTarget( _wrapOutOkrWorkDeployAuthorizeRecord.getSource() );
					wrapOutOkrWorkDeployAuthorizeRecord.setOpinion( "" );
					wrapOutOkrWorkDeployAuthorizeRecord.setDescription( "信息来自于工作信息和第一条授权记录" );
					wrapOutOkrWorkDeployAuthorizeRecordList.add( wrapOutOkrWorkDeployAuthorizeRecord );
				}
			}else{
				wrapOutOkrWorkDeployAuthorizeRecord = new WrapOutOkrWorkDeployAuthorizeRecord();
				wrapOutOkrWorkDeployAuthorizeRecord.setWorkId( okrWorkBaseInfo.getId() );
				wrapOutOkrWorkDeployAuthorizeRecord.setWorkTitle( okrWorkBaseInfo.getTitle());
				wrapOutOkrWorkDeployAuthorizeRecord.setSource( okrWorkBaseInfo.getDeployerIdentity() );
				wrapOutOkrWorkDeployAuthorizeRecord.setOperationTypeCN( "工作部署" );
				wrapOutOkrWorkDeployAuthorizeRecord.setOperationType( "DEPLOY" );
				wrapOutOkrWorkDeployAuthorizeRecord.setOperationTime( okrWorkBaseInfo.getDeployDateStr() );
				wrapOutOkrWorkDeployAuthorizeRecord.setTarget( okrWorkBaseInfo.getResponsibilityEmployeeName() );
				wrapOutOkrWorkDeployAuthorizeRecord.setOpinion( "" );
				wrapOutOkrWorkDeployAuthorizeRecord.setDescription( "信息来自于工作信息" );
				wrapOutOkrWorkDeployAuthorizeRecordList.add( wrapOutOkrWorkDeployAuthorizeRecord );
			}
		}
		
		if(check){
			//递归查询上级工作的部署者,组织部署记录
			while( okrWorkBaseInfo != null && okrWorkBaseInfo.getParentWorkId() != null && !okrWorkBaseInfo.getParentWorkId().isEmpty()
					&& !okrWorkBaseInfo.getParentWorkId().equals( okrWorkBaseInfo.getId() )
			){
				try {
					String responsibilityIdentity = okrWorkBaseInfo.getDeployerIdentity();
					okrWorkBaseInfo = okrWorkBaseInfoService.get( okrWorkBaseInfo.getParentWorkId() );
					if( okrWorkBaseInfo != null ){
						wrapOutOkrWorkDeployAuthorizeRecord = new WrapOutOkrWorkDeployAuthorizeRecord();
						wrapOutOkrWorkDeployAuthorizeRecord.setWorkId( okrWorkBaseInfo.getId() );
						wrapOutOkrWorkDeployAuthorizeRecord.setWorkTitle( okrWorkBaseInfo.getTitle());
						wrapOutOkrWorkDeployAuthorizeRecord.setSource( okrWorkBaseInfo.getDeployerIdentity() );
						wrapOutOkrWorkDeployAuthorizeRecord.setOperationTypeCN( "工作部署" );
						wrapOutOkrWorkDeployAuthorizeRecord.setOperationType( "DEPLOY" );
						wrapOutOkrWorkDeployAuthorizeRecord.setOperationTime( okrWorkBaseInfo.getDeployDateStr() );
						wrapOutOkrWorkDeployAuthorizeRecord.setTarget( responsibilityIdentity );
						wrapOutOkrWorkDeployAuthorizeRecord.setOpinion( "" );
						wrapOutOkrWorkDeployAuthorizeRecord.setDescription( "信息来自于上级工作信息" );
						wrapOutOkrWorkDeployAuthorizeRecordList.add( wrapOutOkrWorkDeployAuthorizeRecord );

						//获取该工作所有的授权信息
						ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkBaseInfo.getId() );
						if( ids != null && !ids.isEmpty() ){
							okrWorkAuthorizeRecordList = okrWorkAuthorizeRecordService.list( ids );
							
							if( okrWorkAuthorizeRecordList != null ){
								for( OkrWorkAuthorizeRecord _okrWorkAuthorizeRecord : okrWorkAuthorizeRecordList ){
									if( deployDate != null && _okrWorkAuthorizeRecord.getDelegateDateTime().before( deployDate )){
										wrapOutOkrWorkDeployAuthorizeRecord = new WrapOutOkrWorkDeployAuthorizeRecord();
										wrapOutOkrWorkDeployAuthorizeRecord.setWorkId( _okrWorkAuthorizeRecord.getId() );
										wrapOutOkrWorkDeployAuthorizeRecord.setWorkTitle( _okrWorkAuthorizeRecord.getTitle());
										wrapOutOkrWorkDeployAuthorizeRecord.setSource( _okrWorkAuthorizeRecord.getDelegatorIdentity() );
										wrapOutOkrWorkDeployAuthorizeRecord.setOperationTypeCN( "工作授权" );
										wrapOutOkrWorkDeployAuthorizeRecord.setOperationType( "AUTHORIZE" );
										wrapOutOkrWorkDeployAuthorizeRecord.setOperationTime( _okrWorkAuthorizeRecord.getDelegateDateTimeStr() );
										wrapOutOkrWorkDeployAuthorizeRecord.setTarget( _okrWorkAuthorizeRecord.getTargetIdentity() );
										wrapOutOkrWorkDeployAuthorizeRecord.setOpinion( _okrWorkAuthorizeRecord.getDelegateOpinion() );
										wrapOutOkrWorkDeployAuthorizeRecord.setDescription( "信息来自于上级授权记录" );
										wrapOutOkrWorkDeployAuthorizeRecordList.add( wrapOutOkrWorkDeployAuthorizeRecord );
										
										if( _okrWorkAuthorizeRecord.getTakebackDateTime() != null && "已收回".equals( _okrWorkAuthorizeRecord.getStatus() )){
											if( deployDate != null && _okrWorkAuthorizeRecord.getTakebackDateTime().before( deployDate )){
												try{
													dateTime = dateOperation.getDate( _okrWorkAuthorizeRecord.getTakebackDateTime(), "yyyy-MM-dd HH:mm:ss");
													wrapOutOkrWorkDeployAuthorizeRecord = new WrapOutOkrWorkDeployAuthorizeRecord();
													wrapOutOkrWorkDeployAuthorizeRecord.setWorkId( _okrWorkAuthorizeRecord.getId() );
													wrapOutOkrWorkDeployAuthorizeRecord.setWorkTitle( _okrWorkAuthorizeRecord.getTitle());
													wrapOutOkrWorkDeployAuthorizeRecord.setSource( _okrWorkAuthorizeRecord.getDelegatorIdentity() );
													wrapOutOkrWorkDeployAuthorizeRecord.setOperationTypeCN( "授权收回" );
													wrapOutOkrWorkDeployAuthorizeRecord.setOperationType( "TACKBACK" );
													wrapOutOkrWorkDeployAuthorizeRecord.setOperationTime( dateTime );
													wrapOutOkrWorkDeployAuthorizeRecord.setTarget( "" );
													wrapOutOkrWorkDeployAuthorizeRecord.setOpinion( "收回" );
													wrapOutOkrWorkDeployAuthorizeRecord.setDescription( "信息来自于上级授权记录" );
													wrapOutOkrWorkDeployAuthorizeRecordList.add( wrapOutOkrWorkDeployAuthorizeRecord );
												}catch( Exception e ){
													logger.warn( "授权收回时间格式化异常, tackbacktime:" + _okrWorkAuthorizeRecord.getTakebackDateTime() );
													logger.error( e );
												}
											}
										}
									}
								}
							}
						}
					}
				} catch ( Exception e ) {
					check = false;
					result.error( e );
					logger.warn( "system get work info by work id got an exception. id:"+okrWorkBaseInfo.getParentWorkId() );
					logger.error( e );
				}
			}
		}
		SortTools.asc( wrapOutOkrWorkDeployAuthorizeRecordList, "operationTime" );
		if( wrap != null ){
			wrap.setWorkDeployAuthorizeRecords( wrapOutOkrWorkDeployAuthorizeRecordList );
			result.setData(wrap);
		}
		return result;
	}
	
}