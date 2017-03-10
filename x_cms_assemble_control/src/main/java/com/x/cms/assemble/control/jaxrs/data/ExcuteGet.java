package com.x.cms.assemble.control.jaxrs.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {
	
	/**
	 * 从数据库中根据docId和路径来获取文档的内容
	 * @param effectivePerson
	 * @param docId
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<JsonElement> execute( EffectivePerson effectivePerson, String docId, String... paths ) throws Exception {
		ActionResult<JsonElement> result = new ActionResult<>();
		JsonElement jsonElement = null;
		DataLobItem lob = null;
		Business business = null;
		List<DataItem> list = null;
		ItemConverter<DataItem> converter = null;
		String cacheKey = docId + ".path." + StringUtils.join(paths, ".");
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			jsonElement = ( JsonElement ) element.getObjectValue();
			result.setData( jsonElement );
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				list = business.getDataItemFactory().listWithDocIdWithPath( docId, paths );
				converter = new ItemConverter<>( DataItem.class );
				
				if( list != null && !list.isEmpty() ){
					for (DataItem o : list) {
						if (o.isLobItem()) {
							lob = emc.find( o.getLobItem(), DataLobItem.class );
							if (null != lob) {
								o.setStringLobValue(lob.getData());
							}
						}
					}
				}
				if( paths != null ){
					jsonElement = converter.assemble( list );
				}else{
					jsonElement = converter.assemble( list );
				}				
				result.setData( jsonElement );
				cache.put(new Element( cacheKey, jsonElement ));
				
			} catch (Exception e) {
				throw new Exception( "getData error.", e );
			}
		}
		
		return result;
	}
	
}