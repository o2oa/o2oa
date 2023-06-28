package com.x.processplatform.assemble.surface.jaxrs.documentversion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDeniedOrEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.DocumentVersion;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithJobCategory extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithJobCategory.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job, String category) throws Exception {

		LOGGER.debug("execute:{}, job:{}, category:{}.", effectivePerson::getDistinguishedName, () -> job,
				() -> category);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);

			if (BooleanUtils.isNotTrue(
					new JobControlBuilder(effectivePerson, business, job).enableAllowVisit().build().getAllowVisit())) {
				throw new ExceptionAccessDeniedOrEntityNotExist(effectivePerson);
			}

			List<Wo> wos = this.list(business, job, category);

			wos = wos.stream().sorted(Comparator.comparing(Wo::getCreateTime).reversed()).collect(Collectors.toList());

			result.setData(wos);

			return result;
		}
	}

	private List<Wo> list(Business business, String job, String category) throws Exception {
		List<DocumentVersion> os = business.entityManagerContainer().fetchEqualAndEqual(DocumentVersion.class,
				DocumentVersion.job_FIELDNAME, job, DocumentVersion.category_FIELDNAME, category);
		return Wo.copier.copy(os);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.documentversion.ActionListWithJobCategory$Wo")
	public static class Wo extends DocumentVersion {

		static final long serialVersionUID = 5610132069178497370L;

		static WrapCopier<DocumentVersion, Wo> copier = WrapCopierFactory.wo(DocumentVersion.class, Wo.class,
				JpaObject.singularAttributeField(DocumentVersion.class, true, false), null);

	}

}