package com.x.cms.assemble.control.jaxrs.categoryinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.element.ViewCategory;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new CategoryInfoIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new CategoryInfoQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( categoryInfo == null ){
				check = false;
				Exception exception = new CategoryInfoNotExistsException( id );
				result.error( exception );
			}
		}
		if( check ){
			Long count = documentServiceAdv.countByCategoryId( id );
			if ( count > 0  ){
				Exception exception = new CategoryInfoEditNotAllowedException( "该分类中仍有"+ count +"个文档，请删除所有文档后再删除分类信息！" );
				result.error( exception );
			}
		}
		if( check ){
			try {
				categoryInfoServiceAdv.delete( id, effectivePerson );
				wrap = new WrapOutId(categoryInfo.getId());
				
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( ViewCategory.class );
				ApplicationCache.notify( AppCategoryPermission.class );
				ApplicationCache.notify( AppCategoryAdmin.class );
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( DocumentPermission.class );
				
				new LogService().log( null, effectivePerson.getName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), id, "", "", "", "CATEGORY", "删除");
				
				result.setData(wrap);
			} catch ( Exception e ) {
				check = false;
				Exception exception = new CategoryInfoDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}