package com.x.okr.assemble.control.servlet.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.jaxrs.okrtask.WrapOutOkrTaskCollect;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.organization.core.express.wrap.WrapPerson;

@WebServlet(urlPatterns = "/task/count")
public class TaskServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger( TaskServlet.class );
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();

	@HttpMethodDescribe(value = "获取待办数量，task/count?flag=liyi&callback=callback", response = Object.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutOkrTaskCollect> result = new ActionResult<>();
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
				result.setUserMessage("系统URL信息获取传入的flag时发生异常。");
				result.error(e);
				logger.error("system get flag from request url got an exception.", e);
			}
		}

		if (check) {
			try {
				person = okrUserManagerService.getUserByUserNumber(flag);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据根据人员唯一标识查询人员信息时发生异常。");
				logger.error("system get person by user flag got an exception.", e);
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
					result.error(e);
					result.setUserMessage("系统在根据用户姓名获取待办总数时发生异常。");
					logger.error("system get task count by user name got an exception.", e);
				}
			} else {
				check = false;
				result.error(new Exception("person{'flag':'" + flag + "'} is not exists."));
				result.setUserMessage("用户帐号'" + flag + "'不存在。");
				logger.error("person{'flag':'" + flag + "'} is not exists.");
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