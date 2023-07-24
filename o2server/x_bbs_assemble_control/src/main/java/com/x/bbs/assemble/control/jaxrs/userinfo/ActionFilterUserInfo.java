package com.x.bbs.assemble.control.jaxrs.userinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.ExceptionPropertyEmpty;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.ExceptionUserInfoQueryByName;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.ExceptionUserInfoWrapOut;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.entity.BBSUserInfo;

public class ActionFilterUserInfo extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionFilterUserInfo.class );
	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Wo wrap = null;
		BBSUserInfo userInfo = null;
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}

		if( check ){
			if( wrapIn.getUserName() == null ){
				check = false;
				Exception exception = new ExceptionPropertyEmpty( "用户姓名(userName)" );
				result.error( exception );
			}
		}
		//查询用户信息是否存在
		if (check) {
			try {
				userInfo = userInfoService.getByUserName( wrapIn.getUserName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionUserInfoQueryByName( e, wrapIn.getUserName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if (check) {
			if ( userInfo == null ) {
				try {
					userInfo = UserPermissionService.getUserInfoFromCache( wrapIn.getUserName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionUserInfoQueryByName( e, wrapIn.getUserName() );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			try {
				wrap = Wo.copier.copy( userInfo );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionUserInfoWrapOut( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}		
		}
		return result;
	}

	public static class Wi{
		
		private String userName = null;
		
		public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodify );
		
		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
		
	}
	
	public static class Wo extends BBSUserInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSUserInfo, Wo > copier = WrapCopierFactory.wo( BBSUserInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
			
	}
}