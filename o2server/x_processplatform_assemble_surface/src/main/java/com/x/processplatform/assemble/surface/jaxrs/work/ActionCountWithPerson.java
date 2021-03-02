package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

class ActionCountWithPerson extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCountWithPerson.class);

	ActionResult<Wo> execute(String credential) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String person = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			person = business.organization().person().get(credential);
		}
		if (StringUtils.isNotEmpty(person)) {
			final String dn = person;
			CompletableFuture<Long> taskFuture = this.taskFuture(dn);
			CompletableFuture<Long> taskCompletedFuture = this.taskCompletedFuture(dn);
			CompletableFuture<Long> readFuture = this.readFuture(dn);
			CompletableFuture<Long> readCompletedFuture = this.readCompletedFuture(dn);
			CompletableFuture<Long> reviewFuture = this.reviewFuture(dn);
			wo.setTask(taskFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setTaskCompleted(
					taskCompletedFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setRead(readFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setReadCompleted(
					readCompletedFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setReview(reviewFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
		}
		result.setData(wo);
		return result;
	}

	private CompletableFuture<Long> taskFuture(String dn) {
		return CompletableFuture.supplyAsync(() -> {
			Long count = 0L;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				count = emc.countEqual(Task.class, Task.person_FIELDNAME, dn);
			} catch (Exception e) {
				logger.error(e);
			}
			return count;
		});
	}

	private CompletableFuture<Long> taskCompletedFuture(String dn) {
		return CompletableFuture.supplyAsync(() -> {
			Long count = 0L;
			// 已办仅取latest
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				EntityManager em;
				em = emc.get(TaskCompleted.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<TaskCompleted> root = cq.from(TaskCompleted.class);
				Predicate p = cb.equal(root.get(TaskCompleted_.person), dn);
				p = cb.and(p, cb.or(cb.equal(root.get(TaskCompleted_.latest), true),
						cb.isNull(root.get(TaskCompleted_.latest))));
				count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
			} catch (Exception e) {
				logger.error(e);
			}
			return count;
		});

	}

	private CompletableFuture<Long> readFuture(String dn) {
		return CompletableFuture.supplyAsync(() -> {
			Long count = 0L;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				count = emc.countEqual(Read.class, Read.person_FIELDNAME, dn);
			} catch (Exception e) {
				logger.error(e);
			}
			return count;
		});
	}

	private CompletableFuture<Long> readCompletedFuture(String dn) {
		return CompletableFuture.supplyAsync(() -> {
			Long count = 0L;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				count = emc.countEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME, dn);
			} catch (Exception e) {
				logger.error(e);
			}
			return count;
		});
	}

	private CompletableFuture<Long> reviewFuture(String dn) {
		return CompletableFuture.supplyAsync(() -> {
			Long count = 0L;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				count = emc.countEqual(Review.class, Review.person_FIELDNAME, dn);
			} catch (Exception e) {
				logger.error(e);
			}
			return count;
		});
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -4391978436352777470L;

		@FieldDescribe("待办数量")
		private Long task = 0L;
		@FieldDescribe("已办数量")
		private Long taskCompleted = 0L;
		@FieldDescribe("待阅数量")
		private Long read = 0L;
		@FieldDescribe("已阅数量")
		private Long readCompleted = 0L;
		@FieldDescribe("待阅数量")
		private Long review = 0L;

		public Long getTask() {
			return task;
		}

		public void setTask(Long task) {
			this.task = task;
		}

		public Long getTaskCompleted() {
			return taskCompleted;
		}

		public void setTaskCompleted(Long taskCompleted) {
			this.taskCompleted = taskCompleted;
		}

		public Long getRead() {
			return read;
		}

		public void setRead(Long read) {
			this.read = read;
		}

		public Long getReadCompleted() {
			return readCompleted;
		}

		public void setReadCompleted(Long readCompleted) {
			this.readCompleted = readCompleted;
		}

		public Long getReview() {
			return review;
		}

		public void setReview(Long review) {
			this.review = review;
		}

	}

}