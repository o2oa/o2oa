package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.element.QueryView;


class ActionUpdate extends BaseAction {
	ActionResult<Wo> execute( EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			e.printStackTrace();
		}
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				QueryView queryView = emc.find( id, QueryView.class );
				if( queryView != null ) {
					emc.beginTransaction(QueryView.class);
					wrapIn.copyTo( queryView, JpaObject.ID_DISTRIBUTEFACTOR );		
					if( StringUtils.isEmpty( queryView.getCreatorPerson() )) {
						queryView.setCreatorPerson( effectivePerson.getDistinguishedName() );
					}
					queryView.setLastUpdatePerson(effectivePerson.getDistinguishedName());
					queryView.setLastUpdateTime( new Date() );
					this.transQuery(queryView);
					emc.check(queryView, CheckPersistType.all);
					emc.commit();
					ApplicationCache.notify(QueryView.class);
					
					Wo wo = new Wo();
					wo.setId( queryView.getId() );
					result.setData( wo );
				}else {
					Exception exception = new ExceptionQueryViewNotExists( id );
					result.error(exception);
				}
			}
		}
		return result;
	}
	
	public static class Wi extends QueryView {
		private static final long serialVersionUID = -5237741099036357033L;
		public static List<String> CreateExcludes = new ArrayList<>();
		public static List<String> UpdateExcludes = new ArrayList<>();
		public static WrapCopier<Wi, QueryView> copier = WrapCopierFactory .wi( Wi.class, QueryView.class, null, Wi.CreateExcludes);
		
		static {
			CreateExcludes.add(JpaObject.distributeFactor_FIELDNAME);
			CreateExcludes.add("updateTime");
			CreateExcludes.add("createTime");
			CreateExcludes.add("sequence");
			CreateExcludes.add("lastUpdatePerson");
			CreateExcludes.add("lastUpdateTime");
		}

		static {
			UpdateExcludes.add(JpaObject.distributeFactor_FIELDNAME);
			UpdateExcludes.add(JpaObject.id_FIELDNAME);
			UpdateExcludes.add("updateTime");
			UpdateExcludes.add("createTime");
			UpdateExcludes.add("sequence");
			UpdateExcludes.add("lastUpdatePerson");
			UpdateExcludes.add("lastUpdateTime");
		}
	}
	
	public static class Wo extends WoId {

	}
}
