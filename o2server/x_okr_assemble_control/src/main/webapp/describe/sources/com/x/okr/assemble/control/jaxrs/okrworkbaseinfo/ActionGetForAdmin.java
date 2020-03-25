package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ActionGetForAdmin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetForAdmin.class );
	
	protected ActionResult<WoOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WoOkrWorkBaseInfo> result = new ActionResult<>();
		WoOkrWorkBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> ids = null;
		String dateTime = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		WoOkrWorkDeployAuthorizeRecord wrapOutOkrWorkDeployAuthorizeRecord = null;
		List<WoOkrWorkDeployAuthorizeRecord> wrapOutOkrWorkDeployAuthorizeRecordList = new ArrayList<>();
		Boolean check = true;

		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionWorkIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的具体工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if(check){
			if( okrWorkBaseInfo != null ){
				try {
					wrap = WoOkrWorkBaseInfo.copier.copy( okrWorkBaseInfo );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkBaseInfoProcess( e, "将查询结果转换为可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				check = false;
				Exception exception = new ExceptionWorkNotExists( id );
				result.error( exception );
			}
		}
		if(check){
			try {
				okrWorkDetailInfo = okrWorkDetailInfoService.get( id );
			} catch ( Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的工作详细信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			//获取该工作所有的授权信息
			try {
				ids = okrWorkAuthorizeRecordService.listByWorkId( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					okrWorkAuthorizeRecordList = okrWorkAuthorizeRecordService.list( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据ID列表查询工作授权信息列表发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}		
		}		
		if( check ){
			if( okrWorkAuthorizeRecordList != null ){
				for( OkrWorkAuthorizeRecord _okrWorkAuthorizeRecord : okrWorkAuthorizeRecordList ){
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
					SortTools.asc( wrapOutOkrWorkDeployAuthorizeRecordList, "operationTime" );
					wrap.setOkrWorkAuthorizeRecords( wrapOutOkrWorkDeployAuthorizeRecordList );
				}
			}
		}		
		if( check ){
			if( okrWorkDetailInfo != null ){
				wrap.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
				wrap.setDutyDescription( okrWorkDetailInfo.getDutyDescription() );
				wrap.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
				wrap.setMajorIssuesDescription( okrWorkDetailInfo.getMajorIssuesDescription() );
				wrap.setProgressAction( okrWorkDetailInfo.getProgressAction() );
				wrap.setProgressPlan( okrWorkDetailInfo.getProgressPlan() );
				wrap.setResultDescription( okrWorkDetailInfo.getResultDescription() );
				result.setData(wrap);
			}else{
				logger.warn( "system can not get any okrWorkDetailInfo by {'id':'"+id+"'}. " );
			}
		}
		
		return result;
	}
}