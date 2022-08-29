package com.x.program.center.jaxrs.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListDumpData extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListDumpData.class);

	private static final String DUMPDATA_PREFIX = "dumpData_";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> wos = Config.nodes().centerServers().entrySet().stream().map(mapper).reduce(new ArrayList<>(),
				(o1, o2) -> {
					o1.addAll(o2);
					return o1;
				});

		result.setData(wos.stream().sorted(Comparator.comparing(Wo::getValue).reversed()).collect(Collectors.toList()));
		return result;
	}

	private Function<Map.Entry<String, CenterServer>, List<Wo>> mapper = entry -> {
		List<Wo> wos = new ArrayList<>();
		try {
			wos = CipherConnectionAction.get(false, 2000, 4000,
					Config.url_x_program_center_jaxrs(entry, "config", "list", "dump", "data", "current", "node"))
					.<Wo>getDataAsList(Wo.class);
		} catch (Exception e) {
			LOGGER.warn("registerToCenter error:{}", e.getMessage());
		}
		return wos;
	};

	@Schema(name = "com.x.program.center.jaxrs.config.ActionListDumpData$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -7649649109954668631L;

		private String node;

		private String value;

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
