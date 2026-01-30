package com.x.custom.index.assemble.control.jaxrs.reveal;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.PersonDetail;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.custom.index.assemble.control.Business;
import com.x.custom.index.core.entity.Reveal;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.express.index.Directory;

abstract class BaseAction extends StandardJaxrsAction {

	CacheCategory revealCacheCategory = new CacheCategory(Reveal.class);

	/**
	 * 过滤用户可管理的的内容管理(AppInfo),流程引擎(Application)
	 * 
	 * @param effectivePerson
	 * @param business
	 * @param pair
	 * @return
	 * @throws Exception
	 */
	protected Pair<List<Application>, List<AppInfo>> filterEditable(EffectivePerson effectivePerson, Business business,
			Pair<List<Application>, List<AppInfo>> pair) throws Exception {
		if (effectivePerson.isManager() || effectivePerson.isCipher()) {
			return pair;
		}
		List<String> groups = business.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
		List<String> units = business.organization().unit().listWithPerson(effectivePerson.getDistinguishedName());
		List<Application> applications = filterApplicationEditable(effectivePerson, business, pair.first());
		List<AppInfo> appInfos = filterAppInfoEditable(effectivePerson, business, pair.second(), groups, units);
		return Pair.of(applications, appInfos);
	}

	private List<Application> filterApplicationEditable(EffectivePerson effectivePerson, Business business,
			List<Application> list) throws Exception {
		if (BooleanUtils.isTrue(business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.ProcessPlatformManager))) {
			return list;
		}
		return list.stream()
				.filter(o -> CollectionUtils.containsAny(o.getControllerList(), effectivePerson.getDistinguishedName()))
				.collect(Collectors.toList());
	}

	private List<AppInfo> filterAppInfoEditable(EffectivePerson effectivePerson, Business business, List<AppInfo> list,
			List<String> groups, List<String> units) throws Exception {
		if (BooleanUtils
				.isTrue(business.organization().person().hasRole(effectivePerson, OrganizationDefinition.CMSManager))) {
			return list;
		}
		return list.stream().filter(
				o -> CollectionUtils.containsAny(o.getManageablePersonList(), effectivePerson.getDistinguishedName())
						|| CollectionUtils.containsAny(o.getManageableUnitList(), units)
						|| CollectionUtils.containsAny(o.getManageableGroupList(), groups))
				.collect(Collectors.toList());
	}

	/**
	 * 检查设置的流程检索目录和内容管理检索目录不能同时为空.
	 * 
	 * @throws ExceptionEmptyDirectory
	 * 
	 */
	protected void checkProcessPlatformCmsEmpty(List<Directory> processPlatformList, List<Directory> cmsList)
			throws ExceptionEmptyDirectory {
		if (ListTools.isEmpty(processPlatformList) && ListTools.isEmpty(cmsList)) {
			throw new ExceptionEmptyDirectory();
		}
	}

	/**
	 * 检查用户是否可以编辑,删除展现
	 * 
	 * @param effectivePerson
	 * @param business
	 * @param reveal
	 * @throws Exception
	 */
	protected void checkEditDeleteAccess(EffectivePerson effectivePerson, Business business, Reveal reveal)
			throws Exception {
		if (effectivePerson.isManager()) {
			return;
		}
		PersonDetail personDetail = business.organization().person().detail(effectivePerson.getDistinguishedName(),
				true, true, true, true, true, false);
		if (!personDetail.containsAnyPerson(reveal.getCreatorPerson())
				&& (!personDetail.containsAnyRole(OrganizationDefinition.Manager))) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
	}
}
