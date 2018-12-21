package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;


import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkArchive;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.assemble.control.service.OkrCenterWorkOperationService;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionArchive extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionArchive.class );
	
	private OkrCenterWorkOperationService okrCenterWorkOperationService = new OkrCenterWorkOperationService();
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new ExceptionCenterWorkIdEmpty();
			result.error( exception );
		}
		if( check ){
			okrUserCache = checkUserLogin( effectivePerson.getDistinguishedName() );
			if( okrUserCache == null ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
				result.error( exception );
			}
		}
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkQueryService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCenterWorkQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrCenterWorkOperationService.archive( id );
				
				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
				
				if( okrCenterWorkInfo != null ){
					WrapInWorkDynamic.sendWithCenterWorkInfo( 
							okrCenterWorkInfo, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginIdentityName(),
							"归档中心工作",
							"中心工作归档成功！"
					);
				}
			}catch(Exception e){
				Exception exception = new ExceptionCenterWorkArchive( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}