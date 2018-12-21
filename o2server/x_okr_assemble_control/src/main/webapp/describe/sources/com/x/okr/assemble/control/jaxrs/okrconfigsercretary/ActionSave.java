package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionGetTopUnitNameByIdentity;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionGetUnitNameByIdentity;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionSercretaryConfigProcess;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionSercretaryConfigSave;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrConfigSecretary;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrConfigSecretary okrConfigSecretary = null;
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
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}

		if( check && wrapIn != null ){			
			if( check ){
				if( wrapIn.getLeaderIdentity() != null && !wrapIn.getLeaderIdentity().isEmpty() ){
					wrapIn.setLeaderName( okrUserManagerService.getPersonNameByIdentity( wrapIn.getLeaderIdentity() ) );
					if( wrapIn.getLeaderName() == null ) {
						check = false;
						Exception exception = new ExceptionSercretaryConfigProcess( "根据身份未能查询到领导个人信息！身份：" + wrapIn.getLeaderIdentity());
						result.error( exception );
					}
				}else {
					check = false;
					Exception exception = new ExceptionSercretaryConfigProcess( "领导身份属性为空！" );
					result.error( exception );
				}
				if( wrapIn.getSecretaryIdentity() != null && !wrapIn.getSecretaryIdentity().isEmpty() ){
					wrapIn.setSecretaryName( okrUserManagerService.getPersonNameByIdentity( wrapIn.getSecretaryIdentity() ) );
					if( wrapIn.getSecretaryName() == null ) {
						check = false;
						Exception exception = new ExceptionSercretaryConfigProcess( "根据身份未能查询到秘书个人信息！身份：" + wrapIn.getSecretaryIdentity());
						result.error( exception );
					}
				}else {
					check = false;
					Exception exception = new ExceptionSercretaryConfigProcess( "秘书身份属性为空！" );
					result.error( exception );
				}
			}			
			if( check ){
				//补充代理领导所属组织名称和顶层组织名称
				try {
					wrapIn.setLeaderUnitName( okrUserManagerService.getUnitNameByIdentity( wrapIn.getLeaderIdentity() ) );
				} catch (Exception e) {
					Exception exception = new ExceptionGetUnitNameByIdentity( e, wrapIn.getLeaderIdentity() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				try {
					wrapIn.setLeaderTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( wrapIn.getLeaderIdentity() ) );
				} catch (Exception e) {
					Exception exception = new ExceptionGetTopUnitNameByIdentity( e, wrapIn.getLeaderIdentity() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				//补充代理领导所属组织名称和顶层组织名称
				try {
					wrapIn.setSecretaryUnitName( okrUserManagerService.getUnitNameByIdentity( wrapIn.getSecretaryIdentity() ) );
				} catch (Exception e) {
					Exception exception = new ExceptionGetUnitNameByIdentity( e, wrapIn.getSecretaryIdentity() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				try {
					wrapIn.setSecretaryTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( wrapIn.getSecretaryIdentity() ) );
				} catch (Exception e) {
					Exception exception = new ExceptionGetTopUnitNameByIdentity( e, wrapIn.getSecretaryIdentity() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				try {
					okrConfigSecretary = okrConfigSecretaryService.save( wrapIn );
					
					Wo wo = new Wo();
					wo.setId( okrConfigSecretary.getId() );
					result.setData( wo );
					
					if( okrConfigSecretary != null ) {
						WrapInWorkDynamic.sendWithSecretaryConfig( 
								okrConfigSecretary, 
								effectivePerson.getDistinguishedName(),
								okrUserCache.getLoginUserName(),
								okrUserCache.getLoginUserName(),
								"保存领导秘书配置",
								"保存领导秘书配置成功！"
						);
					}
					ApplicationCache.notify( OkrConfigSecretary.class );
				} catch (Exception e) {
					Exception exception = new ExceptionSercretaryConfigSave( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
	public static class Wi extends OkrConfigSecretary {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		private String userName = null;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
	}
	
	public static class Wo extends WoId {

	}
}