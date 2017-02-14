package com.x.cms.core.entity.content.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.gson.XGsonBuilder;
import com.x.cms.core.entity.content.DataItem;


public class DataHelper {

	private String appId;
	private String catagoryId;
	private String docId;
	private EntityManagerContainer emc;
	private ItemConverter<DataItem> converter;
	private List<DataItem> items;
	private Gson gson;

	public DataHelper( EntityManagerContainer emc, String appId, String catagoryId, String docId ) throws Exception {
		if ( ( null == emc ) || StringUtils.isEmpty( appId ) || StringUtils.isEmpty( appId ) ) {
			throw new Exception( "create document error." );
		}
		this.emc = emc;
		this.appId = appId;
		this.catagoryId = catagoryId;
		this.docId = docId;
		this.converter = new ItemConverter<DataItem>( DataItem.class );
		this.items = this.load();
		this.gson = XGsonBuilder.instance();
	}

	private List<DataItem> load() throws Exception {
		EntityManager em = emc.get( DataItem.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataItem> cq = cb.createQuery( DataItem.class );
		Root<DataItem> root = cq.from( DataItem.class );
		Path<String> path = root.get( "docId" );
		Predicate p = cb.equal( path, docId );
		List<DataItem> list = em.createQuery( cq.where( p ) ).getResultList();
		return list;
	}
 
	public Map<?, ?> get() throws Exception {
		if ( this.items.isEmpty() ) {
			return new ListOrderedMap<Object, Object>();
		} else {
			JsonElement jsonElement = this.converter.assemble( items );
			//添加了jsonElement != null
			if ( jsonElement != null && jsonElement.isJsonObject() ) {
				return gson.fromJson( jsonElement, ListOrderedMap.class );
			} else {
				/* 如果不是Object强制返回一个Map对象 */
				return new ListOrderedMap<Object, Object>();
			}
		}
	}

	public void update( JsonElement jsonElement ) throws Exception {
		List<DataItem> currents = converter.disassemble( jsonElement );
		List<DataItem> removes = converter.subtract( items, currents );
		List<DataItem> adds = converter.subtract( currents, items );
		if (( !removes.isEmpty() ) || ( !adds.isEmpty() )) {
			emc.beginTransaction( DataItem.class );
			if ( ( !adds.isEmpty() ) ) {
				for ( DataItem o : adds ) {
					o.setAppId( appId );
					o.setCatagoryId( catagoryId );
					o.setDocId( docId );
					o.setDocStatus( "draft" );
					emc.persist( o );
				}
			}
			if ( ( !removes.isEmpty() ) ) {
				List<String> ids = new ArrayList<>();
				for ( DataItem o : removes ) {
					ids.add( o.getId() );
				}
				emc.delete( DataItem.class, ids );
			}
			List<DataItem> list = new ArrayList<>();
			list = converter.subtract( items, removes );
			list.addAll( adds );
			converter.sort( list );
			items = list;
		}
	}

	public void published() throws Exception {
		emc.beginTransaction( DataItem.class );
		for ( DataItem o : items ) {
			o.setDocStatus( "published" );
			o.setPublishTime( new Date() );
		}
	}

	public void update( Map<?, ?> map ) throws Exception {
		JsonElement jsonElement = gson.toJsonTree( map );
		this.update( jsonElement );
	}

	public void remove() throws Exception {
		if (( !items.isEmpty() )) {
			emc.beginTransaction( DataItem.class );
			List<String> ids = new ArrayList<>();
			for ( DataItem o : items ) {
				ids.add( o.getId() );
			}
			emc.delete( DataItem.class, ids );
		}
	}
}
