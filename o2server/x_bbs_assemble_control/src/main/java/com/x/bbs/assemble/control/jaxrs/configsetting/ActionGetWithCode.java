package com.x.bbs.assemble.control.jaxrs.configsetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingCodeEmpty;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingInfoProcess;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingNotExists;
import com.x.bbs.entity.BBSConfigSetting;


public class ActionGetWithCode extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetWithCode.class );
	private String catchNamePrefix = this.getClass().getName();
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		Wo wrap = null;
		Wi wi = null;
		BBSConfigSetting configSetting = null;
		String code = null;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionConfigSettingInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			
			code = wi.getConfigCode();
			
			if( code == null || code.isEmpty() ){
				Exception exception = new ExceptionConfigSettingCodeEmpty();
				result.error( exception );
			}else{
				Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), code);
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
				if( optional.isPresent() ){
					wrap = ( Wo ) optional.get();
					result.setData( wrap );
				}else{
					try {
						configSetting = configSettingService.getWithConfigCode( code );
						if( configSetting != null ){
							wrap = Wo.copier.copy( configSetting );
							CacheManager.put( cacheCategory, cacheKey, wrap );
							result.setData(wrap);
						}else{
							Exception exception = new ExceptionConfigSettingNotExists( code );
							result.error( exception );
						}
					} catch (Exception e) {
						Exception exception = new ExceptionConfigSettingInfoProcess( e, "系统在根据编码获取BBS系统设置信息时发生异常！Code:" + code );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}	
		}
		return result;
	}

	public static class Wi extends BBSConfigSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

	}
	
	public static class Wo extends BBSConfigSetting{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<BBSConfigSetting, Wo> copier = WrapCopierFactory.wo( BBSConfigSetting.class, Wo.class, null, JpaObject.FieldsInvisible	);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}