package com.x.organization.assemble.authentication.servlet.oauth2server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;

@WebServlet(urlPatterns = "/servlet/oauth2server/auth/*")
public class AuthServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String redirectUrl = request.getParameter("redirect_uri");
			redirectUrl += "&code=" + effectivePerson.getToken();
			response.sendRedirect(redirectUrl);
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}
}