package com.x.program.init.jaxrs.externaldatasources;

import com.google.gson.JsonSyntaxException;
import com.x.base.core.entity.LcInfo;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ExternalDataSources;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	public static final Pattern EXTERNALDATASOURCES_PATTERN = Pattern.compile("^externalDataSources_(.*).json$",
			Pattern.CASE_INSENSITIVE);
	private static final Map<String, String> DB_MAP = Map.of("dm", "达梦数据库", "gbase", "南大通用", "gbasemysql", "南大通用mysql版",
			"kingbase", "金仓数据库", "kingbase8", "金仓数据库V8", "oscar", "万里数据库", "vastbase","神通数据库");

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		result.setData(list());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7463810035710030958L;
		@FieldDescribe("数据库类型.")
		private String type;
		@FieldDescribe("数据库名称.")
		private String name;
		@FieldDescribe("数据库连接配置.")
		private ExternalDataSources externalDataSources;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ExternalDataSources getExternalDataSources() {
			return externalDataSources;
		}

		public void setExternalDataSources(ExternalDataSources externalDataSources) {
			this.externalDataSources = externalDataSources;
		}

	}

	private List<Wo> list() throws IOException, URISyntaxException {
		LcInfo lc = null;
		try {
			Class<?> licenseToolsCls = Class.forName("com.x.base.core.lc.LcTools");
			String info = (String) MethodUtils.invokeStaticMethod(licenseToolsCls, "getInfo");
			if(StringUtils.isNotBlank(info)){
				lc = XGsonBuilder.instance().fromJson(info, LcInfo.class);
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		final LcInfo lcInfo = lc;
		List<Wo> list = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(Config.path_configSample(true), 1)) {
			stream.forEach(o -> {
				try {
					Matcher matcher = EXTERNALDATASOURCES_PATTERN.matcher(o.getFileName().toString());
					if (matcher.matches()) {
						String type = matcher.group(1);
						if(lcInfo == null || lcInfo.getSupportDbList().contains(type)){
							Wo wo = new Wo();
							wo.setType(type);
							String name = DB_MAP.getOrDefault(type, type);
							wo.setName(name);
							wo.setExternalDataSources(
									XGsonBuilder.instance().fromJson(Files.readString(o), ExternalDataSources.class));
							list.add(wo);
						}
					}
				} catch (JsonSyntaxException | IOException e) {
					LOGGER.error(e);
				}
			});
		}
		return list;
	}

}
