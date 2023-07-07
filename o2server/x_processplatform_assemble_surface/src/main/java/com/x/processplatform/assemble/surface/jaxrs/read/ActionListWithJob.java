package com.x.processplatform.assemble.surface.jaxrs.read;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.Read;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithJob.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);

			if (BooleanUtils.isNotTrue(
					new JobControlBuilder(effectivePerson, business, job).enableAllowVisit().build().getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}

			List<Wo> wos = Wo.copier.copy(emc.listEqual(Read.class, Read.job_FIELDNAME, job));

			wos = wos.stream().sorted(Comparator.comparing(Wo::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());

			result.setData(wos);

			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionListWithJob$Wo")
	public static class Wo extends Read {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}