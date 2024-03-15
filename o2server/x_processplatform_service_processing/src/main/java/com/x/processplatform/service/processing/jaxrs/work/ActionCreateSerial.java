package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.SerialNumber_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.service.processing.Business;

/**
 * 创建工作序列号
 *
 * @author jian
 *
 */
class ActionCreateSerial extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	private static ConcurrentHashMap<String, ReentrantLock> processMap = new ConcurrentHashMap<>();

	private static ReentrantLock lock = new ReentrantLock();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String processId, String name) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		String application = "";
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.element().get(processId, Process.class);
			if (process == null) {
				throw new ExceptionEntityNotExist(processId, Process.class);
			}
			application = process.getApplication();
			processId = process.getId();
		}
		ReentrantLock appLock = null;
		lock.lock();
		try {
			appLock = processMap.get(application);
			if (appLock == null) {
				appLock = new ReentrantLock();
				processMap.put(application, appLock);
			}
		} finally {
			lock.unlock();
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		appLock.lock();
		try {
			Integer serial = this.createSerial(processId, application, name);
			wo.setValue(serial);
		} finally {
			appLock.unlock();
		}
		result.setData(wo);
		return result;
	}

	/**
	 * 工单流水号是放在应用维度，根据依据(name)来区分，
	 * 同个应用不同流程要使用不同的流水号那他的依据必须不一样,
	 * 如果依据一样那他们将共用流水号.
	 * @param processId
	 * @param application
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private Integer createSerial(String processId, String application, String name) throws Exception {
		if (EMPTY_SYMBOL.equals(name)) {
			name = "";
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Integer serial = 0;
			EntityManager em = emc.beginTransaction(SerialNumber.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SerialNumber> cq = cb.createQuery(SerialNumber.class);
			Root<SerialNumber> root = cq.from(SerialNumber.class);
			Predicate p = cb.equal(root.get(SerialNumber_.application), application);
			p = cb.and(p, cb.equal(root.get(SerialNumber_.name), name));
			cq.select(root).where(p);
			List<SerialNumber> list = em.createQuery(cq).setMaxResults(1).getResultList();
			SerialNumber serialNumber = null;
			if (list.isEmpty()) {
				serialNumber = new SerialNumber();
				serialNumber.setProcess(processId);
				serialNumber.setApplication(application);
				serialNumber.setName(name);
				serialNumber.setSerial(1);
				emc.persist(serialNumber, CheckPersistType.all);
				serial = 1;
			} else {
				serialNumber = list.get(0);
				serialNumber.setSerial(serialNumber.getSerial() + 1);
				serial = serialNumber.getSerial();
			}
			emc.commit();
			return serial;
		}
	}

	public static class Wo extends WrapInteger {

		private static final long serialVersionUID = 8667007945527601792L;

	}

}
