package com.x.organization.assemble.authentication.servlet.oauth2server;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.http.ActionResult;

@WebServlet(urlPatterns = "/servlet/oauth2server/token/*")
public class TokenServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String code = request.getParameter("code");
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1" + code);
			List<String> info = new ArrayList<>();
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!3");
			info.add("access_token=" + URLEncoder.encode(code, "utf-8"));
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!4");
			info.add("expires_in=" + URLEncoder.encode("7776000", "utf-8"));
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!5");
			info.add("refresh_token=" + URLEncoder.encode(code, "utf-8"));
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!6");
			response.getWriter().print(StringUtils.join(info, "&"));
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}
}