package com.x.bbs.assemble.control.jaxrs.configsetting;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingCodeEmptyException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingNotExistsException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingValueEmptyException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.InsufficientPermissionsException;
import com.x.bbs.entity.BBSConfigSetting;


public class ExcuteUpdate extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteUpdate.class );
	
	protected ActionResult<WrapOutBBSConfigSetting> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInBBSConfigSetting wrapIn ) throws Exception {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		Boolean check = true;

		if( check ){
			try {
				if( !userManagerService.isHasRole( effectivePerson.getName(), "BBSSystemAdmin") && !effectivePerson.isManager() ){
					check = false;
					logger.warn("用户没有BBSSystemAdmin角色，并且也不是系统管理员！USER：" + effectivePerson.getName() );
					Exception exception = new InsufficientPermissionsException( effectivePerson.getName(), "BBSSystemAdmin" );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new InsufficientPermissionsException( effectivePerson.getName(), "BBSSystemAdmin" );
				result.error( exception );
				result.error( e );
			}
		}
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				check = false;
				Exception exception = new ConfigSettingCodeEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			if( wrapIn.getConfigValue() == null || wrapIn.getConfigValue().isEmpty() ){
				check = false;
				Exception exception = new ConfigSettingValueEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try {
				configSetting = configSettingService.getWithConfigCode( wrapIn.getConfigCode() );
				if( configSetting == null ){
					check = false;
					Exception exception = new ConfigSettingNotExistsException( wrapIn.getConfigCode() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ConfigSettingInfoProcessException( e, "系统在根据编码获取BBS系统设置信息时发生异常！Code:" + wrapIn.getConfigCode() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				configSetting.setConfigValue( wrapIn.getConfigValue() );
				configSetting = configSettingService.update( configSetting );
			} catch (Exception e) {
				Exception exception = new ConfigSettingInfoProcessException( e, "根据ID更新BBS系统设置信息时发生异常.ID:" + wrapIn.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( configSetting != null ){
				try {
					wrap = WrapTools.configSetting_wrapout_copier.copy( configSetting );
					result.setData( wrap );
				} catch (Exception e) {
					Exception exception = new ConfigSettingInfoProcessException( e, "系统在转换所有BBS系统设置信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		notifyCache();
		return result;
	}

	private void notifyCache() throws Exception {
		ApplicationCache.notify( BBSConfigSetting.class );
	}
}