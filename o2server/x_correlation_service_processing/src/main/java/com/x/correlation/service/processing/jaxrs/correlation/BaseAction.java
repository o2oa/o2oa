package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWo;
import com.x.correlation.service.processing.Business;
import com.x.correlation.service.processing.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean checkAllowVisitProcessPlatform(String person, String job) throws Exception {
		WrapBoolean resp = ThisApplication.context().applications()
				.getQuery(x_processplatform_assemble_surface.class,
						Applications.joinQueryUri("job", job, "allow", "visit", "person", person))
				.getData(WrapBoolean.class);
		return resp.getValue();
	}

	protected boolean checkPermissionReadFromCms(String person, String document) throws Exception {
		WrapBoolean resp = ThisApplication.context().applications().getQuery(x_cms_assemble_control.class,
				Applications.joinQueryUri("document", "cipher", document, "permission", "read", "person", person))
				.getData(WrapBoolean.class);
		return resp.getValue();
	}

	protected Map<String, Correlation> exists(Business business, String fromType, String fromBundle) throws Exception {
		return business.entityManagerContainer()
				.listEqualAndEqual(Correlation.class, Correlation.FROMTYPE_FIELDNAME, fromType,
						Correlation.FROMTYPE_FIELDNAME, fromBundle)
				.stream().collect(Collectors.toMap(o -> o.getTargetType() + o.getTargetBundle(), Function.identity()));
	}

	protected Pair<List<Correlation>, List<TargetWo>> readTarget(String person, Business business,
			List<TargetWi> targets) throws Exception {
		List<Correlation> successList = new ArrayList<>();
		List<TargetWo> failureList = new ArrayList<>();
		for (TargetWi targetWi : targets) {
			if (StringUtils.equalsIgnoreCase(targetWi.getType(), Correlation.TYPE_PROCESSPLATFORM)) {
				if (checkAllowVisitProcessPlatform(person, targetWi.getBundle())) {
					successList.add(readTargetProcessPlatform(person, business, targetWi.getBundle(),
							targetWi.getSite(), targetWi.getView()));
				} else {
					TargetWo targetWo = new TargetWo();
					targetWi.copyTo(targetWo);
					failureList.add(targetWo);
				}
			} else if (StringUtils.equalsIgnoreCase(targetWi.getType(), Correlation.TYPE_CMS)) {
				if (checkPermissionReadFromCms(person, targetWi.getBundle())) {
					successList.add(readTargetCms(person, business, targetWi.getBundle(), targetWi.getSite(),
							targetWi.getView()));
				} else {
					TargetWo targetWo = new TargetWo();
					targetWi.copyTo(targetWo);
					failureList.add(targetWo);
				}
			} else {
				throw new ExceptionAccessDenied(person, targetWi.getBundle());
			}
		}
		return Pair.of(successList, failureList);
	}

	protected Correlation readTargetProcessPlatform(String person, Business business, String bundle, String site,
			String view) throws Exception {
		Correlation correlation = new Correlation();
		correlation.setTargetType(Correlation.TYPE_PROCESSPLATFORM);
		correlation.setTargetBundle(bundle);
		correlation.setSite(site);
		correlation.setView(view);
		Work work = business.entityManagerContainer().firstEqual(Work.class, Work.job_FIELDNAME, bundle);
		if (null != work) {
			correlation.setTargetTitle(work.getTitle());
			correlation.setTargetCategory(work.getProcessName());
			correlation.setTargetStartTime(work.getStartTime());
			correlation.setTargetCreatorPerson(work.getCreatorPerson());
		} else {
			WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
					WorkCompleted.job_FIELDNAME, bundle);
			if (null != workCompleted) {
				correlation.setTargetTitle(workCompleted.getTitle());
				correlation.setTargetCategory(workCompleted.getProcessName());
				correlation.setTargetStartTime(workCompleted.getStartTime());
				correlation.setTargetCreatorPerson(workCompleted.getCreatorPerson());
			} else {
				throw new ExceptionAccessDenied(person, bundle);
			}
		}
		return correlation;
	}

	protected Correlation readTargetCms(String person, Business business, String bundle, String site, String view)
			throws Exception {
		Correlation correlation = new Correlation();
		correlation.setTargetType(Correlation.TYPE_CMS);
		correlation.setTargetBundle(bundle);
		correlation.setSite(site);
		correlation.setView(view);
		Document document = business.entityManagerContainer().firstEqual(Document.class, JpaObject.id_FIELDNAME,
				bundle);
		if (null != document) {
			correlation.setTargetTitle(document.getTitle());
			correlation.setTargetCategory(document.getAppName());
			correlation.setTargetStartTime(document.getPublishTime());
			correlation.setTargetCreatorPerson(document.getCreatorPerson());
		} else {
			throw new ExceptionAccessDenied(person, bundle);
		}
		return correlation;
	}

	protected boolean processPlatformHasReview(Business business, String person, List<String> jobs) throws Exception {
		return business.entityManagerContainer().countEqualAndIn(com.x.processplatform.core.entity.content.Review.class,
				com.x.processplatform.core.entity.content.Review.person_FIELDNAME, person,
				com.x.processplatform.core.entity.content.Review.job_FIELDNAME, jobs) > 0;
	}

	protected boolean cmsHasReviewOrPermissionAny(Business business, String person, List<String> ids) throws Exception {
		EntityManager em = business.entityManagerContainer().get(com.x.cms.core.entity.Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<com.x.cms.core.entity.Review> root = cq.from(com.x.cms.core.entity.Review.class);
		Predicate p = cb.or(cb.equal(root.get(com.x.cms.core.entity.Review_.permissionObj), person), cb.equal(
				root.get(com.x.cms.core.entity.Review_.permissionObj), com.x.cms.core.entity.Review.PERMISSION_ANY));
		p = cb.and(p, root.get(com.x.cms.core.entity.Review_.docId).in(ids));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

}
