package com.x.report.assemble.control.jaxrs.extcontent;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingIdEmpty;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;

public class ActionDeleteExtContent extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionDeleteExtContent.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSettingIdEmpty();
				result.error( exception );
			}
		}

		if( check ){
			try {
				report_I_Ext_ContentServiceAdv.delete( id, effectivePerson );
				wrap = new Wo(id);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "系统根据ID查询指定服务客户信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}