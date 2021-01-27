package com.x.program.center.jaxrs.command;

import java.util.ArrayList;
import java.util.List;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.config.Node;

//获取服器信息列表
class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String currentIP) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Nodes nodes = Config.nodes();

		if (null == nodes) {
			throw new ExceptionEntityNotExist(currentIP, "Nodes");
		}
		List<NodeInfo> nodeInfoList = new ArrayList<>();

		for (String key : nodes.keySet()) {
			NodeInfo nodeInfo = new NodeInfo();

			if (key.equalsIgnoreCase("127.0.0.1")) {
				nodeInfo.setNodeAddress(currentIP);
			} else {
				nodeInfo.setNodeAddress(key);
			}

			nodeInfo.setNode(nodes.get(key));
			nodeInfoList.add(nodeInfo);
		}

		Wo wo = new Wo();
		wo.setNodeList(nodeInfoList);

		result.setData(wo);
		return result;
	}

	public class NodeInfo {
		private String nodeAddress;
		private Node node;

		public String getNodeAddress() {
			return nodeAddress;
		}

		public void setNodeAddress(String nodeAddress) {
			this.nodeAddress = nodeAddress;
		}

		public Node getNode() {
			return node;
		}

		public void setNode(Node node) {
			this.node = node;
		}
	}

	public static class Wo {

		private List<NodeInfo> nodeList;

		public List<NodeInfo> getNodeList() {
			return nodeList;
		}

		public void setNodeList(List<NodeInfo> nodeList) {
			this.nodeList = nodeList;
		}
	}

}
