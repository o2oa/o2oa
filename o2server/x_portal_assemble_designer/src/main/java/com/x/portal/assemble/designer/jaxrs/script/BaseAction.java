package com.x.portal.assemble.designer.jaxrs.script;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Script;

abstract class BaseAction extends StandardJaxrsAction {

	static CacheCategory cache = new CacheCategory(Script.class);

	void checkName(Business business, Script script) throws Exception {
		if (StringUtils.isEmpty(script.getName())) {
			throw new NameEmptyException();
		}
		String id = business.script().getWithNameWithPortal(script.getName(), script.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new NameDuplicateException(script.getName());
		}
		id = business.script().getWithAliasWithPortal(script.getName(), script.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new NameDuplicateWithAliasException(script.getName());
		}
	}

	void checkAlias(Business business, Script script) throws Exception {
		if (StringUtils.isEmpty(script.getAlias())) {
			return;
		}
		String id = business.script().getWithAliasWithPortal(script.getAlias(), script.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new AliasDuplicateException(script.getAlias());
		}
		id = business.script().getWithNameWithPortal(script.getAlias(), script.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new AliasDuplicateWithNameException(script.getAlias());
		}
	}

	void checkDepend(Business business, Script script) throws Exception {
		for (String str : ListTools.trim(script.getDependScriptList(), true, true)) {
			if (StringUtils.isNotEmpty(str)) {
				if (StringUtils.equals(str, script.getId()) || StringUtils.equals(str, script.getName())
						|| StringUtils.equals(str, script.getAlias())) {
					throw new DependSelfException(script.getName(), script.getId());
				} else {
					String id = business.script().getWithNameWithPortal(str, script.getPortal());
					if (StringUtils.isEmpty(id)) {
						id = business.script().getWithAliasWithPortal(str, script.getPortal());
					}
					if (StringUtils.isEmpty(id)) {
						id = business.script().getWithIdWithPortal(str, script.getPortal());
					}
					if (StringUtils.isEmpty(id)) {
						throw new DependNotExistedException(script.getName(), script.getId(), str);
					}
				}
			}
		}
	}
}