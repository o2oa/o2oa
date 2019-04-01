package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

public class ActionUpdateReviewForce extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionUpdateReviewForce.class );
	
	protected ActionResult<WoId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WoId> result = new ActionResult<>();
		Document document = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty( id ) ){
			check = false;
			Exception exception = new ExceptionDocumentIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				document = documentInfoServiceAdv.get(id);
				if (document == null) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
				} 
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "文档信息获取操作时发生异常。Id:" + id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				WoId wo = new WoId();
				wo.setId( id );
				result.setData( wo );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "将查询出来的文档信息对象转换为可输出的数据信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		return result;
	}
}