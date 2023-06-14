package com.x.program.init.jaxrs.server;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Server;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Server server = (Server) request.getServletContext().getAttribute(Server.class.getName());
		Enumeration<String> names = request.getServletContext().getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Object o = request.getAttribute(name);
			System.out.println(name + "-----" + o);
		}

		Wo wo = new Wo();
		//wo.setValue(server.toString());
		return result;
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = -732662981208435732L;

	}

}