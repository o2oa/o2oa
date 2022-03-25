package com.x.mind.assemble.control.jaxrs.mind;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindDelete;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindShareRecordNotExists;
import com.x.mind.entity.MindShareRecord;

/**
 * 根据指定的分享记录取消脑图分享
 * @author O2LEE
 *
 */
public class ActionMindShareCancel extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindShareCancel.class );

	@AuditLog(operation = "取消分享脑图文件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String shareRecordId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		MindShareRecord mindShareRecord = null;
		Boolean check = true;

		if( check ){
			try {
				mindShareRecord = mindInfoService.getMindShareRecord( shareRecordId );
				if( mindShareRecord == null ) {
					check = false;
					Exception exception = new ExceptionMindShareRecordNotExists( shareRecordId );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "系统在根据ID查询脑图分享记录信息时发生异常。", shareRecordId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				mindInfoService.shareCancel( shareRecordId );
				wo.setId( shareRecordId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindDelete( e, "{‘id’:'"+shareRecordId+"'}" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}