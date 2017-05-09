package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutCount;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentIdEmptyException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;

public class ExcuteGetViewCount extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGetViewCount.class );
	
	protected ActionResult<WrapOutCount> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		Long count = 0L;
		Boolean check = true;
		WrapOutCount wrapOutCount = new WrapOutCount();
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new DocumentIdEmptyException();
			result.error( exception );
		}
		
		if( check ){
			try {
				count = documentServiceAdv.getViewCount( id );
				if( count == null ){
					count = 0L;
				}
				wrapOutCount.setCount(count);
				result.setData(wrapOutCount);
			} catch (Exception e) {
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "系统在查询文档访问次数时发生异常。Id:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}

}