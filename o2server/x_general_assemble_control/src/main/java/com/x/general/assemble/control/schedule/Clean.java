package com.x.general.assemble.control.schedule;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;

public class Clean extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(Clean.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			LOGGER.debug("开始定时清理过期的缓存附件！");
			TimeStamp stamp = new TimeStamp();
			Long instantCount = this.clearGeneralFile();
			LOGGER.debug("清理过期的缓存附件：{} 条, 耗时: {}.", instantCount, stamp.consumingMilliseconds());
		} catch (Exception e) {
			LOGGER.error(e);
			throw new JobExecutionException(e);
		}
	}

	private Long clearGeneralFile() throws Exception {
		List<GeneralFile> os = null;
		Long count = 0L;
		do {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				os = this.listInstant(emc);
				if (!os.isEmpty()) {
					emc.beginTransaction(GeneralFile.class);
					for (GeneralFile o : os) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
								o.getStorage());
						if (null != mapping) {
							o.deleteContent(mapping);
						} else {
							LOGGER.warn("storage mapping not exist:{}.", o.getStorage());
						}
						emc.remove(o);
					}
					emc.commit();
					count += os.size();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		} while (ListTools.isNotEmpty(os));
		return count;
	}

	private List<GeneralFile> listInstant(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(GeneralFile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<GeneralFile> cq = cb.createQuery(GeneralFile.class);
		Root<GeneralFile> root = cq.from(GeneralFile.class);
		Date limit = DateTools.floorDate(new Date(), 0);
		Predicate p = cb.lessThan(root.get(JpaObject_.createTime), limit);
		return em.createQuery(cq.select(root).where(p)).setMaxResults(100).getResultList();
	}

}