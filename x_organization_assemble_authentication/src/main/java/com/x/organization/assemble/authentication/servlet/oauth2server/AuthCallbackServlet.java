package com.x.organization.assemble.authentication.servlet.oauth2server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.http.ActionResult;

@WebServlet(urlPatterns = "/servlet/oauth2/authcallback/*")
public class AuthCallbackServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			System.out.println(request.getQueryString());
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}
}