package com.x.program.center.jaxrs.config;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListDumpDataCurrentNode extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListDumpDataCurrentNode.class);

	private static final String DUMPDATA_PREFIX = "dumpData_";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> wos = new ArrayList<>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Config.path_local_dump(true))) {
			for (Path path : stream) {
				String fileName = path.getFileName().toString();
				if (StringUtils.startsWith(fileName, DUMPDATA_PREFIX)
						&& BooleanUtils.isTrue(
								DateTools.isCompactDateTime(StringUtils.substringAfter(fileName, DUMPDATA_PREFIX)))
						&& Files.exists(path.resolve("catalog.json"))) {
					Wo wo = new Wo();
					wo.setValue(fileName);
					wo.setNode(Config.node());
					wos.add(wo);
				}
			}
		}
		result.setData(wos.stream().sorted(Comparator.comparing(Wo::getValue).reversed()).collect(Collectors.toList()));
		return result;
	}

	@Schema(name = "com.x.program.center.jaxrs.config.ActionListDumpData$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -2850802416003861346L;

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
