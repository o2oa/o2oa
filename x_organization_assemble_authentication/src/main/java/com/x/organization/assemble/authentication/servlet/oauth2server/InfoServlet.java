package com.x.organization.assemble.authentication.servlet.oauth2server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

@WebServlet(urlPatterns = "/servlet/oauth2server/info/*")
public class InfoServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.setCharacterEncoding(request, response);
			String access_token = request.getParameter("access_token");
			Business business = new Business(emc);
			if (StringUtils.isNotEmpty(access_token)) {
				HttpToken httpToken = new HttpToken();
				EffectivePerson effectivePerson = httpToken.who(access_token, Config.token().getCipher());
				JsonObject jsonObject = new JsonObject();
				if (Config.token().isInitialManager( effectivePerson.getName())){
					jsonObject.addProperty("name", Config.token().initialManagerInstance().getName());
					jsonObject.addProperty("mail", Config.token().initialManagerInstance().getMail());
				} else {
					String personId = business.person().getWithName(effectivePerson.getName());
					if (StringUtils.isNotEmpty(personId)) {
						Person person = emc.find(personId, Person.class);
						if (null != person) {
							String name = person.getPinyin() + "(" + person.getMobile() + ")";
							String mail = person.getMail();
							if (StringUtils.isEmpty(mail)) {
								mail = person.getId() + "@o2oa.io";
							}
							jsonObject.addProperty("name", name);
							jsonObject.addProperty("mail", mail);
						}
					}
				}
				response.getWriter().print(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

}