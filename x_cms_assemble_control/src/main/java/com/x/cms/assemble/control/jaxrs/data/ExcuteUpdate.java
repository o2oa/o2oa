package com.x.cms.assemble.control.jaxrs.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;

public class ExcuteUpdate extends ExcuteBase {
	
	/**
	 * 更新文档数据的操作
	 * @param effectivePerson
	 * @param document
	 * @param jsonElement
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, Document document, JsonElement jsonElement, String... paths ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		if( document == null ){
			throw new Exception("document is null!");
		}
		
		DataLobItem lob = null;
		List<DataItem> currents = null;
		List<DataItem> removes = null;
		List<DataItem> adds = null;
		Business business = null;
		List<DataItem> exists = null;
		ItemConverter<DataItem> converter = null;
		String cacheKey = document.getId() + ".path." + StringUtils.join(paths, ".");
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			converter = new ItemConverter<>(DataItem.class);
			exists = business.getDataItemFactory().listWithDocIdWithPath( document.getId(), paths );
			if ( exists.isEmpty() ) {
				throw new Exception( "data{document:" + document.getId() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
			}
			currents = converter.disassemble( jsonElement, paths );
			removes = converter.subtract( exists, currents );
			adds = converter.subtract( currents, exists );
			
			emc.beginTransaction(DataItem.class);
			emc.beginTransaction(DataLobItem.class);
			
			for ( DataItem o : removes ) {
				if (o.isLobItem()) {
					lob = emc.find( o.getLobItem(), DataLobItem.class );
					if (null != lob) {
						emc.remove(lob);
					}
				}
				emc.remove(o);
			}
			for ( DataItem o : adds ) {
				o.setDocId( document.getId() );
				o.setAppId( document.getAppId() );
				o.setCategoryId( document.getCategoryId() );
				o.setDocStatus( document.getDocStatus() );
				if (o.isLobItem()) {
					lob = new DataLobItem();
					lob.setData(o.getStringLobValue());
					lob.setDistributeFactor(o.getDistributeFactor());
					o.setLobItem(lob.getId());
					emc.persist(lob);
				}
				emc.persist( o, CheckPersistType.all );
			}
			emc.commit();
			result.setData( new WrapOutId( document.getId() ) );

			ApplicationCache.notify( DataItem.class, cacheKey );
			
		} catch (Exception e) {
			throw new Exception("putData error.", e);
		}
		return result;
	}
	
}