package com.x.query.assemble.surface.jaxrs.neural;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.neural.Model;

class ActionListCalculate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListCalculate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String modelFlag, String workId)
			throws Exception {
		logger.debug(effectivePerson, "modelFlag:{}, workId:{}.", modelFlag, workId);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Model model = emc.flag(modelFlag, Model.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(modelFlag, Model.class);
			}
			Work work = emc.flag(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			List<Wo> wos = ThisApplication
					.context().applications().getQuery(x_query_service_processing.class, Applications
							.joinQueryUri("neural", "list", "calculate", "model", model.getId(), "work", work.getId()))
					.getDataAsList(Wo.class);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private Double score;

		private String value;

		public Double getScore() {
			return score;
		}

		public void setScore(Double score) {
			this.score = score;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
