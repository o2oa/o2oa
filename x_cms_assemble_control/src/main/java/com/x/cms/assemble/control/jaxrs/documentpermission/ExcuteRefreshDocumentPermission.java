package com.x.cms.assemble.control.jaxrs.documentpermission;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

public class ExcuteRefreshDocumentPermission extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteRefreshDocumentPermission.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInDocumentPermission wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Document document = null;
		Boolean check = true;		

		if( check ){
			if( wrapIn.getDocId() == null || wrapIn.getDocId().isEmpty() ){
				check = false;
				Exception exception = new ServiceLogicException( "文档ID为空，无法为文档添加权限。" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getPermissionList() == null || wrapIn.getPermissionList().isEmpty() ){
				check = false;
				Exception exception = new ServiceLogicException( "文档权限为空，该文档将没有任何用户可以访问。ID：" + wrapIn.getDocId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				document = documentServiceAdv.get( wrapIn.getDocId() );
				if( document == null ){
					check = false;
					Exception exception = new ServiceLogicException( "文档不存在。ID：" + wrapIn.getDocId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ServiceLogicException( e,"系统在根据文档ID查询文档信息时发生异常。ID：" + wrapIn.getDocId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				documentPermissionServiceAdv.refreshDocumentPermission( document, wrapIn.getPermissionList() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ServiceLogicException( e,"系统在为文档设置用户访问权限过程中发生异常。ID：" + wrapIn.getDocId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
			
		}
		return result;
	}
	
}