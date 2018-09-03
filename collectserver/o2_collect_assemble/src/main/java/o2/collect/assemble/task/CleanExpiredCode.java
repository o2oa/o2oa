package o2.collect.assemble.task;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.core.entity.Code;
import o2.collect.core.entity.Code_;

public class CleanExpiredCode implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(CleanDevice.class);

	public void run() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Code.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Code> cq = cb.createQuery(Code.class);
			Root<Code> root = cq.from(Code.class);
			Predicate p = cb.lessThan(root.get(Code_.expiredTime), new Date());
			List<Code> list = em.createQuery(cq.where(p)).getResultList();
			emc.beginTransaction(Code.class);
			for (Code o : list) {
				emc.remove(o);
			}
			emc.commit();
		} catch (Exception e) {
			logger.error(e);
		}
	}

}