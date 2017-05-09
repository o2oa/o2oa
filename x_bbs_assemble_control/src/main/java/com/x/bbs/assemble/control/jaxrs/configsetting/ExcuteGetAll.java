package com.x.bbs.assemble.control.jaxrs.configsetting;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingInfoProcessException;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Element;

public class ExcuteGetAll extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGetAll.class );
	private String catchNamePrefix = this.getClass().getName();
	
	protected ActionResult<List<WrapOutBBSConfigSetting>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutBBSConfigSetting>> result = new ActionResult<List<WrapOutBBSConfigSetting>>();
		List<WrapOutBBSConfigSetting> wraps = null;
		List<BBSConfigSetting> configSettingList = null;
		String cacheKey = catchNamePrefix + "#all";
		Element element = null;
		
		element = cache.get( cacheKey );
		
		if( element != null ){
			wraps = ( List<WrapOutBBSConfigSetting> ) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				configSettingList = configSettingService.listAll();
				if( configSettingList != null ){
					wraps = WrapTools.configSetting_wrapout_copier.copy( configSettingList );
					SortTools.asc( wraps, true, "orderNumber");
					cache.put( new Element( cacheKey, wraps ) );
					result.setData( wraps );
				}
			} catch ( Exception e ) {
				Exception exception = new ConfigSettingInfoProcessException( e, "系统在获取所有BBS系统设置信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}