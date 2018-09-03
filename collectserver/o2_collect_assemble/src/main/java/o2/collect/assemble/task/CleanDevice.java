package o2.collect.assemble.task;

import java.util.Calendar;
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

import o2.collect.core.entity.Device;
import o2.collect.core.entity.Device_;

public class CleanDevice implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(CleanDevice.class);

	@Override
	public void run() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Device.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Device> cq = cb.createQuery(Device.class);
			Root<Device> root = cq.from(Device.class);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			Predicate p = cb.not(cb.greaterThan(root.get(Device_.connectTime), cal.getTime()));
			List<Device> os = em.createQuery(cq.select(root).where(p)).setMaxResults(200).getResultList();
			emc.beginTransaction(Device.class);
			for (Device _o : os) {
				emc.remove(_o);
			}
			emc.commit();
		} catch (Exception e) {
			logger.error(e);
		}

	}
}
