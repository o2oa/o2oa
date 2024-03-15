package com.x.program.center.jaxrs.config;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ClassLoaderTools;

public class ActionListApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListApplication.class);

	private ClassLoader classLoader;

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		classLoader = ClassLoaderTools.urlClassLoader(Thread.currentThread().getContextClassLoader(), true, true, true,
				true);

		ActionResult<List<Wo>> result = new ActionResult<>();
		@SuppressWarnings("unchecked")
		List<Wo> wos = Applications.OFFICIAL_APPLICATIONS.stream().map(mapper).collect(Collectors.toList());
		result.setData(wos);
		return result;
	}

	private Function<String, Wo> mapper = s -> {
		Wo wo = new Wo();
		wo.setValue(s);
		try {
			Class<?> clz = classLoader.loadClass(s);
			Module module = clz.getAnnotation(Module.class);
			if (null != module) {
				wo.setName(module.name());
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
