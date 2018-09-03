package o2.collect.assemble.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;

import o2.collect.assemble.jaxrs.account.AccountAction;
import o2.collect.assemble.jaxrs.applog.AppLogAction;
import o2.collect.assemble.jaxrs.authentication.AuthenticationAction;
import o2.collect.assemble.jaxrs.captcha.CaptchaAction;
import o2.collect.assemble.jaxrs.code.CodeAction;
import o2.collect.assemble.jaxrs.collect.CollectAction;
import o2.collect.assemble.jaxrs.device.DeviceAction;
import o2.collect.assemble.jaxrs.module.ModuleAction;
import o2.collect.assemble.jaxrs.prompterrorlog.PromptErrorLogAction;
import o2.collect.assemble.jaxrs.remote.RemoteAction;
import o2.collect.assemble.jaxrs.transmit.TransmitAction;
import o2.collect.assemble.jaxrs.unexpectederrorlog.UnexpectedErrorLogAction;
import o2.collect.assemble.jaxrs.unit.UnitAction;
import o2.collect.assemble.jaxrs.update.UpdateAction;
import o2.collect.assemble.jaxrs.warnlog.WarnLogAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(CodeAction.class);
		classes.add(AuthenticationAction.class);
		classes.add(AccountAction.class);
		classes.add(UnitAction.class);
		classes.add(CollectAction.class);
		classes.add(DeviceAction.class);
		classes.add(CaptchaAction.class);
		classes.add(TransmitAction.class);
		classes.add(UnexpectedErrorLogAction.class);
		classes.add(PromptErrorLogAction.class);
		classes.add(AppLogAction.class);
		classes.add(WarnLogAction.class);
		classes.add(ModuleAction.class);
		classes.add(UpdateAction.class);
		classes.add(RemoteAction.class);
		return classes;
	}

}
