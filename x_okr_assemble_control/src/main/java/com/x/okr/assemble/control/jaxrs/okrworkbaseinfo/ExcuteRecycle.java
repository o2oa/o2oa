package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;

public class ExcuteRecycle extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteRecycle.class );
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	
	protected ActionResult<WrapOutOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
		}else{
			try{
				okrWorkBaseInfoOperationService.recycleWork( id );
			}catch(Exception e){
				Exception exception = new WorkBaseInfoProcessException( e, "将指定ID的具体工作撤回时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}