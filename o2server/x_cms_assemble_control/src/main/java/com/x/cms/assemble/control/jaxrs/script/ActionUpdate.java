package com.x.cms.assemble.control.jaxrs.script;

import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.Script;


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
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Script script = emc.find(id, Script.class);
				if (null == script) {
					throw new Exception("script{id:" + id + "} not existed.");
				}
				emc.beginTransaction(Script.class);
				wrapIn.copyTo(script, JpaObject.ID_DISTRIBUTEFACTOR);
				script.setLastUpdatePerson( effectivePerson.getDistinguishedName());
				script.setLastUpdateTime(new Date());
				emc.commit();
				// 清除所有的Script缓存
				ApplicationCache.notify(Script.class);

				// 记录日志
				emc.beginTransaction(Log.class);
				logService.log(emc, effectivePerson.getDistinguishedName(), script.getName(), script.getAppId(), "", "", script.getId(), "SCRIPT", "更新");
				emc.commit();
				
				Wo wo = new Wo();
				wo.setId( script.getId() );
				result.setData(wo);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
	public class Wi extends GsonPropertyObject {

		@FieldDescribe("创建时间.")
		private Date createTime;
		
		@FieldDescribe("更新时间.")
		private Date updateTime;
		
		@FieldDescribe("ID.")
		private String id;
		
		@FieldDescribe("脚本名称.")
		private String name;
		
		@FieldDescribe("脚本别名.")
		private String alias;
		
		@FieldDescribe("脚本说明.")
		private String description;
		
		@FieldDescribe("是否验证成功.")
		private Boolean validated;
		
		@FieldDescribe("所属栏目ID.")
		private String appId;
		
		@FieldDescribe("脚本内容.")
		private String text;
		
		@FieldDescribe("依赖的脚本ID列表.")
		private List<String> dependScriptList;

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Boolean getValidated() {
			return validated;
		}

		public void setValidated(Boolean validated) {
			this.validated = validated;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public List<String> getDependScriptList() {
			return dependScriptList;
		}

		public void setDependScriptList(List<String> dependScriptList) {
			this.dependScriptList = dependScriptList;
		}

	}
	
	public static class Wo extends WoId {

	}
}
