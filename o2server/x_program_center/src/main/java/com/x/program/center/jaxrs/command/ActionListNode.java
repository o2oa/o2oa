package com.x.program.center.jaxrs.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

//获取服器信息列表
class ActionListNode extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListNode.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Nodes nodes = Config.nodes();

		List<NodeInfo> nodeInfoList = new ArrayList<>();

		for (Entry<String, Node> entry : nodes.entrySet()) {
			NodeInfo nodeInfo = new NodeInfo();
			nodeInfo.setNodeAddress(entry.getKey());
			nodeInfo.setNode(entry.getValue());
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
