package com.x.program.center.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.program.center.jaxrs.adminlogin.AdminLoginAction;
import com.x.program.center.jaxrs.agent.AgentAction;
import com.x.program.center.jaxrs.applications.ApplicationsAction;
import com.x.program.center.jaxrs.appstyle.AppStyleAction;
import com.x.program.center.jaxrs.authentication.AuthenticationAction;
import com.x.program.center.jaxrs.cachedispatch.CacheDispatchAction;
import com.x.program.center.jaxrs.captcha.CaptchaAction;
import com.x.program.center.jaxrs.center.CenterAction;
import com.x.program.center.jaxrs.code.CodeAction;
import com.x.program.center.jaxrs.collect.CollectAction;
import com.x.program.center.jaxrs.config.ConfigAction;
import com.x.program.center.jaxrs.dingding.DingdingAction;
import com.x.program.center.jaxrs.distribute.DistributeAction;
import com.x.program.center.jaxrs.invoke.InvokeAction;
import com.x.program.center.jaxrs.jest.JestAction;
import com.x.program.center.jaxrs.module.ModuleAction;
import com.x.program.center.jaxrs.pms.PmsAction;
import com.x.program.center.jaxrs.prompterrorlog.PromptErrorLogAction;
import com.x.program.center.jaxrs.qiyeweixin.QiyeweixinAction;
import com.x.program.center.jaxrs.schedule.ScheduleAction;
import com.x.program.center.jaxrs.test.TestAction;
import com.x.program.center.jaxrs.unexpectederrorlog.UnexpectedErrorLogAction;
import com.x.program.center.jaxrs.validation.ValidationAction;
import com.x.program.center.jaxrs.warnlog.WarnLogAction;
import com.x.program.center.jaxrs.zhengwudingding.ZhengwuDingdingAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(AdminLoginAction.class);
		classes.add(ApplicationsAction.class);
		classes.add(CacheDispatchAction.class);
		classes.add(CaptchaAction.class);
		classes.add(CenterAction.class);
		classes.add(CodeAction.class);
		classes.add(CollectAction.class);
		classes.add(DistributeAction.class);
		classes.add(PromptErrorLogAction.class);
		classes.add(UnexpectedErrorLogAction.class);
		classes.add(WarnLogAction.class);
		classes.add(JestAction.class);
		classes.add(ModuleAction.class);
		classes.add(PmsAction.class);
		classes.add(AgentAction.class);
		classes.add(InvokeAction.class);
		classes.add(AppStyleAction.class);
		classes.add(ConfigAction.class);
		classes.add(DingdingAction.class);
		classes.add(ZhengwuDingdingAction.class);
		classes.add(QiyeweixinAction.class);
		classes.add(ScheduleAction.class);
		classes.add(AuthenticationAction.class);
		classes.add(ValidationAction.class);
		classes.add(TestAction.class);
		return classes;
	}
}
