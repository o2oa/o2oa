package com.x.cms.assemble.control.jaxrs.appinfo;

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
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		AppInfo appInfo = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new AppInfoIdEmptyException();
			result.error( exception );
		}
		if( check ){
			try {
				appInfo = appInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppInfoQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( appInfo == null ){
				check = false;
				Exception exception = new AppInfoNotExistsException( id );
				result.error( exception );
			}
		}
		if( check ){
			Long count = 0L;
			try {
				count = appInfoServiceAdv.countCategoryByAppId( id );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new CountCategoryByAppIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if ( count > 0 ){
				check = false;
				Exception exception = new AppInfoCanNotDeleteException( "该应用栏目内仍存在"+ count +"个分类，请删除分类后再删除栏目信息！" );
				result.error( exception );
			}
		}
		if( check ){
			try {
				appInfoServiceAdv.delete( id, effectivePerson );
				
				new LogService().log( null, effectivePerson.getName(), appInfo.getAppName(), id, "", "", "", "APPINFO", "删除");
				
				//更新缓存
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( AppDict.class );
				ApplicationCache.notify( AppDictItem.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( AppCategoryPermission.class );
				ApplicationCache.notify( AppCategoryAdmin.class );
				ApplicationCache.notify( View.class );
				ApplicationCache.notify( ViewCategory.class );
				ApplicationCache.notify( ViewFieldConfig.class );
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( DocumentPermission.class );
				
				result.setData( new WrapOutId(appInfo.getId()) );
			} catch (Exception e) {
				Exception exception = new AppInfoDeleteException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}

}