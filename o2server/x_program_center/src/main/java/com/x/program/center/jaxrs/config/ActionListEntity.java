package com.x.program.center.jaxrs.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ClassLoaderTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListEntity extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListEntity.class);

	private ClassLoader classLoader;

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		classLoader = ClassLoaderTools.urlClassLoader(Thread.currentThread().getContextClassLoader(), true, true, true,
				true);

		ActionResult<List<Wo>> result = new ActionResult<>();
		@SuppressWarnings("unchecked")
		List<Wo> wos = ((Map<String, List<String>>) Config.resource(Config.RESOURCE_CONTAINERENTITIES)).values()
				.stream().reduce(new TreeList<>(), (o, v) -> {
					o.addAll(v);
					return o;
				}).stream().distinct().filter(o -> StringUtils.isNotBlank(o) && (!StringUtils.contains(o, "*")))
				.sorted().map(mapper).collect(Collectors.toList());
		result.setData(wos);
		return result;
	}

	private Function<String, Wo> mapper = s -> {
		Wo wo = new Wo();
		wo.setValue(s);
		try {
			Class<?> clz = classLoader.loadClass(s);
			Schema schema = clz.getAnnotation(Schema.class);
			if (null != schema) {
				wo.setName(schema.description());
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error(e);
		}
		return wo;
	};

	public static class Wo extends NameValuePair {

		private static final long serialVersionUID = 1L;

	}

}
