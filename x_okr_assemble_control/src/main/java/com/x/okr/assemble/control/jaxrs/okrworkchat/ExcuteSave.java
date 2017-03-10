package com.x.okr.assemble.control.jaxrs.okrworkchat;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkChat wrapIn ) throws Exception {
		ActionResult< WrapOutId > result = new ActionResult<>();
		OkrWorkChat okrWorkChat = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch ( Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
//		if( wrapIn == null ){
//			check = false;
//			result.error( new Exception( "系统未获取到需要保存的信息，操作无法继续！" ) );
//			result.setUserMessage( "系统未获取到需要保存的信息，操作无法继续！" );
//		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName()  );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		
		if( check ){
			//校验工作ID是否合法
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				Exception exception = new WorkIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new WorkNotExistsException( wrapIn.getWorkId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
				result.setData( new WrapOutId( okrWorkChat.getId() ) );
				okrWorkDynamicsService.workChatDynamic(
						okrWorkBaseInfo, 
						"发送工作交流", 
						effectivePerson.getName(),
						okrUserCache.getLoginUserName(),
						okrUserCache.getLoginIdentityName() , 
						okrWorkChat.getContent(),
						"工作交流发送成功！");
			} catch (Exception e) {
				result.error( e );
				Exception exception = new WorkChatSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}