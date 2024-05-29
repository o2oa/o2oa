package com.x.base.core.project.jaxrs;

import com.x.base.core.project.config.Config;
import org.apache.commons.lang3.BooleanUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

public class ApiAccessFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		try {
			if (BooleanUtils.isFalse(Config.general().getExposeJest())) {
				HttpServletResponse response = (HttpServletResponse) res;
				response.setStatus(403);
				response.setHeader("Content-Type", "text/html;charset=utf-8");
				response.getWriter()
						.write("<html><body><div align='center'><h2>接口页面默认不开放,如需开启请在系统配置中启用.</h2></div></body></html>");
			} else {
				chain.doFilter(req, res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// nothing
	}

	@Override
	public void destroy() {
		// nothing
	}

}
