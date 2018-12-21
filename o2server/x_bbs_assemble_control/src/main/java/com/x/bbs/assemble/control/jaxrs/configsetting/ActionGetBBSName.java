package com.x.bbs.assemble.control.jaxrs.configsetting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingInfoProcess;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingNotExists;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Element;

public class ActionGetBBSName extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetBBSName.class );
	private String catchNamePrefix = this.getClass().getName();
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSConfigSetting configSetting = null;
		
		String cacheKey = catchNamePrefix + "#code#BBS_LOGO_NAME";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wrap = (Wo) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				configSetting = configSettingService.getWithConfigCode( "BBS_LOGO_NAME" );
				if( configSetting != null ){
					wrap = Wo.copier.copy( configSetting );
					cache.put( new Element( cacheKey, wrap ) );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionConfigSettingNotExists( "BBS_LOGO_NAME" );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionConfigSettingInfoProcess( e, "系统在根据编码获取BBS系统设置信息时发生异常！Code:" + "BBS_LOGO_NAME" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}	
		return result;
	}

	public static class Wo extends BBSConfigSetting{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

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