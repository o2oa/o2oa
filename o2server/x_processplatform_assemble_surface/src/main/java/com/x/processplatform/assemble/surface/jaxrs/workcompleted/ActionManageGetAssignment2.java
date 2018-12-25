package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutMap;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

/**
 * 
 * 在管理界面下获取相关联的所有信息,需要control权限,同时需要添加一个permission
 */
class ActionManageGetAssignment2 extends BaseAction {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			WrapOutMap wrap = new WrapOutMap();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			Process process = business.process().pick(workCompleted.getProcess());
			Application application = business.application().pick(workCompleted.getApplication());
			// 需要对这个应用的管理权限
			if (!business.canManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			wrap.put("taskCompletedList", this.listTaskCompleted(business, workCompleted));
			wrap.put("readList", this.listRead(business, workCompleted));
			wrap.put("readCompletedList", this.listReadCompleted(business, workCompleted));
			wrap.put("reviewList", this.listReview(business, workCompleted));
			WoControl control = business.getControl(effectivePerson, workCompleted, WoControl.class);
			wrap.put("control", control);
			result.setData(wrap);
			return result;
		}
	}

	private List<WoTaskCompleted> listTaskCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.workCompleted), workCompleted.getId());
		cq.select(root).where(p);
		List<TaskCompleted> os = em.createQuery(cq).getResultList();
		List<WoTaskCompleted> wos = WoTaskCompleted.copier.copy(os);
		wos = business.taskCompleted().sort(wos);
		return wos;
	}

	private List<WoRead> listRead(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Read> cq = cb.createQuery(Read.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.workCompleted), workCompleted.getId());
		cq.select(root).where(p);
		List<Read> os = em.createQuery(cq).getResultList();
		List<WoRead> wos = WoRead.copier.copy(os);
		wos = business.read().sort(wos);
		return wos;
	}

	private List<WoReadCompleted> listReadCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ReadCompleted> cq = cb.createQuery(ReadCompleted.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.workCompleted), workCompleted.getId());
		cq.select(root).where(p);
		List<ReadCompleted> os = em.createQuery(cq).getResultList();
		List<WoReadCompleted> wos = WoReadCompleted.copier.copy(os);
		wos = business.readCompleted().sort(wos);
		return wos;
	}

	List<WoReview> listReview(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.workCompleted), workCompleted.getId());
		cq.select(root).where(p);
		List<Review> os = em.createQuery(cq).getResultList();
		List<WoReview> wos = WoReview.copier.copy(os);
		wos = business.review().sort(wos);
		return wos;
	}

	public static class Wo extends GsonPropertyObject {

		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();
		private List<WoRead> readList = new ArrayList<>();
		private List<WoReadCompleted> readCompletedList = new ArrayList<>();
		private List<WoReview> reviewList = new ArrayList<>();
		private WoControl control;

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public List<WoReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<WoReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

		public List<WoReview> getReviewList() {
			return reviewList;
		}

		public void setReviewList(List<WoReview> reviewList) {
			this.reviewList = reviewList;
		}

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -5707845125641375013L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -5258254759835923175L;

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = 8123512298634072415L;

		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoReview extends Review {

		private static final long serialVersionUID = -452279135320456110L;

		static WrapCopier<Review, WoReview> copier = WrapCopierFactory.wo(Review.class, WoReview.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoControl extends WorkCompletedControl {
	}

}