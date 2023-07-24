package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author sword
 */
public class ActionEraseDocumentWithCategory extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionEraseDocumentWithCategory.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CategoryInfo categoryInfo = categoryInfoServiceAdv.get(id);
		if(categoryInfo == null){
			throw new ExceptionCategoryInfoNotExists( id );
		}
		AppInfo appInfo = appInfoServiceAdv.get(categoryInfo.getAppId());
		Business business = new Business(null);
		if (!business.isAppInfoManager(effectivePerson, appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		//查询分类下的文档数量
		Long count = documentServiceAdv.countByCategoryId(id, true);
		List<String> idsForDelete = null;
		Integer queryMaxCount = 1000;
		Integer whileCount = 0;
		Integer currentWhileCount = 0;

		if ( count > 0 ) {
			logger.info(">>>>一共需要删除"+count+"个文档。");
			result.setCount(count);
			whileCount =  (int) (count/queryMaxCount + 1);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				//循环清除分类下所有的文档信息
				while( count > 0 && currentWhileCount<=whileCount ) {
					logger.info(">>>>正在根据categoryId查询"+queryMaxCount+"个需要删除的文档ID列表。");
					idsForDelete = documentServiceAdv.listIdsByCategoryId( id, queryMaxCount );
					if( ListTools.isNotEmpty(  idsForDelete )) {
						for( String docId : idsForDelete ) {
							documentInfoService.delete(emc, docId);
						}
						CacheManager.notify( Document.class );
					}
					count = documentServiceAdv.countByCategoryId( id, true );
					logger.info(">>>>已经删除"+idsForDelete.size()+"个文档，还剩下"+count+"个文档需要删除。");
				}
			}
		}

		Wo wo = new Wo();
		wo.setId(id);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}
}
