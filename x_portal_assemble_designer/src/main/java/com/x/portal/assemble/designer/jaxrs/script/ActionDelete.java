package com.x.portal.assemble.designer.jaxrs.script;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.utils.ListTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Script_;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
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
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Script.class);
			this.checkDepended(business, script);
			emc.remove(script, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Script.class);
			result.setData(WrapOutBoolean.trueInstance());
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
}