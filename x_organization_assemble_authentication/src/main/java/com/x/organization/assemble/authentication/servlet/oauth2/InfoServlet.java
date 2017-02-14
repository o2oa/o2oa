package com.x.organization.assemble.authentication.servlet.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.http.ActionResult;

@WebServlet(urlPatterns = "/servlet/oauth2/info/*")
public class InfoServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			response.getWriter().print("email=zhourui@zoneland.net&given_name=rui&family_name=zhou");
			// response.getWriter().print("email=zhourui@zoneland.net&name=rui&employee=zhou");
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			FileUploadServletTools.result(response, result);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!InfoServlet Get");
			System.out.println(request.getRequestURI());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!InfoServlet Get");
			System.out.println(request.getQueryString());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!InfoServlet Get");
		response.getWriter().print("email=zhourui@zoneland.net&given_name=rui&family_name=zhou");
			//response.getWriter().print("mail=zhourui@zoneland.net&name=rui&employee=zhou");
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			FileUploadServletTools.result(response, result);
		}
	}
}