package com.x.program.center.jaxrs.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

public class ActionUninstall extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(CommandAction.class);

	public ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String ctl,
			String nodeName, String nodePort, String fileName) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		String curServer = request.getLocalAddr();
		if (nodeName.equalsIgnoreCase("*")) {
			Nodes nodes = Config.nodes();
			logger.info("先其他服务器");
			for (String node : nodes.keySet()) {
				// 先其他服务器
				if (!node.equalsIgnoreCase(curServer)) {
					if (nodes.get(node).getApplication().getEnable() || nodes.get(node).getCenter().getEnable()) {
						logger.info("node=" + node);
						wo = executeCommand(ctl, node, nodes.get(node).nodeAgentPort(), fileName);
					}
				}
			}

			logger.info("后当前服务器");
			for (String node : nodes.keySet()) {
				// 后当前服务器
				if (node.equalsIgnoreCase(curServer)) {
					if (nodes.get(curServer).getApplication().getEnable()
							|| nodes.get(curServer).getCenter().getEnable()) {
						logger.info("node=" + node);
						wo = executeCommand(ctl, node, nodes.get(curServer).nodeAgentPort(), fileName);
					}
				}
			}
		} else {

			wo = executeCommand(ctl, nodeName, Integer.parseInt(nodePort), fileName);
		}

		result.setData(wo);
		return result;
	}

	private Wo executeCommand(String ctl, String nodeName, Integer nodePort, String fileName) {
		// TODO Auto-generated method stub
		Wo wo = new Wo();
		wo.setNode(nodeName);
		wo.setStatus("success");
		try (Socket socket = new Socket(nodeName, nodePort)) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(5000);
			DataOutputStream dos = null;
			DataInputStream dis = null;
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(socket.getInputStream());

				Map<String, Object> commandObject = new HashMap<>();
				commandObject.put("command", "uninstall:" + ctl);
				commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
				dos.writeUTF(XGsonBuilder.toJson(commandObject));
				dos.flush();

				dos.writeUTF(fileName);
				dos.flush();

			} finally {
				dos.close();
				dis.close();
				socket.close();
			}
		} catch (Exception ex) {
			wo.setStatus("fail");
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		wo.setTime(df.format(new Date()));
		return wo;
	}

	public static class Wi extends GsonPropertyObject {
		private static final long serialVersionUID = -4865686912072669195L;
		@FieldDescribe("命令")
		private String ctl;
		@FieldDescribe("节点")
		private String nodeName;
		@FieldDescribe("端口")
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
