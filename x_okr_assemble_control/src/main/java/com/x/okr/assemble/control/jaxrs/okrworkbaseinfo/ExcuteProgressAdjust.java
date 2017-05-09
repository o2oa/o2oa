package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;


import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkNotExistsException;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteProgressAdjust extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteProgressAdjust.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String workId, Integer percent ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		
		if( workId == null || workId.isEmpty() ){
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
		}
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new WorkNotExistsException( workId );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的具体工作信息时发生异常。ID：" + workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				okrWorkBaseInfoOperationService.progressAdjust( workId, percent );
				result.setData( new WrapOutId( workId ) );
				
				if( okrWorkBaseInfo != null ){
					okrWorkDynamicsService.workDynamic(
							okrWorkBaseInfo.getCenterId(),
							okrWorkBaseInfo.getId(), 
							okrWorkBaseInfo.getTitle(),
							"调整工作进展", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"调整工作进展至["+percent+"%]：" + okrWorkBaseInfo.getTitle(), 
							"工作进展调整！"
					);
				}else{
					okrWorkDynamicsService.workDynamic(
							workId, 
							null,
							"无",
							"调整工作进展", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"调整工作进展至["+percent+"%]：" + workId, 
							"工作进展调整！"
					);
				}
			}catch(Exception e){
				Exception exception = new WorkBaseInfoProcessException( e, "工作进度调整操作过程中发生异常。ID:" + workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}