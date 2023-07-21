package com.x.program.init.jaxrs.externaldatasources;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.gson.JsonSyntaxException;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ExternalDataSources;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	public static final Pattern EXTERNALDATASOURCES_PATTERN = Pattern.compile("^externalDataSources_(.*).json$",
			Pattern.CASE_INSENSITIVE);

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		result.setData(list());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7463810035710030958L;

		private String type;

		private ExternalDataSources externalDataSources;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public ExternalDataSources getExternalDataSources() {
			return externalDataSources;
		}

		public void setExternalDataSources(ExternalDataSources externalDataSources) {
			this.externalDataSources = externalDataSources;
		}

	}

	private List<Wo> list() throws IOException, URISyntaxException {
		List<Wo> list = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(Config.path_configSample(true), 1)) {
			stream.forEach(o -> {
				try {
					Matcher matcher = EXTERNALDATASOURCES_PATTERN.matcher(o.getFileName().toString());
					if (matcher.matches()) {
						Wo wo = new Wo();
						wo.setType(matcher.group(1));
						wo.setExternalDataSources(
								XGsonBuilder.instance().fromJson(Files.readString(o), ExternalDataSources.class));
						list.add(wo);
					}
				} catch (JsonSyntaxException | IOException e) {
					LOGGER.error(e);
				}
			});
		}
		return list;
	}

}