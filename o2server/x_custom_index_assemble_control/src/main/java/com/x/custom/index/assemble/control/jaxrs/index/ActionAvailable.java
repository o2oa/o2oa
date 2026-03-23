package com.x.custom.index.assemble.control.jaxrs.index;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.PersonDetail;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.custom.index.assemble.control.Business;
import com.x.custom.index.core.entity.Reveal;
import com.x.processplatform.core.entity.element.Application;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionAvailable extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAvailable.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			PersonDetail personDetail = business.organization().person().detail(effectivePerson.getDistinguishedName(),
					true, true, true, true, true, false);
			Triple<List<Application>, List<AppInfo>, List<Reveal>> applicationAppInfoReveal = business
					.listApplicationAppInfoReveal();
			applicationAppInfoReveal = filterAvailable(effectivePerson, applicationAppInfoReveal, personDetail);
			Wo wo = new Wo();
			wo.setProcessPlatformList(applicationAppInfoReveal.first().stream()
					.map(o -> new WoProcessPlatformCms(o.getName(), o.getId())).collect(Collectors.toList()));
			wo.setCmsList(applicationAppInfoReveal.second().stream()
					.map(o -> new WoProcessPlatformCms(o.getAppName(), o.getId())).collect(Collectors.toList()));
			wo.setRevealList(applicationAppInfoReveal.third().stream().map(o -> new WoReveal(o.getName(), o.getId()))
					.collect(Collectors.toList()));
			result.setData(wo);
		}
		return result;
	}

	private Triple<List<Application>, List<AppInfo>, List<Reveal>> filterAvailable(EffectivePerson effectivePerson,
			Triple<List<Application>, List<AppInfo>, List<Reveal>> triple, PersonDetail personDetail) {
		List<Application> applications = this.filterAvailableApplication(effectivePerson, triple.first(), personDetail);
		List<AppInfo> appInfos = this.filterAvailableAppInfo(effectivePerson, triple.second(), personDetail);
		List<Reveal> reveals = this.filterAvailableReveal(effectivePerson, triple.third(), personDetail);
		return Triple.of(applications, appInfos, reveals);
	}

	private List<Application> filterAvailableApplication(EffectivePerson effectivePerson, List<Application> list,
			PersonDetail personDetail) {
		return list.stream().filter(o -> {
			if (effectivePerson.isManager() || effectivePerson.isCipher()) {
				return true;
			}
			if (personDetail.containsAnyRole(OrganizationDefinition.Manager,
					OrganizationDefinition.ProcessPlatformManager)) {
				return true;
			}
			if (ListTools.isEmpty(o.getAvailableIdentityList()) && ListTools.isEmpty(o.getAvailableUnitList())
					&& ListTools.isEmpty(o.getAvailableGroupList())) {
				return true;
			}
			return (personDetail.containsAny(o.getAvailableIdentityList(), o.getAvailableUnitList(),
					o.getAvailableGroupList()));
		}).collect(Collectors.toList());
	}

	private List<AppInfo> filterAvailableAppInfo(EffectivePerson effectivePerson, List<AppInfo> list,
			PersonDetail personDetail) {
		return list.stream().filter(o -> {
			if (effectivePerson.isManager()) {
				return true;
			}
			if (personDetail.containsAnyRole(OrganizationDefinition.Manager, OrganizationDefinition.CMSManager)) {
				return true;
			}
			if (ListTools.isEmpty(o.getViewablePersonList()) && ListTools.isEmpty(o.getViewableUnitList())
					&& ListTools.isEmpty(o.getViewableGroupList())) {
				return true;
			}
			if (personDetail.containsAny(o.getViewablePersonList(), o.getViewableUnitList(),
					o.getViewableGroupList())) {
				return true;
			}
			if (personDetail.containsAny(o.getPublishablePersonList(), o.getPublishableUnitList(),
					o.getPublishableGroupList())) {
				return true;
			}
			return personDetail.containsAny(o.getManageablePersonList(), o.getManageableUnitList(),
					o.getManageableGroupList());
		}).collect(Collectors.toList());
	}

	private List<Reveal> filterAvailableReveal(EffectivePerson effectivePerson, List<Reveal> list,
			PersonDetail personDetail) {
		return list.stream().filter(o -> {
			if (effectivePerson.isManager()) {
				return true;
			}
			if (personDetail.containsAnyRole(OrganizationDefinition.Manager)) {
				return true;
			}
			if (ListTools.isEmpty(o.getAvailablePersonList()) && ListTools.isEmpty(o.getAvailableUnitList())
					&& ListTools.isEmpty(o.getAvailableGroupList())) {
				return true;
			}
			return personDetail.containsAny(o.getAvailablePersonList(), o.getAvailableUnitList(),
					o.getAvailableGroupList());
		}).collect(Collectors.toList());
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.index.ActionAvailable$Wo")
	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -7247122401328873164L;

		@FieldDescribe("流程平台.")
		@Schema(description = "流程平台.")
		private List<WoProcessPlatformCms> processPlatformList = new ArrayList<>();

		@FieldDescribe("流程平台.")
		@Schema(description = "流程平台.")
		private List<WoProcessPlatformCms> cmsList = new ArrayList<>();

		@FieldDescribe("定制展现.")
		@Schema(description = "定制展现.")
		private List<WoReveal> revealList = new ArrayList<>();

		public List<WoProcessPlatformCms> getProcessPlatformList() {
			return processPlatformList;
		}

		public void setProcessPlatformList(List<WoProcessPlatformCms> processPlatformList) {
			this.processPlatformList = processPlatformList;
		}

		public List<WoProcessPlatformCms> getCmsList() {
			return cmsList;
		}

		public void setCmsList(List<WoProcessPlatformCms> cmsList) {
			this.cmsList = cmsList;
		}

		public List<WoReveal> getRevealList() {
			return revealList;
		}

		public void setRevealList(List<WoReveal> revealList) {
			this.revealList = revealList;
		}

	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionAvailable$WoProcessPlatformCms")
	public class WoProcessPlatformCms extends GsonPropertyObject {

		private static final long serialVersionUID = 7661558423536695358L;

		public WoProcessPlatformCms(String name, String key) {
			this.name = name;
			this.key = key;
		}

		@FieldDescribe("名称.")
		@Schema(description = "名称.")
		private String name;

		@FieldDescribe("标识.")
		@Schema(description = "标识.")
		private String key;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionAvailable$WoReveal")
	public class WoReveal extends GsonPropertyObject {

		private static final long serialVersionUID = 7661558423536695358L;

		public WoReveal(String name, String id) {
			this.name = name;
			this.id = id;
		}

		@FieldDescribe("名称.")
		@Schema(description = "名称.")
		private String name;

		@FieldDescribe("标识.")
		@Schema(description = "标识.")
		private String id;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

}