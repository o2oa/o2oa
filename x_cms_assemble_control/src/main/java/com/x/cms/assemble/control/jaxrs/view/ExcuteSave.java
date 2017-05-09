package com.x.cms.assemble.control.jaxrs.view;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.view.exception.ViewInfoAppIdEmptyException;
import com.x.cms.assemble.control.jaxrs.view.exception.ViewInfoFormIdEmptyException;
import com.x.cms.assemble.control.jaxrs.view.exception.ViewInfoSaveException;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ExcuteSave extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ViewAction.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInView wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		View view = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn.getFormId() == null || wrapIn.getFormId().isEmpty() ){
				check = false;
				Exception exception = new ViewInfoFormIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getAppId() == null || wrapIn.getAppId().isEmpty() ){
				check = false;
				Exception exception = new ViewInfoAppIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try {
				view = viewServiceAdv.save( wrapIn, effectivePerson );							
				wrap = new WrapOutId( view.getId() );
				new LogService().log( null, effectivePerson.getName(), view.getName(), view.getAppId(), "", "", view.getId(), "VIEW", "保存");
				
				ApplicationCache.notify( View.class );
				ApplicationCache.notify( ViewFieldConfig.class );
				ApplicationCache.notify( ViewCategory.class );
				
				result.setData(wrap);
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ViewInfoSaveException(e);
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}