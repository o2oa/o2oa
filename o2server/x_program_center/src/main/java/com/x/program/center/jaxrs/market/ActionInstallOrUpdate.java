package com.x.program.center.jaxrs.market;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.enums.CommonStatus;
import com.x.base.core.project.*;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.*;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.InstallLog;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import com.x.query.core.entity.wrap.WrapQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.*;

class ActionInstallOrUpdate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInstallOrUpdate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application app = emc.find(id, Application.class);
			if (null == app) {
				throw new ExceptionEntityNotExist(id, Application.class);
			}
			if(!hasAuth(effectivePerson, null)){
				throw new ExceptionAccessDenied(effectivePerson, app);
			}
			logger.print("{}发起安装或更新应用：{}", effectivePerson.getDistinguishedName(), app.getName());
			Wo wo = new Wo();
			wo.setValue(false);
			if(BooleanUtils.isTrue(Config.collect().getEnable())) {
				String token = business.loginCollect();
				if(StringUtils.isNotEmpty(token)){
					byte[] bytes = ConnectionAction.getFile(Config.collect().url(Collect.ADDRESS_COLLECT_APPLICATION_DOWN + "/" + id),
							ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
					if(bytes!=null){
						InstallData installData = this.install(id, bytes);
						if(installData!=null) {
							wo.setValue(true);
							emc.beginTransaction(InstallLog.class);
							InstallLog installLog = emc.find(id, InstallLog.class);
							boolean exist = true;
							if (installLog == null) {
								installLog = new InstallLog();
								installLog.setId(app.getId());
								exist = false;
							}
							installLog.setName(app.getName());
							installLog.setVersion(app.getVersion());
							installLog.setCategory(app.getCategory());
							installLog.setStatus(CommonStatus.VALID.getValue());
							installLog.setData(gson.toJson(installData));
							installLog.setInstallPerson(effectivePerson.getDistinguishedName());
							installLog.setInstallTime(new Date());
							installLog.setUnInstallPerson(null);
							installLog.setUnInstallTime(null);
							if(!exist){
								emc.persist(installLog);
							}
							emc.commit();
						}
					}
				}
			}

			result.setData(wo);
			return result;
		}
	}

	private InstallData install(String id, byte[] bytes) throws Exception{
		InstallData installData = new InstallData();
		File tempFile = new File(Config.base(), "local/temp/install");
		FileTools.forceMkdir(tempFile);
		FileUtils.cleanDirectory(tempFile);
		File zipFile = new File(tempFile.getAbsolutePath(), id+".zip");
		FileUtils.writeByteArrayToFile(zipFile, bytes);
		File dist = new File(tempFile.getAbsolutePath(), "data");
		FileTools.forceMkdir(dist);
		JarTools.unjar(zipFile, new ArrayList<>(), dist, true);

		File[] files = dist.listFiles();
		for(File file : files){
			if(!file.isDirectory()){
				if(file.getName().toLowerCase().endsWith(".xapp")){
					String json = FileUtils.readFileToString(file, DefaultCharset.charset);
					Gson gson = new Gson();
					JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
					WrapModule module = this.convertToWrapIn(jsonElement, WrapModule.class);
					this.installModule(module);
					installData.setWrapModule(module);
				}else if(file.getName().toLowerCase().endsWith(".app.zip")){
					logger.print("开始安装自定义应用：{}", file.getName());
					this.installCustomApp(file.getName(), FileUtils.readFileToByteArray(file));
					installData.setCustomApp(file.getName());
					logger.print("完成自定义应用安装：{}", file.getName());
				}else if(file.getName().toLowerCase().endsWith(".zip")){
					logger.print("开始安装静态资源");
					try {
						Business.dispatch(false, file.getName(), "", FileUtils.readFileToByteArray(file));
						installData.setStaticResource(file.getName());
					} catch (Exception e) {
						logger.print("模块安装成功但静态资源安装失败:{}",e.getMessage());
					}
				}
			}
		}
		FileUtils.cleanDirectory(tempFile);
		return installData;
	}

	private InstallWo installModule(WrapModule module) throws Exception{
		InstallWo wo = new InstallWo();
		logger.print("开始安装应用");
		if(module.getProcessPlatformList()!=null) {
			for (WrapProcessPlatform obj : module.getProcessPlatformList()) {
				wo.getProcessPlatformList()
						.add(ThisApplication.context().applications()
								.putQuery(x_processplatform_assemble_designer.class,
										Applications.joinQueryUri("input", "cover"), obj)
								.getData(WoId.class).getId());
				obj.setIcon(null);
				obj.setApplicationDictList(null);
				obj.setFileList(null);
				obj.setFormList(null);
				obj.setProcessList(null);
				obj.setScriptList(null);
			}
		}
		if(module.getCmsList()!=null) {
			for (WrapCms obj : module.getCmsList()) {
				wo.getCmsList()
						.add(ThisApplication.context().applications()
								.putQuery(x_cms_assemble_control.class,
										Applications.joinQueryUri("input", "cover"), obj)
								.getData(WoId.class).getId());
				obj.setAppIcon(null);
				obj.setAppDictList(null);
				obj.setCategoryInfoList(null);
				obj.setFileList(null);
				obj.setFormList(null);
				obj.setScriptList(null);
			}
		}
		if(module.getPortalList()!=null) {
			for (WrapPortal obj : module.getPortalList()) {
				wo.getPortalList()
						.add(ThisApplication.context().applications()
								.putQuery(x_portal_assemble_designer.class,
										Applications.joinQueryUri("input", "cover"), obj)
								.getData(WoId.class).getId());
				obj.setIcon(null);
				obj.setFileList(null);
				obj.setPageList(null);
				obj.setScriptList(null);
				obj.setWidgetList(null);
			}
		}
		if(module.getQueryList()!=null){
			for (WrapQuery obj : module.getQueryList()) {
				wo.getQueryList()
						.add(ThisApplication.context().applications()
								.putQuery(x_query_assemble_designer.class,
										Applications.joinQueryUri("input", "cover"), obj)
								.getData(WoId.class).getId());
				obj.setIcon(null);
				obj.setRevealList(null);
				obj.setViewList(null);
				obj.setStatementList(null);
				obj.setViewList(null);
				obj.setTableList(null);
			}
		}

		if(module.getServiceModuleList()!=null) {
			for (WrapServiceModule obj : module.getServiceModuleList()) {
				wo.getServiceModuleList()
						.add(CipherConnectionAction.put(false,
								Config.url_x_program_center_jaxrs("input", "cover"), obj)
								.getData(WoId.class).getId());
				if (obj.getAgentList() != null) {
					for (WrapAgent agent : obj.getAgentList()) {
						agent.setText(null);
					}
				}
				if (obj.getInvokeList() != null) {
					for(WrapInvoke invoke : obj.getInvokeList()){
						invoke.setText(null);
					}
				}
			}
		}

		return wo;
	}

	private void installCustomApp(String fileName, byte[] bytes) throws Exception{
		Nodes nodes = Config.nodes();
		for (String node : nodes.keySet()){
			if(nodes.get(node).getApplication().getEnable()) {
				logger.print("socket deploy custom app{} to {}:{}",fileName, node, nodes.get(node).nodeAgentPort());
				try (Socket socket = new Socket(node, nodes.get(node).nodeAgentPort())) {
					socket.setKeepAlive(true);
					socket.setSoTimeout(10000);
					try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						 DataInputStream dis = new DataInputStream(socket.getInputStream())) {
						Map<String, Object> commandObject = new HashMap<>();
						commandObject.put("command", "redeploy:customZip");
						commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));

						dos.writeUTF(XGsonBuilder.toJson(commandObject));
						dos.flush();
						dos.writeUTF(fileName);
						dos.flush();

						try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
							byte[] onceBytes = new byte[1024];
							int length = 0;
							while ((length = bis.read(onceBytes, 0, onceBytes.length)) != -1) {
								dos.write(onceBytes, 0, length);
								dos.flush();
							}
						}
					}

				}
			}
		}
	}

	public static class Wo extends WrapBoolean {

	}

	public static class InstallWo extends GsonPropertyObject {

		@FieldDescribe("流程应用")
		private List<String> processPlatformList = new ArrayList<>();

		@FieldDescribe("门户应用")
		private List<String> portalList = new ArrayList<>();

		@FieldDescribe("统计应用")
		private List<String> queryList = new ArrayList<>();

		@FieldDescribe("内容管理应用")
		private List<String> cmsList = new ArrayList<>();

		@FieldDescribe("服务管理应用")
		private List<String> serviceModuleList = new ArrayList<>();

		public List<String> getProcessPlatformList() {
			return processPlatformList;
		}

		public void setProcessPlatformList(List<String> processPlatformList) {
			this.processPlatformList = processPlatformList;
		}

		public List<String> getPortalList() {
			return portalList;
		}

		public void setPortalList(List<String> portalList) {
			this.portalList = portalList;
		}

		public List<String> getQueryList() {
			return queryList;
		}

		public void setQueryList(List<String> queryList) {
			this.queryList = queryList;
		}

		public List<String> getCmsList() {
			return cmsList;
		}

		public void setCmsList(List<String> cmsList) {
			this.cmsList = cmsList;
		}

		public List<String> getServiceModuleList() {
			return serviceModuleList;
		}

		public void setServiceModuleList(List<String> serviceModuleList) {
			this.serviceModuleList = serviceModuleList;
		}
	}

}