package com.x.bbs.assemble.control.jaxrs.configsetting;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingNotExistsException;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	private String catchNamePrefix = this.getClass().getName();
	
	protected ActionResult<WrapOutBBSConfigSetting> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		
		if( id == null || id.isEmpty() ){
			Exception exception = new ConfigSettingIdEmptyException();
			result.error( exception );
		}else{
			String cacheKey = catchNamePrefix + "#id#" + id;
			Element element = null;
			element = cache.get( cacheKey );
			if( element != null ){
				wrap = (WrapOutBBSConfigSetting) element.getObjectValue();
				result.setData( wrap );
			}else{
				try {
					configSetting = configSettingService.get( id );
					if( configSetting != null ){
						wrap = WrapTools.configSetting_wrapout_copier.copy( configSetting );
						cache.put( new Element( cacheKey, wrap ) );
						result.setData(wrap);
					}else{
						Exception exception = new ConfigSettingNotExistsException( id );
						result.error( exception );
					}
				} catch ( Exception e ) {
					Exception exception = new ConfigSettingInfoProcessException( e, "系统在根据ID获取BBS系统设置信息时发生异常！ID:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}	
		}
		return result;
	}

}