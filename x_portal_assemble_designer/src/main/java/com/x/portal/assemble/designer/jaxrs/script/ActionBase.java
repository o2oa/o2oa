package com.x.portal.assemble.designer.jaxrs.script;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.utils.ListTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInScript;
import com.x.portal.assemble.designer.wrapout.WrapOutScript;
import com.x.portal.core.entity.Script;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends AbstractJaxrsAction {

	static Ehcache cache = ApplicationCache.instance().getCache(Script.class);

	static BeanCopyTools<Script, WrapOutScript> outCopier = BeanCopyToolsBuilder.create(Script.class,
			WrapOutScript.class, null, WrapOutScript.Excludes);

	static BeanCopyTools<WrapInScript, Script> inCopier = BeanCopyToolsBuilder.create(WrapInScript.class, Script.class,
			null, WrapInScript.Excludes);

	static BeanCopyTools<WrapInScript, Script> updateCopier = BeanCopyToolsBuilder.create(WrapInScript.class,
			Script.class, null, JpaObject.FieldsUnmodifies);

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