package com.x.okr.assemble.control.jaxrs.okrworkchat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatSave;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	public ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult< Wo > result = new ActionResult<>();
		OkrWorkChat okrWorkChat = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Boolean check = true;
		Wi wrapIn = null;
		OkrUserCache  okrUserCache  = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
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
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
				result.error( exception );
			}
		}		
		
		if( check ){
			//校验工作ID是否合法
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				Exception exception = new ExceptionWorkIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkNotExists( wrapIn.getWorkId() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkQueryById( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			wrapIn.setSenderName( okrUserCache.getLoginUserName() );
			wrapIn.setSenderIdentity( okrUserCache.getLoginIdentityName() );
			wrapIn.setCenterId(  okrWorkBaseInfo.getCenterId() );
			wrapIn.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
			wrapIn.setWorkId( okrWorkBaseInfo.getId() );
			wrapIn.setWorkTitle( okrWorkBaseInfo.getTitle() );
		}
		
		if( check ){
			try {
				okrWorkChat = okrWorkChatService.save( wrapIn );
				result.setData( new Wo( okrWorkChat.getId() ) );				
			} catch (Exception e) {
				result.error( e );
				Exception exception = new ExceptionWorkChatSave( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( okrWorkChat != null ) {
				WrapInWorkDynamic.sendWithWorkChat( okrWorkChat, 
						effectivePerson.getDistinguishedName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"发送工作交流信息", 
						"工作交流信息发送成功！"
				);
			}
		}
		return result;
	}
	
	public static class Wi extends OkrWorkChat {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}