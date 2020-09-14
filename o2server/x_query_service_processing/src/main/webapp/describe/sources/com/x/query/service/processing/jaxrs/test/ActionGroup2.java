package com.x.query.service.processing.jaxrs.test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.utils.time.ClockStamp;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

class ActionGroup2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGroup2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ClockStamp.INIT("创建测试distinct代码.", "");
			job(emc);
			return new ActionResult<>();
		}
	}

	private void job(EntityManagerContainer emc) throws Exception {
		ClockStamp.STAMP("执行work的job distinct{}.", "开始");
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		cq.select(root.get(Work_.id)).distinct(true);
		em.createQuery(cq).getResultList();
		ClockStamp.STAMP("执行work的job distinct{}.", "结束");
	}

	public static class Wo extends WrapBoolean {
	}

}