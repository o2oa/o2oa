package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;


import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrCenterWorkOperationService;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ExcuteArchive extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteArchive.class );
	private OkrCenterWorkOperationService okrCenterWorkOperationService = new OkrCenterWorkOperationService();
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new CenterWorkIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			okrUserCache = checkUserLogin( effectivePerson.getName() );
			if( okrUserCache == null ){
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkQueryService.get( id );
			} catch (Throwable th) {
				check = false;
				Exception exception = new CenterWorkQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrCenterWorkOperationService.archive( id );
				result.setData( new WrapOutId( id ) );
				if( okrCenterWorkInfo != null ){
					okrWorkDynamicsService.workDynamic(
							okrCenterWorkInfo.getId(), 
							null,
							okrCenterWorkInfo.getTitle(),
							"归档中心工作", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"归档中心工作：" + okrCenterWorkInfo.getTitle(), 
							"中心工作归档成功！"
					);
				}else{
					okrWorkDynamicsService.workDynamic(
							id, 
							null,
							"无",
							"归档中心工作", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"归档中心工作：" + id, 
							"中心工作归档成功！"
					);
				}
			}catch(Exception e){
				Exception exception = new CenterWorkArchiveException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}