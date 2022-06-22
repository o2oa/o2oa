package com.x.base.core.project.jaxrs.openapi;

import static io.swagger.v3.jaxrs2.integration.ServletConfigContextUtils.getContextIdFromServletConfig;

import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;

public class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	private static Wo wo;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext,
			ServletConfig servletConfig, Application application) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = get(servletConfig, application);
		result.setData(wo);
		return result;
	}

	protected String getContextId(ServletConfig config) {
		return getContextIdFromServletConfig(config);
	}

	protected Wo get(ServletConfig config, Application application) throws OpenApiConfigurationException {
		synchronized (ActionGet.class) {
			if (null == wo) {
				String ctxId = getContextId(config);
				@SuppressWarnings("rawtypes")
				OpenApiContext ctx = new JaxrsOpenApiContextBuilder().servletConfig(config).application(application)
//						.resourcePackages(resourcePackages).configLocation(configLocation)
//						.openApiConfiguration(openApiConfiguration)
						.ctxId(ctxId).buildContext(true);
				wo = new Wo();
				wo.setText(gson.toJson(ctx.read()));
			}
			return wo;
		}
	}

	protected String configLocation;

	public String getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	public ActionGet configLocation(String configLocation) {
		setConfigLocation(configLocation);
		return this;
	}

	protected Set<String> resourcePackages;

	public Set<String> getResourcePackages() {
		return resourcePackages;
	}

	public void setResourcePackages(Set<String> resourcePackages) {
		this.resourcePackages = resourcePackages;
	}

	public ActionGet resourcePackages(Set<String> resourcePackages) {
		setResourcePackages(resourcePackages);
		return this;
	}

	protected OpenAPIConfiguration openApiConfiguration;

	public OpenAPIConfiguration getOpenApiConfiguration() {
		return openApiConfiguration;
	}

	public void setOpenApiConfiguration(OpenAPIConfiguration openApiConfiguration) {
		this.openApiConfiguration = openApiConfiguration;
	}

	public ActionGet openApiConfiguration(OpenAPIConfiguration openApiConfiguration) {
		setOpenApiConfiguration(openApiConfiguration);
		return this;
	}

	public static class Wo extends WoText {

		private static final long serialVersionUID = 2588451195416207384L;

	}
}
