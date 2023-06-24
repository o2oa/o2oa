package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.service.processing.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			checkFrom(effectivePerson, business, wi.getFromType(), wi.getFromBundle());
			List<Triple<String, String, String>> targets = this.readTarget(effectivePerson, business,
					wi.getTargetList());
			Map<String, Correlation> exists = this.exists(business, wi.getFromType(), wi.getFromBundle());
			targets.stream().forEach(o -> {
				exists.compute(o.first() + o.second(), null);

			});
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
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
				list.add(readTargetProcessPlatform(effectivePerson, business, targetWi.targetBundle));
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

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -6174739726994185000L;

		private String fromBundle;

		private String fromType;

		private List<TargetWi> targetList;

		public String getFromBundle() {
			return fromBundle;
		}

		public void setFromBundle(String fromBundle) {
			this.fromBundle = fromBundle;
		}

		public String getFromType() {
			return fromType;
		}

		public void setFromType(String fromType) {
			this.fromType = fromType;
		}

		public List<TargetWi> getTargetList() {
			return targetList;
		}

		public void setTargetList(List<TargetWi> targetList) {
			this.targetList = targetList;
		}

	}

	public static class TargetWi extends GsonPropertyObject {

		private static final long serialVersionUID = 395825437810549953L;

		private String targetType;
		private String targetBundle;

		public String getTargetType() {
			return targetType;
		}

		public void setTargetType(String targetType) {
			this.targetType = targetType;
		}

		public String getTargetBundle() {
			return targetBundle;
		}

		public void setTargetBundle(String targetBundle) {
			this.targetBundle = targetBundle;
		}

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -6203367255748339063L;

	}

}