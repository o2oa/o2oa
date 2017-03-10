package com.x.cms.assemble.control.jaxrs.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;

public class ExcuteDelete extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson,  String docId, String... paths ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Business business = null;
		DataLobItem lob = null;
		List<DataItem> exists = null;
		String cacheKey = docId + ".path." + StringUtils.join(paths, ".");
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			lob = null;
			exists = business.getDataItemFactory().listWithDocIdWithPath( docId, paths );
			if ( exists.isEmpty() ) {
				throw new Exception( "data{docId:" + docId + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
			}
			business.entityManagerContainer().beginTransaction( DataItem.class );
			business.entityManagerContainer().beginTransaction( DataLobItem.class );
			
			for ( DataItem o : exists ) {
				lob = emc.find( o.getLobItem(), DataLobItem.class );
				if ( null != lob ) {
					emc.remove(lob);
				}
				business.entityManagerContainer().remove(o);
			}
			if ( NumberUtils.isNumber(paths[paths.length - 1]) ) {
				int position = paths.length - 1;
				for ( DataItem o : business.getDataItemFactory().listWithDocIdWithPathWithAfterLocation( docId, NumberUtils.toInt(paths[position]), paths)) {
					o.path(Integer.toString(o.pathLocation(position) - 1), position);
				}
			}
			business.entityManagerContainer().commit();
			result.setData( new WrapOutId( docId ) );
			
			ApplicationCache.notify( DataItem.class, cacheKey );
			
		} catch (Exception e) {
			throw new Exception("deleteWithApplicationDict error.", e);
		}
		return result;
	}
	
}