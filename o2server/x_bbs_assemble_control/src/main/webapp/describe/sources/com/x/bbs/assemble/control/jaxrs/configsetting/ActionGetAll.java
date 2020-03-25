package com.x.bbs.assemble.control.jaxrs.configsetting;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingInfoProcess;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Element;

public class ActionGetAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetAll.class );
	private String catchNamePrefix = this.getClass().getName();
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wraps = null;
		List<BBSConfigSetting> configSettingList = null;
		String cacheKey = catchNamePrefix + "#all";
		Element element = null;
		
		element = cache.get( cacheKey );
		
		if( element != null ){
			wraps = ( List<Wo> ) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				configSettingList = configSettingService.listAll();
				if( configSettingList != null ){
					wraps = Wo.copier.copy( configSettingList );
					SortTools.asc( wraps, true, "orderNumber");
					cache.put( new Element( cacheKey, wraps ) );
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