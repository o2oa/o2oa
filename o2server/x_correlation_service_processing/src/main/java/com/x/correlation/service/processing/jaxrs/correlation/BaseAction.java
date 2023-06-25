package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWi;
import com.x.correlation.service.processing.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

abstract class BaseAction extends StandardJaxrsAction {

	protected void checkFromProcessPlatform(Business business, String person, String job) throws Exception {
		if (business.entityManagerContainer().countEqualAndEqual(com.x.processplatform.core.entity.content.Review.class,
				com.x.processplatform.core.entity.content.Review.person_FIELDNAME, person,
				com.x.processplatform.core.entity.content.Review.job_FIELDNAME, job) == 0) {
			throw new ExceptionEntityNotExist(job);
		}
	}

	protected void checkFromCms(Business business, String person, String document) throws Exception {
		if (business.entityManagerContainer().countEqualAndEqual(com.x.cms.core.entity.Review.class,
				com.x.cms.core.entity.Review.permissionObj_FIELDNAME, person,
				com.x.cms.core.entity.Review.docId_FIELDNAME, document) == 0) {
			throw new ExceptionEntityNotExist(document);
		}
	}

	protected Map<String, Correlation> exists(Business business, String fromType, String fromBundle) throws Exception {
		return business.entityManagerContainer()
				.listEqualAndEqual(Correlation.class, Correlation.FROMTYPE_FIELDNAME, fromType,
						Correlation.FROMTYPE_FIELDNAME, fromBundle)
				.stream().collect(Collectors.toMap(o -> o.getTargetType() + o.getTargetBundle(), Function.identity()));
	}

	protected List<Correlation> readTarget(EffectivePerson effectivePerson, Business business, List<TargetWi> targets)
			throws Exception {
		List<Correlation> list = new ArrayList<>();
		for (TargetWi targetWi : targets) {
			if (StringUtils.equalsIgnoreCase(targetWi.getType(), Correlation.TYPE_PROCESSPLATFORM)) {
				list.add(
						readTargetProcessPlatform(effectivePerson, business, targetWi.getBundle(), targetWi.getSite()));
			} else if (StringUtils.equalsIgnoreCase(targetWi.getType(), Correlation.TYPE_CMS)) {
				list.add(readTargetCms(effectivePerson, business, targetWi.getBundle(), targetWi.getSite()));
			} else {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		return list;
	}

	protected Correlation readTargetProcessPlatform(EffectivePerson effectivePerson, Business business, String bundle,
			String site) throws Exception {
		Correlation correlation = new Correlation();
		correlation.setTargetType(Correlation.TYPE_PROCESSPLATFORM);
		correlation.setTargetBundle(bundle);
		correlation.setSite(site);
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
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		return correlation;
	}

	protected Correlation readTargetCms(EffectivePerson effectivePerson, Business business, String bundle, String site)
			throws Exception {
		Correlation correlation = new Correlation();
		correlation.setTargetType(Correlation.TYPE_PROCESSPLATFORM);
		correlation.setTargetBundle(bundle);
		correlation.setSite(site);
		Document document = business.entityManagerContainer().firstEqual(Document.class, JpaObject.id_FIELDNAME,
				bundle);
		if (null != document) {
			correlation.setTargetTitle(document.getTitle());
			correlation.setTargetCategory(document.getAppName());
			correlation.setTargetStartTime(document.getPublishTime());
			correlation.setTargetCreatorPerson(document.getCreatorPerson());
		} else {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		return correlation;
	}

}
