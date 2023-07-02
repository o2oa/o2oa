package com.x.calendar.assemble.control.jaxrs.setting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.core.entity.Calendar_Setting;

public class ActionListAll extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListAll.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		String lobValue = null;
		List<Wo> wraps = null;
		List<Calendar_Setting> report_S_SettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				report_S_SettingList = calendar_SettingServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "系统查询所有考勤系统设置信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wraps = Wo.copier.copy( report_S_SettingList );
				if( ListTools.isNotEmpty(wraps) ) {
					for( Wo wo : wraps) {
						if( wo.getIsLob() ) {
							//查询LOB值
							lobValue = calendar_SettingServiceAdv.getLobValueWithId( wo.getId() );
							wo.setConfigValue( lobValue );
						}
					}
				}
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "将所有查询到的考勤配置信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends Calendar_Setting  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Calendar_Setting, Wo> copier = WrapCopierFactory.wo( Calendar_Setting.class, Wo.class, null,Wo.Excludes);
	}
}