package com.x.cms.assemble.control.jaxrs.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;

public class ExcuteSave extends ExcuteBase {
	/**
	 * 新增，保存文档数据信息
	 * @param effectivePerson
	 * @param business
	 * @param document
	 * @param jsonElement
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, Business business, Document document, JsonElement jsonElement, String... paths ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		if( document == null ){
			throw new Exception("document is null!");
		}
		
		Integer index = null;
		String[] ps = null;
		String[] parentPaths = null;
		String[] cursorPaths = null;
		DataItem parent = null;
		DataItem cursor = null;
		DataLobItem lob = null;
		List<DataItem> adds = null;
		ItemConverter<DataItem> converter = null;
		String cacheKey = document.getId() + ".path." + StringUtils.join(paths, ".");
		
		try {
			parentPaths = new String[] { "", "", "", "", "", "", "", "" };
			cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
			for (int i = 0; paths != null && i < paths.length - 1; i++) {
				parentPaths[i] = paths[i];
				cursorPaths[i] = paths[i];
			}
			cursorPaths[paths.length - 1] = paths[paths.length - 1];		
		    parent = business.getDataItemFactory().getWithDocIdWithPath( document, parentPaths[0], parentPaths[1], parentPaths[2], parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
			if ( null == parent ) {
				throw new Exception("parent not existed.");
			}
			cursor = business.getDataItemFactory().getWithDocIdWithPath( document, cursorPaths[0], cursorPaths[1], cursorPaths[2], cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
			converter = new ItemConverter<>(DataItem.class);
			
			
			business.entityManagerContainer().beginTransaction(DataItem.class);
			business.entityManagerContainer().beginTransaction(DataLobItem.class);
			
			if (( null != cursor ) && cursor.getItemType().equals( ItemType.a )) {
				/* 向数组里面添加一个成员对象 */
				index = business.getDataItemFactory().getArrayLastIndexWithDocIdWithPath( document.getId(), paths );
				/* 新的路径开始 */
				ps = new String[paths.length + 1];
				
				for (int i = 0; i < paths.length; i++) {
					ps[i] = paths[i];
				}
				ps[paths.length] = Integer.toString(index + 1);
				adds = converter.disassemble(jsonElement, ps);
				
				for ( DataItem o : adds ) {
					o.setAppId(document.getAppId());
					o.setCategoryId(document.getCategoryId());
					o.setDocStatus( document.getDocStatus() );
					if (o.isLobItem()) {
						lob = new DataLobItem();
						lob.setData(o.getStringLobValue());
						lob.setDistributeFactor(o.getDistributeFactor());
						o.setLobItem(lob.getId());
						business.entityManagerContainer().persist(lob);
					}
					business.entityManagerContainer().persist(o);
				}
			} else if (( cursor == null ) && parent.getItemType().equals( ItemType.o )) {
				/* 向parent对象添加一个属性值 */
				adds = converter.disassemble(jsonElement, paths);
				for ( DataItem o : adds ) {
					o.setAppId( document.getAppId() );
					o.setCategoryId( document.getCategoryId() );
					o.setDocStatus( document.getDocStatus() );
					if ( o.isLobItem() ) {
						lob = new DataLobItem();
						lob.setData(o.getStringLobValue());
						lob.setDistributeFactor(o.getDistributeFactor());
						o.setLobItem(lob.getId());
						business.entityManagerContainer().persist(lob);
					}
					business.entityManagerContainer().persist(o);
				}
			} else {
				throw new Exception("unexpected post data with document" + document + ".path:" + StringUtils.join(paths, ".") + "json:" + jsonElement);
			}
			
			business.entityManagerContainer().commit();
			result.setData( new WrapOutId( document.getId() ) );

			ApplicationCache.notify( DataItem.class, cacheKey );
			
		} catch (Exception e) {
			throw new Exception("postWithApplicationDict error.", e);
		}
		return result;
	}
	
}