package com.x.program.center.jaxrs.script;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.exception.ExceptionAliasExist;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionNameExist;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Script;

abstract class BaseAction extends StandardJaxrsAction {

	static CacheCategory cache = new CacheCategory(Script.class);

	void checkName(Business business, Script script) throws Exception {
		if (StringUtils.isEmpty(script.getName())) {
			throw new ExceptionEntityFieldEmpty(Script.class, Script.name_FIELDNAME);
		}
		String id = business.script().getWithName(script.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new ExceptionNameExist(script.getName());
		}
		id = business.script().getWithAlias(script.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new ExceptionNameExist(script.getName());
		}
	}

	void checkAlias(Business business, Script script) throws Exception {
		if (StringUtils.isEmpty(script.getAlias())) {
			return;
		}
		String id = business.script().getWithAlias(script.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new ExceptionAliasExist(script.getAlias());
		}
		id = business.script().getWithName(script.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(script.getId(), id))) {
			throw new ExceptionAliasExist(script.getAlias());
		}
	}

	void checkDepend(Business business, Script script) throws Exception {
		for (String str : ListTools.trim(script.getDependScriptList(), true, true)) {
			if (StringUtils.isNotEmpty(str)) {
				if (StringUtils.equals(str, script.getId()) || StringUtils.equals(str, script.getName())
						|| StringUtils.equals(str, script.getAlias())) {
					throw new DependSelfException(script.getName(), script.getId());
				} else {
					String id = business.script().getWithName(str);
					if (StringUtils.isEmpty(id)) {
						id = business.script().getWithAlias(str);
					}
					if (StringUtils.isEmpty(id)) {
						id = business.script().getWithId(str);
					}
					if (StringUtils.isEmpty(id)) {
						throw new DependNotExistedException(script.getName(), script.getId(), str);
					}
				}
			}
		}
	}
}
