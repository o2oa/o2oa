package com.x.program.center.jaxrs.module;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Structure;
import com.x.program.center.core.entity.Structure_;

public class ActionListStructure extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListStructure.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Structure> os = emc.listAll(Structure.class);
			List<Wo> wos = Wo.copier.copy(os);
			wos = wos.stream().sorted(Comparator.comparing(Structure::getCreateTime, Comparator.nullsLast(Date::compareTo)).reversed())
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Structure {

		private static final long serialVersionUID = -7954190536053029081L;

		static WrapCopier<Structure, Wo> copier = WrapCopierFactory.wo(Structure.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}