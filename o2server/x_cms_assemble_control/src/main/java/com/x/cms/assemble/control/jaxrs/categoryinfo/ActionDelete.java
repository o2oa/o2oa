package com.x.cms.assemble.control.jaxrs.categoryinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.ViewCategory;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		
		if ( StringUtils.isEmpty( id )) {
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error(exception);
		}
		if (check) {
			try {
				categoryInfo = categoryInfoServiceAdv.get(id);
				if (categoryInfo == null) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "根据ID查询分类信息对象时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			Long count = documentServiceAdv.countByCategoryId( id );
			if ( count > 0 ) {
				Exception exception = new ExceptionEditNotAllowed( "该分类中仍有" + count + "个文档，请删除所有文档后再删除分类信息！");
				result.error(exception);
			}
		}
		
		if (check) {
			try {
				categoryInfoServiceAdv.delete( id, effectivePerson );				
				
				Wo wo = new Wo();
				wo.setId( categoryInfo.getId() );
				result.setData( wo );				
				
				//增加删除栏目批量操作（对分类和文档）的信息
				new CmsBatchOperationPersistService().addOperation( 
						CmsBatchOperationProcessService.OPT_OBJ_CATEGORY, 
						CmsBatchOperationProcessService.OPT_TYPE_DELETE, id, id, "删除分类：ID=" + id );
				
				new LogService().log(null, effectivePerson.getDistinguishedName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), id, "", "", "", "CATEGORY", "删除");
				
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( ViewCategory.class );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "分类信息在删除时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}