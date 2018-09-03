package com.x.calendar.assemble.control.jaxrs.event;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.core.entity.Calendar_Event;

public class ActionGetRFCContent extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGetRFCContent.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		String content = null;
		Calendar_Event calendar_Event = null;
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionEventPropertyEmpty("ID");
				result.error( exception );
			}
		}

		if( check ){
			try {
				calendar_Event = calendar_EventServiceAdv.get( id );
				if( calendar_Event == null ) {
					check = false;
					Exception exception = new ExceptionEventNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历记录信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				content = calendar_EventServiceAdv.getiCalContent(calendar_Event);
				wrap.setValue( content );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统在获取日程信息的RFC内容时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData( wrap );
		return result;
	}

	public static class Wo extends WrapString  {
	}

}