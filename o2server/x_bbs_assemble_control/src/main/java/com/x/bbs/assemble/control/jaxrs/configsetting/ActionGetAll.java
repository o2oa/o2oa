package com.x.bbs.assemble.control.jaxrs.configsetting;

import java.util.List;
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
import com.x.base.core.project.tools.SortTools;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingInfoProcess;
import com.x.bbs.entity.BBSConfigSetting;

public class ActionGetAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetAll.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wraps = null;
		List<BBSConfigSetting> configSettingList = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
		if( optional.isPresent() ){
			wraps = ( List<Wo> ) optional.get();
			result.setData( wraps );
		}else{
			try {
				configSettingList = configSettingService.listAll();
				if( configSettingList != null ){
					wraps = Wo.copier.copy( configSettingList );
					SortTools.asc( wraps, true, "orderNumber");
					CacheManager.put( cacheCategory, cacheKey, wraps );
					result.setData( wraps );
				}
			} catch ( Exception e ) {
				Exception exception = new ExceptionConfigSettingInfoProcess( e, "系统在获取所有BBS系统设置信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends BBSConfigSetting{

		private static final long serialVersionUID = -5076990764713538973L;


		public static WrapCopier<BBSConfigSetting, Wo> copier = WrapCopierFactory.wo( BBSConfigSetting.class, Wo.class, null,JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}

}