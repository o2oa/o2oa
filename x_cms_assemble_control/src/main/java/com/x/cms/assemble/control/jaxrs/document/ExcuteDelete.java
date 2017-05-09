package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentNotExistsException;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;

public class ExcuteDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		List<String> document_permission_ids = null;
		List<DataItem> dataItems = null;
		DataLobItem lob = null;
		DocumentPermission documentPermission = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			
			//先判断需要操作的文档信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Document document = business.getDocumentFactory().get( id );
			if (null == document) {
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
				throw exception;
			}
			
			//进行数据库持久化操作
			emc.beginTransaction( Document.class );
			emc.beginTransaction( DataItem.class );
			emc.beginTransaction( DataLobItem.class );
			emc.beginTransaction( DocumentPermission.class );
			
			//删除与该文档有关的所有数据信息
			dataItems = business.getDataItemFactory().listWithDocIdWithPath( id );
			if ((!dataItems.isEmpty())) {
				emc.beginTransaction(DataItem.class);
				emc.beginTransaction(DataLobItem.class);
				for ( DataItem o : dataItems ) {
					if (o.isLobItem()) {
						lob = emc.find(o.getLobItem(), DataLobItem.class);
						if (null != lob) {
							emc.remove(lob);
						}
					}
					emc.remove(o);
				}
			}
		
			document_permission_ids = business.documentPermissionFactory().listIdsByDocumentId( id );
			if( document_permission_ids != null && !document_permission_ids.isEmpty() ){
				for( String permissionId : document_permission_ids ){
					documentPermission = emc.find( permissionId, DocumentPermission.class );
					emc.remove( documentPermission );
				}
			}
			//删除文档信息
			emc.remove( document, CheckRemoveType.all );
			emc.commit();
			
			ApplicationCache.notify( Document.class );
			
			String cacheKey = ApplicationCache.concreteCacheKey( id );
			ApplicationCache.notify( DataItem.class, cacheKey );
			ApplicationCache.notify( DataLobItem.class, cacheKey );
			
			logService.log( emc, effectivePerson.getName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "删除" );
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

}