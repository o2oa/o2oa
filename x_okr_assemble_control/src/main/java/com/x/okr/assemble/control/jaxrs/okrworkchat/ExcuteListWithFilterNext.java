package com.x.okr.assemble.control.jaxrs.okrworkchat;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;

public class ExcuteListWithFilterNext extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWithFilterNext.class );
	
	protected ActionResult<List<WrapOutOkrWorkChat>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id, Integer count, WrapInFilterWorkChat wrapIn ) throws Exception {
		ActionResult<List<WrapOutOkrWorkChat>> result = new ActionResult<List<WrapOutOkrWorkChat>>();
		List<WrapOutOkrWorkChat> wrapOutOkrWorkChatList = null;
		List<OkrWorkChat> chatList = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Long total = 0L;
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
		if( count == null ){
			count = 20;
		}
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
			//对wrapIn里的信息进行校验
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
			try{
				chatList = okrWorkChatService.listChatNextWithFilter( id, count, wrapIn );
				total = okrWorkChatService.getChatCountWithFilter(wrapIn);
				wrapOutOkrWorkChatList = wrapout_copier.copy(chatList);	
				result.setData( wrapOutOkrWorkChatList );
				result.setCount( total );
			}catch(Throwable th){
				Exception exception = new WorkChatFilterException( th );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}