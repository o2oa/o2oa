package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfoConfig;

public class ActionGetConfig extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetConfig.class );
	
	protected ActionResult<WoText> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WoText> result = new ActionResult<>();
		WoText woText = new WoText();
		AppInfoConfig appInfoConfig = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty(id) ){
			check = false;
			Exception exception = new ExceptionAppInfoIdEmpty();
			result.error( exception );
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((WoText)optional.get());
		} else {
			if( check ){
				try {
					appInfoConfig = appInfoServiceAdv.getConfigObject( id );
					if( appInfoConfig == null ){
						woText.setText("{}");
					}else{
						woText.setText( appInfoConfig.getConfig() );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAppInfoProcess( e, "根据指定id查询栏目配置支持信息对象时发生异常。id:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				CacheManager.put(cacheCategory, cacheKey, woText);
				result.setData( woText );
			}
		}
		
		return result;
	}
}