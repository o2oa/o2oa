package com.x.okr.assemble.control.jaxrs.okrworkchat;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatDelete;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatQueryById;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrWorkChat;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkChat okrWorkChat = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
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
		
		if( check ) {
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionWorkIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ) {
			try {
				okrWorkChat = okrWorkChatService.get( id );
				if( okrWorkChat == null ){
					Exception exception = new ExceptionWorkChatNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkChatQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			try{
				okrWorkChatService.delete( id );
				result.setData( new Wo(id) );
			}catch(Exception e){
				Exception exception = new ExceptionWorkChatDelete( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			if( okrWorkChat != null ) {
				WrapInWorkDynamic.sendWithWorkChat( okrWorkChat, 
						effectivePerson.getDistinguishedName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"删除工作交流信息", 
						"工作交流信息删除成功！"
				);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}