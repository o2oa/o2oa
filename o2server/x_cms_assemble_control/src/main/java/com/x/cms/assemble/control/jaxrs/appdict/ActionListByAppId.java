package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

public class ActionListByAppId extends BaseAction {
	
	protected ActionResult<List<Wo>> execute( EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);			
			AppInfo appInfo = emc.find(appId, AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + appId + "} not existed.");
			}
			List<String> ids = business.getAppDictFactory().listWithAppInfo(appId);
			for (AppDict o : emc.list(AppDict.class, ids)) {
				wos.add( new Wo(o) );
			}
			Collections.sort( wos, new Comparator<Wo>() {
				public int compare( Wo o1, Wo o2 ) {
					return ObjectUtils.compare(o1.getName(), o2.getName(), true);
				}
			});
			result.setData( wos );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
	public static class Wo extends GsonPropertyObject {
		
		static WrapCopier<AppDictItem, Wo> copier = WrapCopierFactory.wo( AppDictItem.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));
		
		private String id;
		
		private String appId;
		
		private String name;
		
		private String alias;
		
		private String description;
		
		private JsonElement data;
		
		public Wo( AppDict o ) throws Exception {
			o.copyTo(this, JpaObject.FieldsInvisible);
		}
		
		public String getId() {
			return id;
		}
		public String getAppId() {
			return appId;
		}
		public String getName() {
			return name;
		}
		public String getAlias() {
			return alias;
		}
		public String getDescription() {
			return description;
		}
		public JsonElement getData() {
			return data;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setAlias(String alias) {
			this.alias = alias;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public void setData(JsonElement data) {
			this.data = data;
		}
	}
}