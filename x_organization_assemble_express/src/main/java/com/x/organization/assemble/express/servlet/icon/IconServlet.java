package com.x.organization.assemble.express.servlet.icon;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.entity.Person;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@WebServlet("/servlet/icon/*")
public class IconServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;

	private Ehcache cache = ApplicationCache.instance().getCache(Person.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream outputStream = response.getOutputStream();
		byte[] wraps = null;
		response.setHeader("Content-Type", "image/png");
		try {
			this.setCharacterEncoding(request, response);
			String name = this.getURIPart(request.getRequestURI(), "icon");
			if (StringUtils.isNotEmpty(name)) {
				name = URLDecoder.decode(name, "UTF-8");
				String cacheKey = name;
				Element element = cache.get(cacheKey);
				if (element != null) {
					wraps = (byte[]) element.getObjectValue();
				} else {
					if (Config.token().isInitialManager(name)) {
						wraps = Base64.decodeBase64(Config.token().initialManagerInstance().getIcon());
					} else {
						try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
							Business business = new Business(emc);
							String personId = business.person().getWithName(name);
							if (StringUtils.isNotEmpty(personId)) {
								Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
								String icon = "";
								if (StringUtils.isNotEmpty(person.getIcon())) {
									icon = person.getIcon();
								} else if (Objects.equals(person.getGenderType(), GenderType.m)) {
									icon = com.x.base.core.project.server.Person.ICON_MALE;
								} else if (Objects.equals(person.getGenderType(), GenderType.f)) {
									icon = com.x.base.core.project.server.Person.ICON_FEMALE;
								} else {
									icon = com.x.base.core.project.server.Person.ICON_UNKOWN;
								}
								wraps = Base64.decodeBase64(icon);
								cache.put(new Element(cacheKey, wraps));
							} else {
								wraps = Base64.decodeBase64(com.x.base.core.project.server.Person.ICON_UNKOWN);
							}
						}
					}
				}
			} else {
				wraps = Base64.decodeBase64(com.x.base.core.project.server.Person.ICON_UNKOWN);
			}
			outputStream.write(wraps);
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			outputStream.write(result.toJson().getBytes());
		}
	}
}