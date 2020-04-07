package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAppraiseInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionViewWork extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionViewWork.class );
	
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
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		List<OkrWorkBaseInfo> subWorkBaseInfoList = null;
		List<OkrWorkReportBaseInfo>  okrWorkReportBaseInfoList = null;
		List<OkrAttachmentFileInfo> attachmentList = null;
		
		Wo wrap = null;
		WoOkrCenterWorkViewInfo wrapOutOkrCenterWorkViewInfo  = null;
		WoOkrWorkDeployAuthorizeRecord wrapOutOkrWorkDeployAuthorizeRecord = null;
		WoOkrWorkDeployAuthorizeRecord _wrapOutOkrWorkDeployAuthorizeRecord = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<WoOkrWorkBaseSimpleInfo> wrapOutSubWorkBaseInfoList = null;
		List<WoOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfos = null;
		List<WoOkrWorkReportBaseSimpleInfo> wrapOutOkrWorkReportBaseSimpleInfoList = null;
		List<WoOkrWorkDeployAuthorizeRecord> wrapOutOkrWorkDeployAuthorizeRecordList = new ArrayList<>();
		
		List<String> ids = null;
		List<String> subIds = null;
		Date deployDate = null;
		String dateTime = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new ExceptionWorkIdEmpty();
			result.error( exception );
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
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
					wrap = Wo.copier.copy( okrWorkBaseInfo );
				}else{
					check = false;
					Exception exception = new ExceptionWorkNotExists( id  );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的具体工作信息时发生异常。ID：" + id  );
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
						wrapOutOkrCenterWorkViewInfo = WoOkrCenterWorkViewInfo.copier.copy( okrCenterWorkInfo );
						if ( wrapOutOkrCenterWorkViewInfo != null ) {
							wrap.setCenterWorkInfo( wrapOutOkrCenterWorkViewInfo );
						}
					}else{
						check = false;
						Exception exception = new ExceptionCenterWorkNotExists( okrWorkBaseInfo.getCenterId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的中心工作信息时发生异常。ID：" + okrWorkBaseInfo.getCenterId() );
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
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的工作详细信息时发生异常。ID：" + id );
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
						wrapOutOkrAttachmentFileInfos = WoOkrAttachmentFileInfo.copier.copy( attachmentList );
						wrap.setWorkAttachments(wrapOutOkrAttachmentFileInfos);
					}
				} catch ( Exception e ) {
					check = false;
					Exception exception = new ExceptionWorkBaseInfoProcess( e, "根据工作ID获取工作附件列表发生异常，ID:"+id );
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
						wrapOutOkrWorkReportBaseSimpleInfoList = WoOkrWorkReportBaseSimpleInfo.copier.copy( okrWorkReportBaseInfoList );
						wrap.setWorkReports(wrapOutOkrWorkReportBaseSimpleInfoList);
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据工作ID查询所有工作汇报ID列表发生异常. ID：" + id );
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
					wrap.setOkrWorkAuthorizeRecord( WoOkrWorkAuthorizeRecord.copier.copy( okrWorkAuthorizeRecord ) );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。Person: "+ okrUserCache.getLoginIdentityName() +", ID：" + id );
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
					wrapOutSubWorkBaseInfoList = WoOkrWorkBaseSimpleInfo.copier.copy( subWorkBaseInfoList );
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
							wrapOutOkrWorkDeployAuthorizeRecord = new WoOkrWorkDeployAuthorizeRecord();
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
									wrapOutOkrWorkDeployAuthorizeRecord = new WoOkrWorkDeployAuthorizeRecord();
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
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。ID：" + id );
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
					wrapOutOkrWorkDeployAuthorizeRecord = new WoOkrWorkDeployAuthorizeRecord();
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
				wrapOutOkrWorkDeployAuthorizeRecord = new WoOkrWorkDeployAuthorizeRecord();
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
						wrapOutOkrWorkDeployAuthorizeRecord = new WoOkrWorkDeployAuthorizeRecord();
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
										wrapOutOkrWorkDeployAuthorizeRecord = new WoOkrWorkDeployAuthorizeRecord();
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
													wrapOutOkrWorkDeployAuthorizeRecord = new WoOkrWorkDeployAuthorizeRecord();
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
		if( ListTools.isNotEmpty(wrapOutOkrWorkDeployAuthorizeRecordList)) {
			SortTools.asc( wrapOutOkrWorkDeployAuthorizeRecordList, "operationTime" );
		}
		
		//如果有考核信息，将考核信息全部放进考核信息对象列表里
		if(check){
			List<OkrWorkAppraiseInfo> appraiseInfoList = null;
			List<WoOkrWorkAppraiseInfo> woAppraiseInfoList = null; 
			if( ListTools.isNotEmpty( wrap.getAppraiseInfoList() )) {
				appraiseInfoList = okrWorkAppraiseQueryService.listByIds( wrap.getAppraiseInfoList() );
				if(ListTools.isNotEmpty( appraiseInfoList )) {
					woAppraiseInfoList = WoOkrWorkAppraiseInfo.copier.copy(appraiseInfoList);
					SortTools.asc( woAppraiseInfoList, "createTime" );
					wrap.setAppraiseObjectList(woAppraiseInfoList);
				}
			}
		}
		
		if( wrap != null ){
			wrap.setWorkDeployAuthorizeRecords( wrapOutOkrWorkDeployAuthorizeRecordList );
			result.setData(wrap);
		}
		return result;
	}
	
	public static class WoOkrWorkAppraiseInfo extends OkrWorkAppraiseInfo  {
		private static final long serialVersionUID = 1L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<OkrWorkAppraiseInfo, WoOkrWorkAppraiseInfo> copier = WrapCopierFactory.wo( OkrWorkAppraiseInfo.class, WoOkrWorkAppraiseInfo.class, null, JpaObject.FieldsInvisible);
	}
	
	public static class Wo  {
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<OkrWorkBaseInfo, Wo> copier = WrapCopierFactory.wo( OkrWorkBaseInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		@FieldDescribe( "工作所属中心工作信息" )
		private WoOkrCenterWorkViewInfo centerWorkInfo = null;
		
		@FieldDescribe( "工作附件信息列表" )
		private List< WoOkrAttachmentFileInfo > workAttachments = null;
		
		@FieldDescribe( "子工作信息列表" )
		private List< WoOkrWorkBaseSimpleInfo > subWorks = null;
		
		@FieldDescribe( "工作汇报信息简单信息列表" )
		private List< WoOkrWorkReportBaseSimpleInfo > workReports = null;
		
		@FieldDescribe( "工作组织以及授权过程列表， 由该工作的上级工作线以及工作授权记录一起组织起来的信息列表" )
		private List< WoOkrWorkDeployAuthorizeRecord > workDeployAuthorizeRecords = null;
		
		@FieldDescribe( "查看者的授权信息, 有可能没有" )
		private WoOkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		
		@FieldDescribe( "工作ID" )
		private String id = "";
		
		@FieldDescribe( "工作标题" )
		private String title = "";
		
		@FieldDescribe( "中心工作ID" )
		private String centerId = "";
		
		@FieldDescribe( "中心工作标题" )
		private String centerTitle = "";
		
		@FieldDescribe( "上级工作ID" )
		private String parentWorkId = "";
		
		@FieldDescribe( "上级工作标题" )
		private String parentWorkTitle = "";	

		@FieldDescribe( "工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）" )
		private String workDateTimeType = "长期工作";
		
		@FieldDescribe( "部署者姓名" )
		private String deployerName = "";
		
		@FieldDescribe( "部署者身份" )
		private String deployerIdentity = "";
		
		@FieldDescribe( "部署者所属组织" )
		private String deployerUnitName = "";
		
		@FieldDescribe( "部署者所属顶层组织" )
		private String deployerTopUnitName = "";
		
		@FieldDescribe( "工作部署日期-字符串，显示用：yyyy-mm-dd" )
		private String deployDateStr = "";
		
		@FieldDescribe( "工作确认日期-字符串，显示用：yyyy-mm-dd" )
		private String confirmDateStr = "";
		
		@FieldDescribe( "工作完成日期-字符串，显示用：yyyy-mm-dd" )
		private String completeDateLimitStr = "";
		
		@FieldDescribe( "主责人姓名" )
		private String responsibilityEmployeeName = "";
		
		@FieldDescribe( "主责人身份" )
		private String responsibilityIdentity = "";
		
		@FieldDescribe( "主责人所属组织" )
		private String responsibilityUnitName = "";
		
		@FieldDescribe( "主责人所属顶层组织" )
		private String responsibilityTopUnitName = "";

		@FieldDescribe( "协助人姓名，多值" )
		private List<String> cooperateEmployeeNameList = null;
		
		@FieldDescribe( "协助人身份，多值" )
		private List<String> cooperateIdentityList = null;
		
		@FieldDescribe( "协助人所属组织，多值" )
		private List<String> cooperateUnitNameList = null;
		
		@FieldDescribe( "协助人所属顶层组织，多值" )
		private List<String> cooperateTopUnitNameList = null;
		
		@FieldDescribe( "阅知领导身份，多值" )
		private List<String> readLeaderIdentityList = null;
		
		@FieldDescribe( "阅知领导，可能多值" )
		private List<String> readLeaderNameList = null;
		
		@FieldDescribe( "阅知领导所属组织，多值" )
		private List<String> readLeaderUnitNameList = null;
		
		@FieldDescribe( "阅知领导所属顶层组织，多值" )
		private List<String> readLeaderTopUnitNameList = null;
		
		@FieldDescribe( "工作类别" )
		private String workType = "";
		
		@FieldDescribe( "工作级别" )
		private String workLevel = "";
		
		@FieldDescribe( "工作进度" )
		private Double overallProgress = 0.0;
		
		@FieldDescribe( "工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消" )
		private String workProcessStatus = "草稿";
		
		@FieldDescribe( "工作是否已超期" )
		private Boolean isOverTime = false;
		
		@FieldDescribe( "工作是否已完成" )
		private Boolean isCompleted = false;

		@FieldDescribe( "上一次汇报时间" )
		private Date lastReportTime = null;
		
		@FieldDescribe( "下一次汇报时间" )
		private Date nextReportTime = null;
		
		@FieldDescribe( "已汇报次数" )
		private Integer reportCount = 0;
		
		@FieldDescribe( "汇报周期:不需要汇报|每月汇报|每周汇报" )
		private String reportCycle = "";
		
		@FieldDescribe( "是否需要定期汇报" )
		private Boolean isNeedReport = true;
		
		@FieldDescribe( "周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00" )
		private Integer reportDayInCycle = 0;
		
		@FieldDescribe( "工作汇报是否需要管理补充信息" )
		private Boolean reportNeedAdminAudit = false;
		
		@FieldDescribe( "工作管理员姓名" )
		private String reportAdminName = "";
		
		@FieldDescribe( "工作管理员姓名" )
		private String reportAdminIdentity = "";
		
		@FieldDescribe( "工作详细描述, 事项分解" )
		private String workDetail = "";
		
		@FieldDescribe( "职责描述" )
		private String dutyDescription = "";
		
		@FieldDescribe( "里程碑标志说明" )
		private String landmarkDescription = "";
		
		@FieldDescribe( "重点事项说明" )
		private String majorIssuesDescription = "";
		
		@FieldDescribe( "具体行动举措" )
		private String progressAction = "";
		
		@FieldDescribe( "进展计划时限说明" )
		private String progressPlan = "";
		
		@FieldDescribe( "交付成果说明" )
		private String resultDescription = "";
	   
		@FieldDescribe( "信息状态：正常|已归档" )
		private String status = "";
		
		@FieldDescribe( "归档日期" )
		private Date archiveDate = null;
		
		@FieldDescribe( "完成日期日期" )
		private Date completeTime = null;
		
		@FieldDescribe( "当前考核信息标题" )
		private String currentAppraiseTitle = "无标题";
		
		@FieldDescribe( "当前考核信息Id" )
		private String currentAppraiseInfoId = "";

		@FieldDescribe( "当前考核流程WorkID" )
		private String currentAppraiseWorkId = "";
		
		@FieldDescribe( "当前考核流程JobID" )
		private String currentAppraiseJobId = "";

		@FieldDescribe( "当前考核流程环节名称" )
		private String currentActivityName = "";

		@FieldDescribe( "当前考核审核状态" )
		private String currentAppraiseStatus = "";
		
		@FieldDescribe( "考核次数" )
		private Integer appraiseTimes = 0;
		
		@FieldDescribe("考核流程信息ID列表")
		private List<String> appraiseInfoList;
		
		@FieldDescribe("考核流程信息列表")
		private List<WoOkrWorkAppraiseInfo> appraiseObjectList;
		
		private Long rank = 0L;
		
		public List<WoOkrWorkAppraiseInfo> getAppraiseObjectList() {
			return appraiseObjectList;
		}

		public void setAppraiseObjectList(List<WoOkrWorkAppraiseInfo> appraiseObjectList) {
			this.appraiseObjectList = appraiseObjectList;
		}
		public String getCurrentAppraiseWorkId() {
			return currentAppraiseWorkId;
		}

		public String getCurrentActivityName() {
			return currentActivityName;
		}

		public String getCurrentAppraiseStatus() {
			return currentAppraiseStatus;
		}

		public Integer getAppraiseTimes() {
			return appraiseTimes;
		}

		public String getCurrentAppraiseJobId() {
			return currentAppraiseJobId;
		}

		public void setCurrentAppraiseJobId(String currentAppraiseJobId) {
			this.currentAppraiseJobId = currentAppraiseJobId;
		}

		public List<String> getAppraiseInfoList() {
			return appraiseInfoList;
		}
		public void setCurrentAppraiseWorkId(String currentAppraiseWorkId) {
			this.currentAppraiseWorkId = currentAppraiseWorkId;
		}

		public void setCurrentActivityName(String currentActivityName) {
			this.currentActivityName = currentActivityName;
		}

		public void setCurrentAppraiseStatus(String currentAppraiseStatus) {
			this.currentAppraiseStatus = currentAppraiseStatus;
		}

		public void setAppraiseTimes(Integer appraiseTimes) {
			this.appraiseTimes = appraiseTimes;
		}

		public void setAppraiseInfoList(List<String> appraiseInfoList) {
			this.appraiseInfoList = appraiseInfoList;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getWorkDetail() {
			return workDetail;
		}

		public void setWorkDetail(String workDetail) {
			this.workDetail = workDetail;
		}

		public String getDutyDescription() {
			return dutyDescription;
		}

		public void setDutyDescription(String dutyDescription) {
			this.dutyDescription = dutyDescription;
		}

		public String getLandmarkDescription() {
			return landmarkDescription;
		}

		public void setLandmarkDescription(String landmarkDescription) {
			this.landmarkDescription = landmarkDescription;
		}

		public String getMajorIssuesDescription() {
			return majorIssuesDescription;
		}

		public void setMajorIssuesDescription(String majorIssuesDescription) {
			this.majorIssuesDescription = majorIssuesDescription;
		}

		public String getProgressAction() {
			return progressAction;
		}

		public void setProgressAction(String progressAction) {
			this.progressAction = progressAction;
		}

		public String getProgressPlan() {
			return progressPlan;
		}

		public void setProgressPlan(String progressPlan) {
			this.progressPlan = progressPlan;
		}

		public String getResultDescription() {
			return resultDescription;
		}

		public void setResultDescription(String resultDescription) {
			this.resultDescription = resultDescription;
		}

		public WoOkrWorkAuthorizeRecord getOkrWorkAuthorizeRecord() {
			return okrWorkAuthorizeRecord;
		}

		public void setOkrWorkAuthorizeRecord(WoOkrWorkAuthorizeRecord okrWorkAuthorizeRecord) {
			this.okrWorkAuthorizeRecord = okrWorkAuthorizeRecord;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getCenterId() {
			return centerId;
		}

		public void setCenterId(String centerId) {
			this.centerId = centerId;
		}

		public String getCenterTitle() {
			return centerTitle;
		}

		public void setCenterTitle(String centerTitle) {
			this.centerTitle = centerTitle;
		}

		public String getParentWorkId() {
			return parentWorkId;
		}

		public void setParentWorkId( String parentWorkId ) {
			this.parentWorkId = parentWorkId;
		}

		public String getParentWorkTitle() {
			return parentWorkTitle;
		}

		public void setParentWorkTitle(String parentWorkTitle) {
			this.parentWorkTitle = parentWorkTitle;
		}

		public String getWorkDateTimeType() {
			return workDateTimeType;
		}

		public void setWorkDateTimeType(String workDateTimeType) {
			this.workDateTimeType = workDateTimeType;
		}

		public String getDeployerName() {
			return deployerName;
		}

		public void setDeployerName(String deployerName) {
			this.deployerName = deployerName;
		}

		public String getDeployerIdentity() {
			return deployerIdentity;
		}

		public void setDeployerIdentity(String deployerIdentity) {
			this.deployerIdentity = deployerIdentity;
		}

		public String getDeployerUnitName() {
			return deployerUnitName;
		}

		public void setDeployerUnitName(String deployerUnitName) {
			this.deployerUnitName = deployerUnitName;
		}

		public String getDeployerTopUnitName() {
			return deployerTopUnitName;
		}

		public void setDeployerTopUnitName(String deployerTopUnitName) {
			this.deployerTopUnitName = deployerTopUnitName;
		}

		public String getDeployDateStr() {
			return deployDateStr;
		}

		public void setDeployDateStr(String deployDateStr) {
			this.deployDateStr = deployDateStr;
		}

		public String getConfirmDateStr() {
			return confirmDateStr;
		}

		public void setConfirmDateStr(String confirmDateStr) {
			this.confirmDateStr = confirmDateStr;
		}

		public String getCompleteDateLimitStr() {
			return completeDateLimitStr;
		}

		public void setCompleteDateLimitStr(String completeDateLimitStr) {
			this.completeDateLimitStr = completeDateLimitStr;
		}

		public String getResponsibilityEmployeeName() {
			return responsibilityEmployeeName;
		}

		public void setResponsibilityEmployeeName(String responsibilityEmployeeName) {
			this.responsibilityEmployeeName = responsibilityEmployeeName;
		}

		public String getResponsibilityIdentity() {
			return responsibilityIdentity;
		}

		public void setResponsibilityIdentity(String responsibilityIdentity) {
			this.responsibilityIdentity = responsibilityIdentity;
		}

		public String getResponsibilityUnitName() {
			return responsibilityUnitName;
		}

		public void setResponsibilityUnitName(String responsibilityUnitName) {
			this.responsibilityUnitName = responsibilityUnitName;
		}

		public String getResponsibilityTopUnitName() {
			return responsibilityTopUnitName;
		}

		public void setResponsibilityTopUnitName(String responsibilityTopUnitName) {
			this.responsibilityTopUnitName = responsibilityTopUnitName;
		}
		
		public String getWorkType() {
			return workType;
		}

		public void setWorkType(String workType) {
			this.workType = workType;
		}

		public String getWorkLevel() {
			return workLevel;
		}

		public void setWorkLevel(String workLevel) {
			this.workLevel = workLevel;
		}

		public Double getOverallProgress() {
			return overallProgress;
		}

		public void setOverallProgress(Double overallProgress) {
			this.overallProgress = overallProgress;
		}

		public String getWorkProcessStatus() {
			return workProcessStatus;
		}

		public void setWorkProcessStatus(String workProcessStatus) {
			this.workProcessStatus = workProcessStatus;
		}

		public Boolean getIsOverTime() {
			return isOverTime;
		}

		public void setIsOverTime(Boolean isOverTime) {
			this.isOverTime = isOverTime;
		}

		public Boolean getIsCompleted() {
			return isCompleted;
		}

		public void setIsCompleted(Boolean isCompleted) {
			this.isCompleted = isCompleted;
		}

		public Date getLastReportTime() {
			return lastReportTime;
		}

		public void setLastReportTime(Date lastReportTime) {
			this.lastReportTime = lastReportTime;
		}

		public Date getNextReportTime() {
			return nextReportTime;
		}

		public void setNextReportTime(Date nextReportTime) {
			this.nextReportTime = nextReportTime;
		}

		public Integer getReportCount() {
			return reportCount;
		}

		public void setReportCount(Integer reportCount) {
			this.reportCount = reportCount;
		}

		public String getReportCycle() {
			return reportCycle;
		}

		public void setReportCycle(String reportCycle) {
			this.reportCycle = reportCycle;
		}

		public Boolean getIsNeedReport() {
			return isNeedReport;
		}

		public void setIsNeedReport(Boolean isNeedReport) {
			this.isNeedReport = isNeedReport;
		}

		public Integer getReportDayInCycle() {
			return reportDayInCycle;
		}

		public void setReportDayInCycle(Integer reportDayInCycle) {
			this.reportDayInCycle = reportDayInCycle;
		}

		public Boolean getReportNeedAdminAudit() {
			return reportNeedAdminAudit;
		}

		public void setReportNeedAdminAudit(Boolean reportNeedAdminAudit) {
			this.reportNeedAdminAudit = reportNeedAdminAudit;
		}

		public String getReportAdminName() {
			return reportAdminName;
		}

		public void setReportAdminName(String reportAdminName) {
			this.reportAdminName = reportAdminName;
		}

		public String getReportAdminIdentity() {
			return reportAdminIdentity;
		}

		public void setReportAdminIdentity(String reportAdminIdentity) {
			this.reportAdminIdentity = reportAdminIdentity;
		}

		public WoOkrCenterWorkViewInfo getCenterWorkInfo() {
			return centerWorkInfo;
		}

		public void setCenterWorkInfo(WoOkrCenterWorkViewInfo centerWorkInfo) {
			this.centerWorkInfo = centerWorkInfo;
		}

		public List<WoOkrAttachmentFileInfo> getWorkAttachments() {
			return workAttachments;
		}

		public void setWorkAttachments(List<WoOkrAttachmentFileInfo> workAttachments) {
			this.workAttachments = workAttachments;
		}

		public List<WoOkrWorkReportBaseSimpleInfo> getWorkReports() {
			return workReports;
		}

		public void setWorkReports(List<WoOkrWorkReportBaseSimpleInfo> workReports) {
			this.workReports = workReports;
		}

		public List<WoOkrWorkDeployAuthorizeRecord> getWorkDeployAuthorizeRecords() {
			return workDeployAuthorizeRecords;
		}

		public void setWorkDeployAuthorizeRecords(List<WoOkrWorkDeployAuthorizeRecord> workDeployAuthorizeRecords) {
			this.workDeployAuthorizeRecords = workDeployAuthorizeRecords;
		}

		public List<WoOkrWorkBaseSimpleInfo> getSubWorks() {
			return subWorks;
		}

		public void setSubWorks(List<WoOkrWorkBaseSimpleInfo> subWorks) {
			this.subWorks = subWorks;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Date getArchiveDate() {
			return archiveDate;
		}

		public Date getCompleteTime() {
			return completeTime;
		}

		public void setArchiveDate(Date archiveDate) {
			this.archiveDate = archiveDate;
		}

		public void setCompleteTime(Date completeTime) {
			this.completeTime = completeTime;
		}

		public String getCurrentAppraiseTitle() {
			return currentAppraiseTitle;
		}

		public String getCurrentAppraiseInfoId() {
			return currentAppraiseInfoId;
		}

		public void setCurrentAppraiseTitle(String currentAppraiseTitle) {
			this.currentAppraiseTitle = currentAppraiseTitle;
		}

		public void setCurrentAppraiseInfoId(String currentAppraiseInfoId) {
			this.currentAppraiseInfoId = currentAppraiseInfoId;
		}

		public List<String> getCooperateEmployeeNameList() {
			return cooperateEmployeeNameList == null?new ArrayList<>(): cooperateEmployeeNameList;
		}

		public List<String> getCooperateIdentityList() {
			return cooperateIdentityList == null?new ArrayList<>(): cooperateIdentityList;
		}

		public List<String> getCooperateUnitNameList() {
			return cooperateUnitNameList == null?new ArrayList<>(): cooperateUnitNameList;
		}

		public List<String> getCooperateTopUnitNameList() {
			return cooperateTopUnitNameList == null?new ArrayList<>(): cooperateTopUnitNameList;
		}

		public List<String> getReadLeaderIdentityList() {
			return readLeaderIdentityList == null?new ArrayList<>(): readLeaderIdentityList;
		}

		public List<String> getReadLeaderNameList() {
			return readLeaderNameList == null?new ArrayList<>(): readLeaderNameList;
		}

		public List<String> getReadLeaderUnitNameList() {
			return readLeaderUnitNameList == null?new ArrayList<>(): readLeaderUnitNameList;
		}

		public List<String> getReadLeaderTopUnitNameList() {
			return readLeaderTopUnitNameList == null?new ArrayList<>(): readLeaderTopUnitNameList;
		}

		public void setCooperateEmployeeNameList(List<String> cooperateEmployeeNameList) {
			this.cooperateEmployeeNameList = cooperateEmployeeNameList;
		}

		public void setCooperateIdentityList(List<String> cooperateIdentityList) {
			this.cooperateIdentityList = cooperateIdentityList;
		}

		public void setCooperateUnitNameList(List<String> cooperateUnitNameList) {
			this.cooperateUnitNameList = cooperateUnitNameList;
		}

		public void setCooperateTopUnitNameList(List<String> cooperateTopUnitNameList) {
			this.cooperateTopUnitNameList = cooperateTopUnitNameList;
		}

		public void setReadLeaderIdentityList(List<String> readLeaderIdentityList) {
			this.readLeaderIdentityList = readLeaderIdentityList;
		}

		public void setReadLeaderNameList(List<String> readLeaderNameList) {
			this.readLeaderNameList = readLeaderNameList;
		}

		public void setReadLeaderUnitNameList(List<String> readLeaderUnitNameList) {
			this.readLeaderUnitNameList = readLeaderUnitNameList;
		}

		public void setReadLeaderTopUnitNameList(List<String> readLeaderTopUnitNameList) {
			this.readLeaderTopUnitNameList = readLeaderTopUnitNameList;
		}
	}
}