package com.x.processplatform.assemble.surface.jaxrs;

import static io.swagger.v3.jaxrs2.integration.ServletConfigContextUtils.getContextIdFromServletConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;

@Path("/openapi")
public class OpenApiAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiAction.class);

	@Context
	ServletConfig servletConfig;

	@Context
	Application application;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(hidden = true)
	public Response getOpenApiJson(@Context HttpHeaders headers, @Context UriInfo uriInfo) throws Exception {
		return this.getOpenApi(headers, servletConfig, application, uriInfo, "json");
	}

	protected String getContextId(ServletConfig config) {
		return getContextIdFromServletConfig(config);
	}

	protected Response getOpenApi(HttpHeaders headers, ServletConfig config, Application app, UriInfo uriInfo,
			String type) throws Exception {

		String ctxId = getContextId(config);
		OpenApiContext ctx = new JaxrsOpenApiContextBuilder().servletConfig(config).application(app)
				.resourcePackages(resourcePackages).configLocation(configLocation)
				.openApiConfiguration(openApiConfiguration).ctxId(ctxId).buildContext(true);
		OpenAPI oas = ctx.read();
//		boolean pretty = false;
//		if (ctx.getOpenApiConfiguration() != null
//				&& Boolean.TRUE.equals(ctx.getOpenApiConfiguration().isPrettyPrint())) {
//			pretty = true;
//		}
//
//		if (oas != null) {
//			if (ctx.getOpenApiConfiguration() != null && ctx.getOpenApiConfiguration().getFilterClass() != null) {
//				try {
//					OpenAPISpecFilter filterImpl = (OpenAPISpecFilter) Class
//							.forName(ctx.getOpenApiConfiguration().getFilterClass()).getDeclaredConstructor()
//							.newInstance();
//					SpecFilter f = new SpecFilter();
//					oas = f.filter(oas, filterImpl, getQueryParams(uriInfo.getQueryParameters()), getCookies(headers),
//							getHeaders(headers));
//				} catch (Exception e) {
//					LOGGER.error("failed to load filter", e);
//				}
//			}
//		}

//		if (oas == null) {
//			return Response.status(404).build();
//		}

		return Response.status(Response.Status.OK).entity(XGsonBuilder.instance().toJson(oas))
				.type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	private static Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
		Map<String, List<String>> output = new HashMap<>();
		if (params != null) {
			params.forEach(output::put);
		}
		return output;
	}

	private static Map<String, String> getCookies(HttpHeaders headers) {
		Map<String, String> output = new HashMap<>();
		if (headers != null) {
			headers.getCookies().forEach((k, v) -> output.put(k, v.getValue()));
		}
		return output;
	}

	private static Map<String, List<String>> getHeaders(HttpHeaders headers) {
		Map<String, List<String>> output = new HashMap<>();
		if (headers != null) {
			headers.getRequestHeaders().forEach(output::put);
		}
		return output;
	}

	protected String configLocation;

	public String getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	public OpenApiAction configLocation(String configLocation) {
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

	public OpenApiAction resourcePackages(Set<String> resourcePackages) {
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

	public OpenApiAction openApiConfiguration(OpenAPIConfiguration openApiConfiguration) {
		setOpenApiConfiguration(openApiConfiguration);
		return this;
	}
}
