package com.x.custom.index.assemble.control.jaxrs.reveal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.custom.index.assemble.control.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.express.index.Indexs;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListDirectory extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListDirectory.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Pair<List<Application>, List<AppInfo>> pair = business.listApplicationAppInfo();
			pair = this.filterEditable(effectivePerson, business, pair);
			wos = Stream
					.concat(pair.first().stream()
							.map(o -> new Wo(Indexs.CATEGORY_PROCESSPLATFORM, o.getName(), o.getId())),
							pair.second().stream().map(o -> new Wo(Indexs.CATEGORY_CMS, o.getAppName(), o.getId())))
					.collect(Collectors.toList());
		}
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionListDirectory$Wo")
	public class Wo extends com.x.query.core.express.index.Directory {

		public Wo(String category, String name, String key) {
			super(category, name, key);

		}

	}

}