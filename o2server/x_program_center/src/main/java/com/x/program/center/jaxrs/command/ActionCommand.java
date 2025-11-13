package com.x.program.center.jaxrs.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import org.apache.commons.lang3.BooleanUtils;

/*执行服务器命令*/
public class ActionCommand extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionCommand.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
			throw new ExceptionCommandDisable();
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String ctl = wi.getCtl();
		String nodeName = wi.getNodeName();
		Wo wo;
		String logInfo = "{} executeCommand {} on node {}";
		if (nodeName.equals("*")) {
			Nodes nodes = Config.nodes();
			// 先其他服务器再当前服务器
			for (String node : nodes.keySet()) {
				if (!node.equalsIgnoreCase(Config.node())) {
					logger.info(logInfo, effectivePerson.getDistinguishedName(), ctl,
							node);
					executeCommand(ctl, node, nodes.get(node).nodeAgentPort());
				}
			}
			logger.info(logInfo, effectivePerson.getDistinguishedName(), ctl,
					Config.node());
			wo = executeCommand(ctl, Config.node(), nodes.get(Config.node()).nodeAgentPort());
		} else {
			logger.info(logInfo, effectivePerson.getDistinguishedName(), ctl, nodeName);
			wo = executeCommand(ctl, nodeName, Integer.parseInt(wi.getNodePort()));
		}

		result.setData(wo);
		return result;
	}

	private synchronized Wo executeCommand(String ctl, String nodeName, int nodePort) {
		Wo wo = new Wo();
		wo.setNode(nodeName);
		wo.setStatus("success");
		try (Socket socket = new Socket(nodeName, nodePort)) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(5000);
			try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					DataInputStream dis = new DataInputStream(socket.getInputStream())) {
				Map<String, Object> commandObject = new HashMap<>();
				commandObject.put("command", "command:" + ctl);
				commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
				dos.writeUTF(XGsonBuilder.toJson(commandObject));
				dos.flush();

				if (ctl.contains("create encrypt")) {
					String createEncrypt = dis.readUTF();
					logger.info(createEncrypt);
				}
			}
		} catch (Exception ex) {
			wo.setStatus("fail");
			logger.warn("socket dispatch executeCommand to {}:{} error={}", nodeName, nodePort, ex.getMessage());
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		wo.setTime(df.format(new Date()));
		return wo;
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("命令名称")
		private String ctl;
		@FieldDescribe("服务器地址(*代表多台应用服务器)")
		private String nodeName;
		@FieldDescribe("服务端口")
		private String nodePort;

		public String getCtl() {
			return ctl;
		}

		public void setCtl(String ctl) {
			this.ctl = ctl;
		}

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public String getNodePort() {
			return nodePort;
		}

		public void setNodePort(String nodePort) {
			this.nodePort = nodePort;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("执行时间")
		private String time;
		@FieldDescribe("执行结束")
		private String status;
		@FieldDescribe("执行服务器")
		private String node;

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

}
