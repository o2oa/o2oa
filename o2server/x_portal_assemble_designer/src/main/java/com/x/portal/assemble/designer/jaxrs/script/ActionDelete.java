package com.x.portal.assemble.designer.jaxrs.script;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Script_;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class);
			if (null == script) {
				throw new ScriptNotExistedException(id);
			}
			Portal portal = emc.find(script.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(script.getPortal());
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Script.class);
			this.checkDepended(business, script);
			emc.remove(script, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(Script.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void checkDepended(Business business, Script script) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.isMember(script.getId(), root.get(Script_.dependScriptList));
		p = cb.and(p, cb.equal(root.get(Script_.portal), script.getPortal()));
		List<Script> list = em.createQuery(cq.select(root).where(p)).getResultList();
		if (!list.isEmpty()) {
			List<String> names = ListTools.extractProperty(list, "name", String.class, true, false);
			throw new DependedException(script.getName(), script.getId(), StringUtils.join(names, ","));
		}
	}

	public static class Wo extends WrapBoolean {
	}
}