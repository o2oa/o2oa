package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.core.entity.Document;

public class ActionPersistDeleteWithBatch extends BaseAction {
	
	protected static final int SQL_STATEMENT_IN_BATCH = 1000;
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String importBatchName, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> docIds = null;
		List<Document> documentList = null;
		String appId = null;
		String categoryId = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			
			docIds = business.getDocumentFactory().listWithImportBatchName( importBatchName );
			int i=0;
			if( ListTools.isNotEmpty( docIds )) {
				emc.beginTransaction( Document.class );
				for (List<String> _batch_docIds : ListTools.batch( docIds, SQL_STATEMENT_IN_BATCH ) ) {
					documentList = emc.list( Document.class,  _batch_docIds );
					if( ListTools.isNotEmpty( documentList )) {
						for( Document document : documentList ) {
							appId = document.getAppId();
							categoryId = document.getCategoryId();
							emc.remove( document, CheckRemoveType.all );
							i++;
							if( i%100 == 0 ) {
								System.out.println( ">>>>>>>>>正在删除第条"+i+"数据......" );
							}
							
							new CmsBatchOperationPersistService().addOperation( 
									CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
									CmsBatchOperationProcessService.OPT_TYPE_DELETE,  document.getId(),  document.getId(), "文档删除：ID=" +  document.getId() );
							
							//检查是否需要删除热点图片
							try {
								ThisApplication.queueDocumentDelete.send( document.getId() );
							} catch ( Exception e1 ) {
								e1.printStackTrace();
							}
							
							
						}
					}
					emc.commit();
				}
				result.setCount( Long.parseLong( docIds.size() + "") );
			}
			logService.log( emc, effectivePerson.getDistinguishedName(), "按批次号删除导入文档信息！" + importBatchName, appId, categoryId, null, "", "DOCUMENT", "删除" );

			Wo wo = new Wo();
			wo.setId( importBatchName );
			
			result.setData( wo );
			
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}