package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWi;
import com.x.correlation.service.processing.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionCreateTypeProcessPlatform extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateTypeProcessPlatform.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, job:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> job,
				() -> jsonElement);

		ActionResult<List<Wo>> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<Wo> wos = new ArrayList<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<Triple<String, String, String>> targets = this.readTarget(effectivePerson, business,
					wi.getTargetList());
			Map<String, Correlation> exists = this.exists(business, Correlation.TYPE_PROCESSPLATFORM, job);
			targets.stream().forEach(o -> exists.compute(o.first() + o.second(), (k, v) -> {
				try {
					if (null == v) {
						v = new Correlation();
						v.setFromType(Correlation.TYPE_PROCESSPLATFORM);
						v.setFromBundle(job);
						v.setTargetType(o.first());
						v.setTargetBundle(o.second());
						v.setTitle(o.third());
						v.setPerson(effectivePerson.getDistinguishedName());
						emc.persist(v, CheckPersistType.all);
						wos.add(Wo.copier.copy(v));
					} else {
						v.setTitle(o.third());
						v.setPerson(effectivePerson.getDistinguishedName());
						emc.check(v, CheckPersistType.all);
						wos.add(Wo.copier.copy(v));
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
				return v;
			}));
			emc.commit();
			result.setData(wos);
			return result;
		}
	}

	private Map<String, Correlation> exists(Business business, String fromType, String fromBundle) throws Exception {
		return business.entityManagerContainer()
				.listEqualAndEqual(Correlation.class, Correlation.FROMTYPE_FIELDNAME, fromType,
						Correlation.FROMTYPE_FIELDNAME, fromBundle)
				.stream().collect(Collectors.toMap(o -> o.getTargetType() + o.getTargetBundle(), Function.identity()));
	}

	private void checkFrom(EffectivePerson effectivePerson, Business business, String type, String bundle)
			throws Exception {
		if (StringUtils.equalsIgnoreCase(type, Correlation.TYPE_PROCESSPLATFORM)) {
			checkFromProcessPlatform(effectivePerson, business, bundle);
		} else if (StringUtils.equalsIgnoreCase(type, Correlation.TYPE_CMS)) {
			checkFromCms(effectivePerson, business, bundle);
		} else {
			throw new ExceptionAccessDenied(effectivePerson);
		}
	}

	private void checkFromProcessPlatform(EffectivePerson effectivePerson, Business business, String bundle)
			throws Exception {
		if (business.entityManagerContainer().countEqualAndEqual(com.x.processplatform.core.entity.content.Review.class,
				com.x.processplatform.core.entity.content.Review.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), com.x.processplatform.core.entity.content.Review.job_FIELDNAME,
				bundle) == 0) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
	}

	private void checkFromCms(EffectivePerson effectivePerson, Business business, String bundle) throws Exception {
		if (business.entityManagerContainer().countEqualAndEqual(com.x.cms.core.entity.Review.class,
				com.x.cms.core.entity.Review.permissionObj_FIELDNAME, effectivePerson.getDistinguishedName(),
				com.x.cms.core.entity.Review.docId_FIELDNAME, bundle) == 0) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
	}

	private List<Triple<String, String, String>> readTarget(EffectivePerson effectivePerson, Business business,
			List<TargetWi> targets) throws Exception {
		List<Triple<String, String, String>> list = new ArrayList<>();
		for (TargetWi targetWi : targets) {
			if (StringUtils.equalsIgnoreCase(targetWi.getTargetType(), Correlation.TYPE_PROCESSPLATFORM)) {
				list.add(readTargetProcessPlatform(effectivePerson, business, targetWi.getTargetBundle()));
			} else if (StringUtils.equalsIgnoreCase(targetWi.getTargetType(), Correlation.TYPE_CMS)) {
				list.add(readTargetCms(effectivePerson, business, targetWi.getTargetBundle()));
			} else {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		return list;
	}

	private Triple<String, String, String> readTargetProcessPlatform(EffectivePerson effectivePerson, Business business,
			String bundle) throws Exception {
		Work work = business.entityManagerContainer().firstEqual(Work.class, Work.job_FIELDNAME, bundle);
		if (null != work) {
			return Triple.of(Correlation.TYPE_PROCESSPLATFORM, bundle, work.getTitle());
		} else {
			WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
					WorkCompleted.job_FIELDNAME, bundle);
			if (null != workCompleted) {
				return Triple.of(Correlation.TYPE_PROCESSPLATFORM, bundle, workCompleted.getTitle());
			} else {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
	}

	private Triple<String, String, String> readTargetCms(EffectivePerson effectivePerson, Business business,
			String bundle) throws Exception {
		Document document = business.entityManagerContainer().firstEqual(Document.class, JpaObject.id_FIELDNAME,
				bundle);
		if (null != document) {
			return Triple.of(Correlation.TYPE_CMS, bundle, document.getTitle());
		} else {
			throw new ExceptionAccessDenied(effectivePerson);
		}
	}

	public static class Wi extends ActionCreateTypeProcessPlatformWi {

		private static final long serialVersionUID = -1782585450737681793L;

	}

	public static class Wo extends ActionCreateTypeProcessPlatformWo {

		private static final long serialVersionUID = 8119049505336942577L;

		static WrapCopier<Correlation, Wo> copier = WrapCopierFactory.wo(Correlation.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}