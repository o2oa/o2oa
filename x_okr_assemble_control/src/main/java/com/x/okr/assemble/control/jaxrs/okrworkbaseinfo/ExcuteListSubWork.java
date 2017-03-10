package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ExcuteListSubWork extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListSubWork.class );
	
	protected ActionResult<List<WrapOutOkrWorkBaseInfo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<List<WrapOutOkrWorkBaseInfo>>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<String> ids = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if(check){
			try {
				ids = okrWorkBaseInfoService.listByParentId( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubWorkQueryByPidException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if(check){
			if( ids != null && !ids.isEmpty()){
				try {
					okrWorkBaseInfoList = okrWorkBaseInfoService.listByIds(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		
		if(check){
			if( okrWorkBaseInfoList != null && !okrWorkBaseInfoList.isEmpty()){
				try {
					wraps = wrapout_copier.copy(okrWorkBaseInfoList);
				} catch (Exception e) {
					Exception exception = new WorkWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		
		if(check){
			if( wraps != null && !wraps.isEmpty() ){
				for( WrapOutOkrWorkBaseInfo wrapOutOkrWorkBaseInfo : wraps ){
					try {
						okrWorkDetailInfo = okrWorkDetailInfoService.get( wrapOutOkrWorkBaseInfo.getId() );
						if( okrWorkDetailInfo != null ){
							wrapOutOkrWorkBaseInfo.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
							wrapOutOkrWorkBaseInfo.setDutyDescription( okrWorkDetailInfo.getDutyDescription() );
							wrapOutOkrWorkBaseInfo.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
							wrapOutOkrWorkBaseInfo.setMajorIssuesDescription( okrWorkDetailInfo.getMajorIssuesDescription() );
							wrapOutOkrWorkBaseInfo.setProgressAction( okrWorkDetailInfo.getProgressAction() );
							wrapOutOkrWorkBaseInfo.setProgressPlan( okrWorkDetailInfo.getProgressPlan() );
							wrapOutOkrWorkBaseInfo.setResultDescription( okrWorkDetailInfo.getResultDescription() );
						}
					} catch (Exception e) {
						Exception exception = new WorkDetailQueryByIdException( e, wrapOutOkrWorkBaseInfo.getId() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}
		}
		result.setData(wraps);
		
		return result;
	}
	
}