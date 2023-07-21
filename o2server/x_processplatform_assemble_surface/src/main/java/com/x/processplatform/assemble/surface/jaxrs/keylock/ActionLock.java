package com.x.processplatform.assemble.surface.jaxrs.keylock;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.KeyLock;
import com.x.processplatform.core.entity.content.KeyLock_;
import com.x.processplatform.core.express.service.processing.jaxrs.keylock.ActionLockWi;
import com.x.processplatform.core.express.service.processing.jaxrs.keylock.ActionLockWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionLock extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionLock.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(KeyLock.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KeyLock> cq = cb.createQuery(KeyLock.class);
			Root<KeyLock> root = cq.from(KeyLock.class);
			Predicate p = cb.equal(root.get(KeyLock_.key), wi.getKey());
			p = cb.and(p, cb.notEqual(root.get(KeyLock_.person), effectivePerson.getDistinguishedName()));
			List<KeyLock> os = em.createQuery(cq.where(p).orderBy(cb.desc(root.get(JpaObject_.createTime))))
					.setMaxResults(1).getResultList();
			if (os.isEmpty()) {
				emc.beginTransaction(KeyLock.class);
				KeyLock o = new KeyLock(wi.getKey(), effectivePerson.getDistinguishedName());
				emc.persist(o, CheckPersistType.all);
				emc.commit();
				wo.setSuccess(true);
				wo.setPerson(o.getPerson());
			} else {
				wo.setSuccess(false);
				wo.setPerson(os.get(0).getPerson());
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.keylock.ActionLock$Wo")
	public static class Wo extends ActionLockWo {

		private static final long serialVersionUID = -1295412555217995247L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.keylock.ActionLock$Wi")
	public static class Wi extends ActionLockWi {

		private static final long serialVersionUID = -326401129410594142L;

	}
}
