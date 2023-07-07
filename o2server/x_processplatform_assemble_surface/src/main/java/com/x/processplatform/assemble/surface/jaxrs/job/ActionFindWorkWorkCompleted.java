package com.x.processplatform.assemble.surface.jaxrs.job;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionFindWorkWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFindWorkWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (BooleanUtils.isTrue(
					new JobControlBuilder(effectivePerson, business, job).enableAllowVisit().build().getAllowVisit())) {
				wo.setWorkList(this.listWork(business, job));
				wo.setWorkCompletedList(this.listWorkCompleted(business, job));
			}
		}
		result.setData(wo);
		return result;
	}

	private List<WoWork> listWork(Business business, String job) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.job), job);
		cq.select(root.get(Work_.id)).where(p);
		List<String> ids = em.createQuery(cq).getResultList();
		return business.entityManagerContainer().fetch(ids, WoWork.copier);
	}

	private List<WoWorkCompleted> listWorkCompleted(Business business, String job) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.job), job);
		cq.select(root.get(WorkCompleted_.id)).where(p);
		List<String> ids = em.createQuery(cq).getResultList();
		return business.entityManagerContainer().fetch(ids, WoWorkCompleted.copier);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.ActionFindWorkWorkCompleted$Wo")
	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 8586585135373549163L;

		@FieldDescribe("属于job的work")
		private List<WoWork> workList = new ArrayList<>();
		@FieldDescribe("属于job的workCompleted")
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();

		public List<WoWork> getWorkList() {
			return workList;
		}

		public void setWorkList(List<WoWork> workList) {
			this.workList = workList;
		}

		public List<WoWorkCompleted> getWorkCompletedList() {
			return workCompletedList;
		}

		public void setWorkCompletedList(List<WoWorkCompleted> workCompletedList) {
			this.workCompletedList = workCompletedList;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.ActionFindWorkWorkCompleted$WoWork")
	public static class WoWork extends Work {

		private static final long serialVersionUID = 8844552543181007645L;
		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class,
				JpaObject.singularAttributeField(Work.class, true, true), null);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.ActionFindWorkWorkCompleted$WoWorkCompleted")
	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = 6933166763697642092L;
		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, JpaObject.singularAttributeField(Work.class, true, true), null);
	}
}