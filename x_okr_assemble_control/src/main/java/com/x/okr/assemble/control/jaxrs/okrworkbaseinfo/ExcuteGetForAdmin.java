package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkNotExistsException;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ExcuteGetForAdmin extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGetForAdmin.class );
	
	protected ActionResult<WrapOutOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> ids = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		Boolean check = true;

		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new WorkIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的具体工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if(check){
			if( okrWorkBaseInfo != null ){
				try {
					wrap = wrapout_copier.copy( okrWorkBaseInfo );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkBaseInfoProcessException( e, "将查询结果转换为可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				check = false;
				Exception exception = new WorkNotExistsException( id );
				result.error( exception );
			}
		}
		if(check){
			try {
				okrWorkDetailInfo = okrWorkDetailInfoService.get( id );
			} catch ( Exception e) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的工作详细信息时发生异常。ID：" + id );
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
				Exception exception = new WorkBaseInfoProcessException( e, "系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。ID：" + id );
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
					Exception exception = new WorkBaseInfoProcessException( e, "系统根据ID列表查询工作授权信息列表发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}		
		}		
		if( check ){
			if( okrWorkAuthorizeRecordList != null ){
				try {
					wrap.setOkrWorkAuthorizeRecords( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecordList ) );
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkBaseInfoProcessException( e, "将工作授权记录查询结果转换为可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
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