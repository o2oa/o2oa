package com.x.program.center.jaxrs.market;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.enums.CommonStatus;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.InstallLog;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import com.x.query.core.entity.wrap.WrapQuery;

class ActionUninstall extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUninstall.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Application app = emc.find(id, Application.class);
			if (null == app) {
				throw new ExceptionEntityNotExist(id, Application.class);
			}
			InstallLog installLog = emc.find(id, InstallLog.class);
			if(installLog==null){
				throw new ExceptionEntityNotExist(id, InstallLog.class);
			}
			if(!hasAuth(effectivePerson, installLog.getInstallPerson())){
				throw new ExceptionAccessDenied(effectivePerson, app);
			}
			logger.print("{}发起卸载应用：{}", effectivePerson.getDistinguishedName(), app.getName());
			Wo wo = new Wo();
			InstallData installData = gson.fromJson(installLog.getData(), InstallData.class);
			List<WrapModule> moduleList = installData.getWrapModuleList();
			if(ListTools.isNotEmpty(moduleList)) {
				for(WrapModule module : moduleList){
					this.uninstall(module);
				}
			}

			emc.beginTransaction(InstallLog.class);
			installLog.setStatus(CommonStatus.INVALID.getValue());
			installLog.setUnInstallPerson(effectivePerson.getDistinguishedName());
			installLog.setUnInstallTime(new Date());
			emc.commit();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void uninstall(WrapModule module) throws Exception{
		boolean flag = true;
		try {
			if(module.getProcessPlatformList()!=null) {
				for (WrapProcessPlatform obj : module.getProcessPlatformList()) {
					ThisApplication.context().applications()
							.deleteQuery(x_processplatform_assemble_designer.class,
									Applications.joinQueryUri("application", obj.getId()), "true");
				}
			}
		} catch (Exception e) {
			logger.warn("卸载流程平台应用异常：{}",e.getMessage());
		}
		try {
			if(module.getCmsList()!=null) {
				for (WrapCms obj : module.getCmsList()) {
					ThisApplication.context().applications()
							.deleteQuery(x_cms_assemble_control.class,
									Applications.joinQueryUri("appinfo/erase/app", obj.getId()));
				}
			}
		} catch (Exception e) {
			logger.warn("卸载CMS应用异常：{}",e.getMessage());
		}
		try {
			if(module.getPortalList()!=null) {
				for (WrapPortal obj : module.getPortalList()) {
					ThisApplication.context().applications()
							.deleteQuery(x_portal_assemble_designer.class,
									Applications.joinQueryUri("portal", obj.getId()));
				}
			}
		} catch (Exception e) {
			logger.warn("卸载门户应用异常：{}",e.getMessage());
		}
		try {
			if(module.getQueryList()!=null){
				for (WrapQuery obj : module.getQueryList()) {
					ThisApplication.context().applications()
							.deleteQuery(x_query_assemble_designer.class,
									Applications.joinQueryUri("query", obj.getId()));
				}
			}
		} catch (Exception e) {
			logger.warn("卸载数据中心应用异常：{}",e.getMessage());
		}

		try {
			if(module.getServiceModuleList()!=null) {
				for (WrapServiceModule obj : module.getServiceModuleList()) {
					if(obj.getInvokeList()!=null){
						for(WrapInvoke invoke : obj.getInvokeList()){
							CipherConnectionAction.delete(false, Config.url_x_program_center_jaxrs("invoke", invoke.getId()));
						}
					}
					if(obj.getAgentList()!=null){
						for(WrapAgent agent : obj.getAgentList()){
							CipherConnectionAction.delete(false, Config.url_x_program_center_jaxrs("agent", agent.getId()));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.warn("卸载服务模块异常：{}",e.getMessage());
		}

	}

	private void uninstallCustomApp(String fileName) throws Exception{
		Nodes nodes = Config.nodes();
		for (String node : nodes.keySet()){
			if(nodes.get(node).getApplication().getEnable()) {
				logger.print("socket uninstall custom app{} to {}:{}",fileName, node, nodes.get(node).nodeAgentPort());
				try (Socket socket = new Socket(node, nodes.get(node).nodeAgentPort())) {
					socket.setKeepAlive(true);
					socket.setSoTimeout(5000);
					try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						 DataInputStream dis = new DataInputStream(socket.getInputStream())) {
						Map<String, Object> commandObject = new HashMap<>();
						commandObject.put("command", "uninstall:customWar");
						commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));

						dos.writeUTF(XGsonBuilder.toJson(commandObject));
						dos.flush();
						dos.writeUTF(fileName);
						dos.flush();
					}
				}
			}
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
