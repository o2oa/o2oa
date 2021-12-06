package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.SerialNumber_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.service.processing.Business;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 创建工作序列号
 * @author jian
 *
 */
class ActionCreateSerial extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreateSerial.class);

	private static ConcurrentHashMap<String, ReentrantLock> processMap = new ConcurrentHashMap();

	private static ReentrantLock lock = new ReentrantLock();

	ActionResult<Wo> execute(String processId, String name) throws Exception {
		String application = "";
		String processName = "";
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.element().get(processId, Process.class);
			if(process == null){
				throw new ExceptionEntityNotExist(processId, Process.class);
			}
			application = process.getApplication();
			processId = process.getId();
			processName = process.getName();
		}
		ReentrantLock appLock = null;
		lock.lock();
		try {
			appLock = processMap.get(application);
			if(appLock == null){
				appLock = new ReentrantLock();
				processMap.put(application, appLock);
			}
		}finally {
			lock.unlock();
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if(appLock != null){
			appLock.lock();
			try {
				Integer serial = this.createSerial(processId, application, name);
				wo.setValue(serial);
				logger.info("为流程:{}的关键字:{}创建序列号：{}", processName, name, serial);
			}finally {
				appLock.unlock();
			}
		}else{
			Integer serial = this.createSerial(processId, application, name);
			wo.setValue(serial);
		}


		result.setData(wo);
		return result;
	}

	private Integer createSerial(String processId, String application, String name) throws Exception{
		if(EMPTY_SYMBOL.equals(name)){
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

	}

}
