package com.x.report.assemble.control.jaxrs.setting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingCodeEmpty;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;
import com.x.report.core.entity.Report_S_Setting;

public class ActionGetWithCode extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGetWithCode.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String code ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Report_S_Setting report_S_Setting = null;
		Boolean check = true;
		
		if( check ){
			if( code == null || code.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSettingCodeEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				report_S_Setting = report_S_SettingServiceAdv.getByCode( code );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "系统根据指定的编码'CODE'查询所有符合条件的考勤系统设置信息列表时发生异常.Code:" + code );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = Wo.copier.copy( report_S_Setting );
				if( wrap.getIsLob() ) {
					//查询LOB值
					String lobValue = report_S_SettingServiceAdv.getLobValueWithId( wrap.getId() );
					wrap.setConfigValue( lobValue );
				}
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "将所有查询到的考勤配置信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends Report_S_Setting  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_S_Setting, Wo> copier = WrapCopierFactory.wo( Report_S_Setting.class, Wo.class, null,Wo.Excludes);
	}

}