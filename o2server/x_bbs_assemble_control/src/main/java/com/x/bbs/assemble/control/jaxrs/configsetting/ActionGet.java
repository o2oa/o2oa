package com.x.bbs.assemble.control.jaxrs.configsetting;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingIdEmpty;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingInfoProcess;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingNotExists;
import com.x.bbs.entity.BBSConfigSetting;

public class ActionGet extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSConfigSetting configSetting = null;
		
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionConfigSettingIdEmpty();
			result.error( exception );
		}else{
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				wrap = (Wo) optional.get();
				result.setData( wrap );
			}else{
				try {
					configSetting = configSettingService.get( id );
					if( configSetting != null ){
						wrap = Wo.copier.copy( configSetting );
						CacheManager.put( cacheCategory, cacheKey, wrap );
						result.setData(wrap);
					}else{
						Exception exception = new ExceptionConfigSettingNotExists( id );
						result.error( exception );
					}
				} catch ( Exception e ) {
					Exception exception = new ExceptionConfigSettingInfoProcess( e, "系统在根据ID获取BBS系统设置信息时发生异常！ID:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}	
		}
		return result;
	}

	public static class Wo extends BBSConfigSetting{

		private static final long serialVersionUID = -5076990764713538973L;


		public static WrapCopier<BBSConfigSetting, Wo> copier = WrapCopierFactory.wo( BBSConfigSetting.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}