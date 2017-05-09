package com.x.bbs.assemble.control.jaxrs.configsetting;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingCodeEmptyException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingNotExistsException;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Element;

public class ExcuteGetWithCode extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGetWithCode.class );
	private String catchNamePrefix = this.getClass().getName();
	
	protected ActionResult<WrapOutBBSConfigSetting> execute( HttpServletRequest request, EffectivePerson effectivePerson, String code ) throws Exception {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<WrapOutBBSConfigSetting>();
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		String cacheKey = catchNamePrefix + "#code#" + code;
		Boolean check = true;
		
		if( check ){
			if( code == null || code.isEmpty() ){
				Exception exception = new ConfigSettingCodeEmptyException();
				result.error( exception );
			}else{
				Element element = null;
				element = cache.get( cacheKey );
				if( element != null ){
					wrap = ( WrapOutBBSConfigSetting ) element.getObjectValue();
					result.setData( wrap );
				}else{
					try {
						configSetting = configSettingService.getWithConfigCode( code );
						if( configSetting != null ){
							wrap = WrapTools.configSetting_wrapout_copier.copy( configSetting );
							cache.put( new Element( cacheKey, wrap ) );
							result.setData(wrap);
						}else{
							Exception exception = new ConfigSettingNotExistsException( code );
							result.error( exception );
						}
					} catch (Exception e) {
						Exception exception = new ConfigSettingInfoProcessException( e, "系统在根据编码获取BBS系统设置信息时发生异常！Code:" + code );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}	
		}
		return result;
	}

}