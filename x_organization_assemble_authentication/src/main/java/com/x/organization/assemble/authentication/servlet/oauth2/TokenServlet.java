package com.x.organization.assemble.authentication.servlet.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.http.ActionResult;

@WebServlet(urlPatterns = "/servlet/oauth2/token/*")
public class TokenServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// response.setContentType("");
			response.getWriter().print("access_token=112233&expires_in=7776000&refresh_token=223344");
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			FileUploadServletTools.result(response, result);
		}
	}
}