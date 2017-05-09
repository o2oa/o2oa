package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.GetCompanyNameByIdentityException;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.GetDepartmentNameByIdentityException;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.SercretaryConfigLeaderIdentityEmptyException;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.SercretaryConfigSaveException;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.UserNoLoginException;
import com.x.okr.entity.OkrConfigSecretary;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInOkrConfigSecretary wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrConfigSecretary okrConfigSecretary = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}

		if( wrapIn != null ){
			if( check ){
				if( okrUserCache.getLoginUserOrganizationName()  != null && !okrUserCache.getLoginUserOrganizationName().isEmpty() ){
					wrapIn.setSecretaryOrganizationName( okrUserCache.getLoginUserOrganizationName() );
				}else{
					check = false;
					Exception exception = new UserNoLoginException( effectivePerson.getName() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				if( okrUserCache.getLoginUserCompanyName()  != null && !okrUserCache.getLoginUserCompanyName().isEmpty() ){
					wrapIn.setSecretaryCompanyName( okrUserCache.getLoginUserCompanyName() );
				}else{
					check = false;
					Exception exception = new UserNoLoginException( effectivePerson.getName() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				if( wrapIn.getLeaderIdentity() == null || wrapIn.getLeaderIdentity().isEmpty() ){
					check = false;
					Exception exception = new SercretaryConfigLeaderIdentityEmptyException();
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}else{
					//补充代理领导所属组织名称和公司名称
					try {
						wrapIn.setLeaderOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrapIn.getLeaderIdentity() ) );
					} catch (Exception e) {
						Exception exception = new GetDepartmentNameByIdentityException( e, wrapIn.getLeaderIdentity() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					try {
						wrapIn.setLeaderCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrapIn.getLeaderIdentity() ) );
					} catch (Exception e) {
						Exception exception = new GetCompanyNameByIdentityException( e, wrapIn.getLeaderIdentity() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			if( check ){
				try {
					okrConfigSecretary = okrConfigSecretaryService.save( wrapIn );
					result.setData( new WrapOutId( okrConfigSecretary.getId() ) );
					
					ApplicationCache.notify( OkrConfigSecretary.class );
				} catch (Exception e) {
					Exception exception = new SercretaryConfigSaveException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
}