package com.x.organization.assemble.authentication.jaxrs.oauth2server;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

@Path("oauth2server")
public class OAuth2ServerAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "")
	@GET
	@Path("auth")
	// @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response auth(@Context HttpServletRequest request, @QueryParam("redirect_uri") String redirect_uri,
			@QueryParam("response_type") String response_type, @QueryParam("client_id") String client_id,
			@QueryParam("state") String state, @QueryParam("scope") String scope) {
		try {
			HttpToken httpToken = new HttpToken();
			String token = httpToken.getToken(request);
			if (StringUtils.isNotEmpty(token)) {
				String url = redirect_uri;
				if (StringUtils.containsAny(url, "?", "!")) {
					url += "&";
				} else {
					url += "?";
				}
				url += "code=" + URLEncoder.encode(token, "utf-8");
				if (StringUtils.isNotEmpty(state)) {
					url += "&state=" + URLEncoder.encode(state, "utf-8");
				}
				return Response.seeOther(new URI(url)).build();
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return null;
	}

	@HttpMethodDescribe(value = "")
	@GET
	@Path("token")
	// @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response token_method_get(@Context HttpServletRequest request,
			@QueryParam("redirect_uri") String redirect_uri, @QueryParam("client_id") String client_id,
			@QueryParam("client_secret") String client_secret, @QueryParam("code") String code,
			@QueryParam("grant_type") String grant_type) {
		try {
			List<String> info = new ArrayList<>();
			info.add("access_token=" + URLEncoder.encode(code, "utf-8"));
			info.add("expires_in=" + URLEncoder.encode("7776000", "utf-8"));
			info.add("refresh_token=" + URLEncoder.encode(code, "utf-8"));
			return Response.ok(StringUtils.join(info, "&"), MediaType.TEXT_PLAIN).build();
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return null;
	}

	@HttpMethodDescribe(value = "")
	@POST
	@Path("token")
	@Produces(MediaType.TEXT_PLAIN)
	// @Consumes(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response token_method_post(@Context HttpServletRequest request,
			@FormParam("redirect_uri") String redirect_uri, @FormParam("client_id") String client_id,
			@FormParam("client_secret") String client_secret, @FormParam("code") String code,
			@FormParam("grant_type") String grant_type) {
		try {
			List<String> info = new ArrayList<>();
			info.add("access_token=" + URLEncoder.encode(code, "utf-8"));
			info.add("expires_in=" + URLEncoder.encode("7776000", "utf-8"));
			info.add("refresh_token=" + URLEncoder.encode(code, "utf-8"));
			return Response.ok(StringUtils.join(info, "&"), MediaType.TEXT_PLAIN).build();
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return null;
	}

	@HttpMethodDescribe(value = "")
	@GET
	@Path("info")
	public Response info(@Context HttpServletRequest request, @Context HttpServletResponse response,
			@QueryParam("access_token") String access_token) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			HttpToken httpToken = new HttpToken();
			String token = access_token;
			if (StringUtils.isEmpty(token)) {
				token = httpToken.getToken(request);
			}
			if (StringUtils.isNotEmpty(token)) {
				EffectivePerson effectivePerson = httpToken.who(token, Config.token().getCipher());
				List<String> info = new ArrayList<>();
				if (TokenType.user.equals(effectivePerson.getToken())) {
					String personId = business.person().getWithName(effectivePerson.getName());
					if (StringUtils.isNotEmpty(personId)) {
						Person person = emc.find(personId, Person.class);
						if (null != person) {
							info.add("name=" + person.getName());
							info.add("email=" + person.getMail());
							info.add("employee=" + person.getEmployee());
						}
					}
				}
				if (TokenType.manager.equals(effectivePerson.getToken())) {
					info.add("name=" + Config.administrator().getName());
					info.add("email=" + Config.administrator().getMail());
					info.add("employee=" + Config.administrator().getEmployee());
				}
				return Response.ok(StringUtils.join(info, "&")).build();
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return null;
	}
}