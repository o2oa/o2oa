package com.x.cms.assemble.control.jaxrs.script;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.element.Script;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wrap = (Wo) optional.get();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Script script = emc.find(id, Script.class);
				if (null == script) {
					throw new Exception("[get]script{id:" + id + "} not existed.");
				}
				wrap = Wo.copier.copy(script);
				result.setData(wrap);

				// 将查询结果放进缓存里
				CacheManager.put(cacheCategory, cacheKey, wrap );

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private Date createTime;

		private Date updateTime;

		private String id;

		private String name;

		private String alias;

		private String description;

		private Boolean validated;

		private String appId;

		private String text;

		private List<String> dependScriptList;

		private String creatorPerson;

		private String lastUpdatePerson;

		private Date lastUpdateTime;

		public static final WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				JpaObject.FieldsInvisible);

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

		public List<String> getDependScriptList() {
			return dependScriptList;
		}

		public void setDependScriptList(List<String> dependScriptList) {
			this.dependScriptList = dependScriptList;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public String getLastUpdatePerson() {
			return lastUpdatePerson;
		}

		public void setLastUpdatePerson(String lastUpdatePerson) {
			this.lastUpdatePerson = lastUpdatePerson;
		}

		public Date getLastUpdateTime() {
			return lastUpdateTime;
		}

		public void setLastUpdateTime(Date lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
}
