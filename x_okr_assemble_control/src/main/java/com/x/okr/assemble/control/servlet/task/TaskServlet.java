package com.x.okr.assemble.control.servlet.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrtask.WrapOutOkrTaskCollect;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.organization.core.express.wrap.WrapPerson;

@WebServlet(urlPatterns = "/task/count")
public class TaskServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger( TaskServlet.class );
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();

	@HttpMethodDescribe(value = "获取待办数量，task/count?flag=liyi&callback=callback", response = Object.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutOkrTaskCollect> result = new ActionResult<>();
		EffectivePerson effectivePerson = null;
		List<String> taskTypeList = new ArrayList<String>();
		WrapPerson person = null;
		Long taskCount = 0L;
		String flag = null;
		boolean check = true;

		if (check) {
			try {
				flag = request.getParameter("flag");
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get flag from request url got an exception." );
				logger.error(e);
			}
		}

		if (check) {
			try {
				effectivePerson = this.effectivePerson(request);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get effectivePerson from request got an exception." );
				logger.error(e);
			}
		}
		
		if (check) {
			try {
				person = okrUserManagerService.getUserByUserNumber(flag);
			} catch (Exception e) {
				check = false;
				Exception exception = new PersonQueryException( e, flag );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (person != null) {
				taskTypeList.add( "中心工作");
				taskTypeList.add( "工作汇报汇总");
			    taskTypeList.add( "工作阅知" );
				try {
					taskCount = okrTaskService.getTaskCountByUserName( taskTypeList, person.getName());
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskCountQueryException( e, person.getName() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} else {
				check = false;
				Exception exception = new PersonNotExistsException( flag );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}

		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String callbackFunName = request.getParameter("callback");
		StringBuilder stringBuilder = new StringBuilder(callbackFunName);
		stringBuilder.append("( ");
		stringBuilder.append(taskCount);
		stringBuilder.append(" )");
		if (check) {
			out.println(stringBuilder.toString());
		} else {
			out.println(result);
		}
		out.flush();
		out.close();
	}
}