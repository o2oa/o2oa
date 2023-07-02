package com.x.processplatform.assemble.designer.jaxrs.file;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.File;
import com.x.processplatform.core.entity.element.File_;

class ActionCopy extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			File file = emc.flag(flag, File.class);
			if (null == file) {
				throw new ExceptionEntityNotExist(flag, File.class);
			}
			Application application = emc.find(file.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(file.getApplication(), Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Application toApplication = emc.flag(applicationFlag, Application.class);
			if (null == toApplication) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			if (!business.editable(effectivePerson, toApplication)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			File toFile = new File();
			toFile.setName(this.getName(business, file.getName(), toFile.getId(), toApplication.getId()));
			toFile.setApplication(toApplication.getId());
			toFile.setDescription(file.getDescription());
			toFile.setData(file.getData());
			toFile.setFileName(file.getFileName());
			toFile.setLastUpdatePerson(file.getLastUpdatePerson());
			toFile.setLastUpdateTime(new Date());
			toFile.setLength(file.getLength());
			emc.beginTransaction(File.class);
			emc.persist(toFile, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(File.class);
			Wo wo = new Wo();
			wo.setId(toFile.getId());
			result.setData(wo);
			return result;
		}
	}

	private String getName(Business business, String name, String id, String applicationId) throws Exception {
		for (int i = 0; i < 10000; i++) {
			if (!this.exist(business, name + i, id, applicationId)) {
				return name;
			}
		}
		throw new ExceptionErrorName(name);
	}

	private boolean exist(Business business, String name, String id, String applicationId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.or(cb.equal(root.get(File_.name), name), cb.equal(root.get(File_.alias), name),
				cb.equal(root.get(File_.id), name));
		p = cb.and(p, cb.equal(root.get(File_.application), applicationId), cb.notEqual(root.get(File_.id), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult() > 0;
	}

	public static class Wo extends WoId {
	}

}