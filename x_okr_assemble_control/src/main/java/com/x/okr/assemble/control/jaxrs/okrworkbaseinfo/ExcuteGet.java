package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.WrapOutOkrWorkAuthorizeRecord;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkNotExistsException;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> ids = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<WrapOutOkrWorkAuthorizeRecord> wrapOutOkrWorkAuthorizeRecordList = null;
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
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
				if( okrWorkBaseInfo != null ){
					wrap = wrapout_copier.copy( okrWorkBaseInfo );
				}else{
					check = false;
					Exception exception = new WorkNotExistsException( id );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的具体工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
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
		if(check){
			try {
				//获取该工作和当前责任人相关的授权信息
				okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( okrWorkBaseInfo.getId(), okrUserCache.getLoginIdentityName(), "正常" );
				if( okrWorkAuthorizeRecord != null ){
					wrap.setOkrWorkAuthorizeRecord( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecord ) );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。Person: "+ okrUserCache.getLoginIdentityName() +", ID：" + okrWorkBaseInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if(check){
			try {
				//获取该工作所有的授权信息
				ids = okrWorkAuthorizeRecordService.listByWorkId( id );
				if( ids != null && !ids.isEmpty() ){
					okrWorkAuthorizeRecordList = okrWorkAuthorizeRecordService.list( ids );
					if( okrWorkAuthorizeRecordList != null ){
						wrapOutOkrWorkAuthorizeRecordList = okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecordList );
						SortTools.asc( wrapOutOkrWorkAuthorizeRecordList, "createTime" );
						wrap.setOkrWorkAuthorizeRecords( wrapOutOkrWorkAuthorizeRecordList );
					}
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。ID：" + okrWorkBaseInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData(wrap);
		return result;
	}
	
}