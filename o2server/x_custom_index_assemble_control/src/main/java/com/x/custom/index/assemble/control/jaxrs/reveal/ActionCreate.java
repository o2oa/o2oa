package com.x.custom.index.assemble.control.jaxrs.reveal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.custom.index.assemble.control.Business;
import com.x.custom.index.core.entity.Reveal;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.express.index.Directory;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			checkProcessPlatformCmsEmpty(wi.getProcessPlatformList(), wi.getCmsList());
			Pair<List<Application>, List<AppInfo>> pair = business.listApplicationAppInfo();
			pair = this.filterEditable(effectivePerson, business, pair);
			List<String> applications = pair.first().stream().map(Application::getId).collect(Collectors.toList());
			List<String> appInfos = pair.second().stream().map(AppInfo::getId).collect(Collectors.toList());
			if ((!CollectionUtils.containsAll(applications,
					wi.getProcessPlatformList().stream().map(Directory::getKey).collect(Collectors.toList())))
					|| (!CollectionUtils.containsAll(appInfos,
							wi.getCmsList().stream().map(Directory::getKey).collect(Collectors.toList())))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Reveal.class);
			Reveal reveal = new Reveal();
			reveal.setEnable(BooleanUtils.isTrue(wi.getEnable()));
			reveal.setName(wi.getName());
			reveal.setAvailableGroupList(business.organization().group().list(wi.getAvailableGroupList()));
			reveal.setAvailableUnitList(business.organization().unit().list(wi.getAvailableUnitList()));
			reveal.setAvailablePersonList(business.organization().person().list(wi.getAvailablePersonList()));
			reveal.setProcessPlatformList(wi.getProcessPlatformList());
			reveal.setCmsList(wi.getCmsList());
			reveal.setIgnorePermission(wi.getIgnorePermission());
			reveal.setData(wi.getData());
			reveal.setCreatorPerson(effectivePerson.getDistinguishedName());
			emc.persist(reveal, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Reveal.class);
			Wo wo = new Wo();
			wo.setId(reveal.getId());
			result.setData(wo);
		}
		return result;
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionCreate$Wo")
	public class Wo extends WoId {

		private static final long serialVersionUID = 3751674531291729956L;

	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionCreate$Wi")
	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -7453646449864051832L;

		@FieldDescribe("名称.")
		@Schema(description = "名称.")
		private String name;

		@FieldDescribe("流程平台标识.")
		@Schema(description = "流程平台标识.")
		private List<Directory> processPlatformList = new ArrayList<>();

		@FieldDescribe("内容管理标识.")
		@Schema(description = "内容管理标识.")
		private List<Directory> cmsList = new ArrayList<>();

		@FieldDescribe("是否启用.")
		@Schema(description = "是否启用.")
		private Boolean enable;

		@FieldDescribe("可见人员.")
		@Schema(description = "可见身份.")
		private List<String> availablePersonList = new ArrayList<>();

		@FieldDescribe("可见组织.")
		@Schema(description = "可见组织.")
		private List<String> availableUnitList = new ArrayList<>();

		@FieldDescribe("可见群组.")
		@Schema(description = "可见群组.")
		private List<String> availableGroupList = new ArrayList<>();

		@FieldDescribe("忽略权限.")
		@Schema(description = "忽略权限.")
		private Boolean ignorePermission = false;

		public Boolean getIgnorePermission() {
			return ignorePermission;
		}

		public void setIgnorePermission(Boolean ignorePermission) {
			this.ignorePermission = ignorePermission;
		}

		private JsonElement data;

		public List<Directory> getProcessPlatformList() {
			return processPlatformList;
		}

		public void setProcessPlatformList(List<Directory> processPlatformList) {
			this.processPlatformList = processPlatformList;
		}

		public List<Directory> getCmsList() {
			return cmsList;
		}

		public void setCmsList(List<Directory> cmsList) {
			this.cmsList = cmsList;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public List<String> getAvailablePersonList() {
			return availablePersonList;
		}

		public void setAvailablePersonList(List<String> availablePersonList) {
			this.availablePersonList = availablePersonList;
		}

		public List<String> getAvailableUnitList() {
			return availableUnitList;
		}

		public void setAvailableUnitList(List<String> availableUnitList) {
			this.availableUnitList = availableUnitList;
		}

		public List<String> getAvailableGroupList() {
			return availableGroupList;
		}

		public void setAvailableGroupList(List<String> availableGroupList) {
			this.availableGroupList = availableGroupList;
		}

	}

}
